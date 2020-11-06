package us.uofm.comp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;

public class ClientListener extends Thread/* implements Runnable */ {

	private MediaPlayer mp = null;
	private InputStream is = null;
	private Socket s = null;

	private InputStreamReader isr = null;
	private Socket ntpSocket = null;
	private String incomingS = null;
	private BufferedReader br = null;

	private NTPUDPClient newtClient;
	private InetAddress inetAddress;
	private volatile TimeInfo timeInfo;
	private volatile Long offset;
	private TimeStamp atomicNtpTime;

	private boolean quit = false;

	private ClientPlayer cplayer = null;

	public ClientListener(Socket s, MediaPlayer mp) {
		this.mp = mp;
		this.s = s;
	}

	@Override
	public void run() {

		cplayer = new ClientPlayer(mp);

		try {
			is = s.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		isr = new InputStreamReader(is);
		br = new BufferedReader(isr);

		cplayer.start();

		newtClient = new NTPUDPClient();
		newtClient.setDefaultTimeout(10_000);
		try {
			inetAddress = InetAddress.getByName("pool.ntp.org");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			quit = true;
		}

		while (!quit) {
			try {
				incomingS = br.readLine();

				String timestamp = getTime();

				//Mon, Nov 02 2020 15:43:51.135
				timestamp = timestamp.split(" ")[4];

				double waitTime = Double.parseDouble(timestamp.split(":")[2]) % 3;

				if (incomingS.contains("play")) {
					waitSync(waitTime);
					cplayer.play();
				} else if (incomingS.contains("pause")) {
					waitSync(waitTime);
					cplayer.pause();
				} else if (incomingS.contains("stop")) {
					waitSync(waitTime);
					cplayer.stopMusic();
				} else if (incomingS.contains("quit")) {
					stopListener();
					continue;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void stopListener() {
		try {
			quit = true;
			if (ntpSocket != null)
				ntpSocket.close();
			if (s != null)
				s.close();
			if (is != null)
				is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void waitSync(double seconds) {
		try {
			int time = (int) (seconds * 1000);
			sleep((long) time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private String getTime() {
		
		String time = null;
		try {

			TimeInfo timeInfo = newtClient.getTime(inetAddress);
			timeInfo.computeDetails();
			if (timeInfo.getOffset() != null) {
				this.timeInfo = timeInfo;
				this.offset = timeInfo.getOffset();
			}
			// Calculating the remote server time. Atomic time
			long currentTime = System.currentTimeMillis();
			atomicNtpTime = TimeStamp.getNtpTime(currentTime + offset);
			String atomicTime = atomicNtpTime.toDateString();
			//System.out.println("Atomic time: " + atomicTime + " Offset: " + this.timeInfo.getOffset() + " Delay: "
			//		+ this.timeInfo.getDelay() + " Return time: " + this.timeInfo.getReturnTime());
			time = atomicTime;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

}

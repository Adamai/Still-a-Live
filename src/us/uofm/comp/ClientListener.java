package us.uofm.comp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;

public class ClientListener extends Thread/* implements Runnable */ {

	private Label lbl_status = null;
	private MediaPlayer mp = null;
	private InputStream is = null;
	private Socket s = null;

	private InputStreamReader isr = null;
	private Socket ntpSocket = null;
	private String incomingS = null;
	private BufferedReader br = null;
	private BufferedReader br2 = null;

	private NTPUDPClient newtClient;
	private InetAddress inetAddress;
	private volatile TimeInfo timeInfo;
	private volatile Long offset;

	private boolean quit = false;

	private ClientPlayer cplayer = null;

	public ClientListener(Socket s, Label lbl_status, MediaPlayer mp) {
		this.mp = mp;
		this.lbl_status = lbl_status;
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
		newtClient.setDefaultTimeout(20_000);
		try {
			inetAddress = InetAddress.getByName("pool.ntp.org");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		while (!quit) {
			try {
				// use a switch?
				incomingS = br.readLine();

				String timestamp = getTime();
				
				//Mon, Nov 02 2020 15:43:51.135
				timestamp = timestamp.split(" ")[4];
				System.out.println("Atomic time is: " + timestamp);
				
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
			//if (seconds == 0) {
			//	seconds++;
			//}
			int time = (int) (seconds * 1000);
			sleep((long)time);
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

			// The current client system time. Should this be used?
			TimeStamp systemNtpTime = TimeStamp.getCurrentTime();
			String sysTime = systemNtpTime.toDateString();
			System.out.println("System time:\t" + sysTime);

			// Calculating the remote server NTP time. Atomic time
			long currentTime = System.currentTimeMillis();
			TimeStamp atomicNtpTime = TimeStamp.getNtpTime(currentTime + offset);
			String atomicTime = atomicNtpTime.toDateString();
			//TEST IF GETTING THE DELAY HELPS
			System.out.println("Atomic time:\t" + atomicTime + "Delay: " + timeInfo.getDelay());
			
			time = atomicTime;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return time;
	}

}

package us.uofm.comp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		
		while (!quit) {
			try {
				//use a switch?
				incomingS = br.readLine();
				
				String timestamp = getTime();
				boolean skip = false;
				
				//WORK ON CORRECTING THE DESYNCHRONIZATION WHEN FAILING TO GET TIME
				Timestamp currJavaTime = new Timestamp(new java.util.Date().getTime());
				while(timestamp == null) {
					waitSync(1);
					System.out.println("Time request failed. Trying again");
					timestamp = getTime();
					skip = true;
				}
				Timestamp newJavaTime = new Timestamp(new java.util.Date().getTime());
				double msToSkip = newJavaTime.getTime() - currJavaTime.getTime();
				
				
				timestamp = timestamp.split(" ")[2];
				System.out.println("Nist says the time is: " + timestamp);
				int waitTime = Integer.parseInt(timestamp.split(":")[2]) % 3;
				waitSync(waitTime);
				
				if (incomingS.contains("play")) {
					if(skip)
						cplayer.skipPlay(msToSkip);
					else
						cplayer.play();
				} else if(incomingS.contains("pause")) {
					cplayer.pause();
				} else if(incomingS.contains("stop")) {
					cplayer.stopMusic();
				} else if(incomingS.contains("quit")) {
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
	
	private static void waitSync(int seconds) {
		try {
			if(seconds == 0) {
				seconds++;
			}
			sleep(seconds + 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private String getTime() {
		String time = null;
		try {
			ntpSocket = new Socket("time.nist.gov", 13);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			BufferedReader br2 = new BufferedReader(new InputStreamReader(ntpSocket.getInputStream()));
			br2.readLine();
			time = br2.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return time;
	}

}

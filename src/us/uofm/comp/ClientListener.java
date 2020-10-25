package us.uofm.comp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;

public class ClientListener implements Runnable {

	private Label lbl_status = null;
	private MediaPlayer mp = null;
	private InputStream is = null;
	private Socket s = null;

	private DataInputStream dIn = null;
	private Socket ntpSocket = null;

	public ClientListener(Socket s, Label lbl_status, MediaPlayer mp) {
		this.mp = mp;
		this.lbl_status = lbl_status;
		this.s = s;
	}

	@Override
	public void run() {

		try {
			is = s.getInputStream();
			dIn = new DataInputStream(is);
			String incomingUTF = dIn.readUTF();
			if (incomingUTF != null) {
				System.out.println("lets");
				if (incomingUTF.contains("play")) {
					System.out.println("go!");
					mp.play();
				}
			}

			// then use the clock as reference to start playing at a specific time (next
			// second multiple of 4?)
			ntpSocket = new Socket("132.163.96.3", 13);
			BufferedReader br = new BufferedReader(new InputStreamReader(ntpSocket.getInputStream()));
			br.readLine();
			String timestamp = br.readLine();
			System.out.println("Nist says the time is: " + timestamp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ntpSocket != null)
					ntpSocket.close();
				if (s != null)
					s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}

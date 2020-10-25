package us.uofm.comp;

import java.net.*;

import javafx.application.Application;

import java.io.*;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Client extends Application {

	public static int port = 5999;
	public static String server = "127.0.0.1"; // REMEMBER TO MODIFY HERE WHEN TESTING WITH ANOTHER MACHINE!

	public static String fileSave = "songDownloaded.mp3";
	public static int fileSize = 20000000; // is this necessary?
	
	public static Label lbl_status = null;
	public static MediaPlayer mp = null;
	public static InputStream is = null;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		int bytesRead = 0;
		int currentBytes = 0;

		Socket s = null;
		Socket ntpSocket = null;
		DataInputStream dIn = null;
		ObjectInputStream ois = null;

		System.out.println("Looking for host...");

		Media musicFile;

		try {

			s = new Socket(server, port);
			is = s.getInputStream();
			
			ois = new ObjectInputStream(is);
			
			FileContainer fc = (FileContainer) ois.readObject();
			
			fileSize = fc.getSize();
			fileSave = "downloaded_"+fc.getFilename();
			
			
			byte byteArray[] = new byte[fileSize];

			fos = new FileOutputStream(fileSave);
			bos = new BufferedOutputStream(fos);

			//bytesRead = is.read(byteArray, 0, byteArray.length);
			//currentBytes = bytesRead;

			/*System.out.println("Going to download file");
			
			do {
				bytesRead = is.read(byteArray, currentBytes, (byteArray.length - currentBytes));
				System.out.println(bytesRead);
				if (bytesRead >= 0) {
					currentBytes += bytesRead;
					System.out.print("A");
				}
			} while (bytesRead > -1);*/
			
			System.out.println("Finished getting file data. Writing it to a file");
			//bos.write(byteArray, 0, currentBytes);
			bos.write(fc.getData(), 0, fileSize);
			bos.flush();

			System.out.println("File downloaded: " + fileSave + " (" + fileSize + " bytes received)");

			File file = new File(fileSave);
			musicFile = new Media(file.toURI().toURL().toString());
			
			

			lbl_status = new Label("File downloaded: " + fileSave);


			mp = new MediaPlayer(musicFile);
			// mp.setAutoPlay(true);
			mp.setVolume(0.5);

			VBox root = new VBox();
			root.getChildren().addAll(lbl_status);

			Scene scene = new Scene(root, 500, 500);
			stage.setTitle("Cliente");
			stage.setScene(scene);

			stage.show();
			
			ClientListener cl = new ClientListener(s, lbl_status, mp);
			System.out.println("what the actual f");
			cl.run();
			System.out.println("what the actual f");
			
			//create a new thread and put everything that should happen next there. Nothing else should happen after stage.show above
			


		} finally {
			if (fos != null)
				fos.close();
			if (bos != null)
				bos.close();
		}
		
		
	}
	
	private void loadMusic() { //implement here the method to load another music file from host
		
	}
	
}

package us.uofm.comp;

import java.net.*;

import javafx.application.Application;

import java.io.*;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Client extends Application {
	
	public static int port = 3462;
	public static String server = "127.0.0.1";
	
	public static String fileSave = "songDownloaded.mp3";
	public static int fileSize = 9999999;	//is this necessary?
	
	MediaPlayer mp;
	
	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage stage) throws Exception {
		
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		InputStream is = null;
		
		int bytesRead = 0;
		int currentBytes = 0;
		
		Socket s = null;
		
		System.out.println("Looking for host...");
		
		Media musicFile;
		
		try {
			
			s = new Socket("localhost", port);
			
			byte byteArray[] = new byte[fileSize];
			is = s.getInputStream();
			
			fos = new FileOutputStream(fileSave);
			bos = new BufferedOutputStream(fos);
			
			bytesRead = is.read(byteArray, 0, byteArray.length);
			currentBytes = bytesRead;
			
			System.out.println();
			
			do {
				bytesRead = is.read(byteArray, currentBytes, (byteArray.length - currentBytes));
				
				if(bytesRead >= 0) {
					currentBytes += bytesRead;
				}
			} while(bytesRead > -1);
			System.out.println("BBBBBBBBBBB");
			bos.write(byteArray, 0, currentBytes);
			bos.flush();
			
			System.out.println("File downloaded: " + fileSave + " (" + currentBytes + " bytes received)");
			
			File file = new File (fileSave);
			musicFile = new Media(file.toURI().toURL().toString());
			
			
			
			PrintWriter pr = new PrintWriter(s.getOutputStream());
			pr.println("hola");
			pr.flush();
			
			InputStreamReader in = new InputStreamReader(s.getInputStream());
			BufferedReader bf = new BufferedReader(in);
			
			String str = bf.readLine();
			System.out.println("Server: " + str);
			
		} finally {
			if (s != null) s.close();
			if (fos != null) fos.close();
		    if (bos != null) bos.close();
		}
		
		//-----------------------------
		
Button btn_play, btn_pause, btn_stop;
		
		btn_play = new Button("Play");
		btn_pause = new Button("Pause");
		btn_stop = new Button("Stop");
		
		btn_play.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				mp.play();
			}
		});
		btn_pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				mp.pause();
			}
		});
		btn_stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				mp.stop();
			}
		});
		
		mp = new MediaPlayer(musicFile);
		//mp.setAutoPlay(true);
		mp.setVolume(0.5);
		
		VBox root = new VBox();
		root.getChildren().addAll(btn_play, btn_pause, btn_stop);
		
		Scene scene = new Scene(root, 500, 500);
		stage.setScene(scene);
		
		stage.show();
	}
}

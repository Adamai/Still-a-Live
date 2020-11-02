package us.uofm.comp;

import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;

public class Host extends Application {

	public static String fileP = "test.mp3";

	public static int port = 5999;

	public static String lock = "";
	public volatile static ArrayList<String> sharedS = new ArrayList<String>();
	
	private int clientCounter = 1; 
	private Scene scene = null;

	public static void main(String[] args) throws IOException {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		ServerSocket ss = new ServerSocket(port);

		System.out.println("This is the HOST.\nAttempting to find client...");

		Button btn_play, btn_pause, btn_stop, btn_addClient, btn_newFile;

		btn_play = new Button("Play");
		btn_pause = new Button("Pause");
		btn_stop = new Button("Stop");
		btn_addClient = new Button("Add new client");
		btn_newFile = new Button("Select mp3 file");

		btn_play.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				synchronized (sharedS) {
					sharedS.add("play");
				}
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		});
		btn_pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				synchronized (sharedS) {
					sharedS.add("pause");
				}
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		});
		btn_stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				synchronized (sharedS) {
					sharedS.add("stop");
				}
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		});
		btn_addClient.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Socket s;
				try {
					s = ss.accept();
					new Thread(new WorkerHost(s, "Client " + clientCounter, fileP, lock, sharedS)).start();
					clientCounter++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btn_newFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
				File file2 = fc.showOpenDialog(scene.getWindow());
				if(file2.getAbsolutePath() != null)
					fileP = file2.getAbsolutePath();
			}
		});

		VBox root = new VBox();
		root.getChildren().addAll(btn_newFile, btn_addClient, btn_play, btn_pause, btn_stop);

		scene = new Scene(root, 500, 500);
		stage.setScene(scene);
		stage.setTitle("Host");
		stage.show();

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				try {
					synchronized (sharedS) {
						sharedS.add("quit");
					}
					synchronized (lock) {
						lock.notifyAll();
					}
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

}

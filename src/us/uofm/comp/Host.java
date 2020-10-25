package us.uofm.comp;

import java.net.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class Host extends Application {

	public static String fileP = "test.mp3";

	public static int port = 5999;
	
	Thread workerSpider;
	Object lock;

	public static void main(String[] args) throws IOException {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		ServerSocket ss = new ServerSocket(port);

		Socket s = null;

		System.out.println("This is the HOST.\nAttempting to find client...");

		
		lock = new Object();
			

		Button btn_play, btn_pause, btn_stop, btn_addClient;

		btn_play = new Button("Play");
		btn_pause = new Button("Pause");
		btn_stop = new Button("Stop");
		btn_addClient = new Button("Add new client");

		btn_play.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				synchronized(lock) { lock.notifyAll(); }
			}
		});
		btn_pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

			}
		});
		btn_stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

			}
		});
		btn_addClient.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Socket s;
				try {
					s = ss.accept();
					new Thread(new WorkerHost(s, "Cliente", fileP, lock)).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		VBox root = new VBox();
		root.getChildren().addAll(btn_play, btn_pause, btn_stop, btn_addClient);

		Scene scene = new Scene(root, 500, 500);
		stage.setScene(scene);
		stage.setTitle("Host");
		stage.show();

	}

}

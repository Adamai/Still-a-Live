package us.uofm.comp;

import java.net.*;

import javafx.application.Application;

import java.io.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Client extends Application {

	public static int port = 5999;
	public static String server = "127.0.0.1"; // REMEMBER TO MODIFY HERE WHEN TESTING WITH ANOTHER MACHINE!

	public static String fileSave = "songDownloaded.mp3";
	public static int fileSize = 20000000; // is this necessary?

	public static Label lbl_status = null;
	public static MediaPlayer mp = null;
	public static InputStream is = null;

	private Socket s = null;
	private ObjectInputStream ois = null;
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	private Media musicFile;
	private Button btn_connect;
	private TextField txt_ip;
	private FileContainer fc;
	private File file;
	private ClientListener cl;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		try {
			btn_connect = new Button("Connect");
			txt_ip = new TextField();
			lbl_status = new Label("Type an ip and connect ");

			btn_connect.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					if (!txt_ip.getText().isEmpty())
						server = txt_ip.getText();
					try {

						s = new Socket(server, port);
						is = s.getInputStream();

						ois = new ObjectInputStream(is);

						fc = (FileContainer) ois.readObject();

						fileSize = fc.getSize();
						fileSave = "downloaded_" + fc.getFilename();

						fos = new FileOutputStream(fileSave);
						bos = new BufferedOutputStream(fos);

						// bos.write(byteArray, 0, currentBytes);
						bos.write(fc.getData(), 0, fileSize);
						bos.flush();

						file = new File(fileSave);
						musicFile = new Media(file.toURI().toURL().toString());

						lbl_status.setText("File downloaded: " + fileSave);

						mp = new MediaPlayer(musicFile);
						// mp.setAutoPlay(true);
						mp.setVolume(0.5);
						
						cl = new ClientListener(s, mp);
						cl.start();						

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			VBox root = new VBox();
			root.getChildren().addAll(lbl_status, txt_ip, btn_connect);

			Scene scene = new Scene(root, 500, 500);
			stage.setTitle("Client");
			stage.setScene(scene);

			stage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				try {
					if (fos != null)
						fos.close();
					if (bos != null)
						bos.close();
					if(ois != null)
						ois.close();
					if(s != null)
						s.close();
					if(cl != null)
						cl.stopListener();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}


}

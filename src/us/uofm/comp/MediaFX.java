package us.uofm.comp;

import java.io.File;
import java.net.MalformedURLException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MediaFX extends Application{
	
	MediaPlayer mp;
	
	public MediaFX() throws Exception {
		//constructor
	}
	
	public Boolean setMedia(String fileName) throws Exception {
		if(!fileName.contains(".mp3"))
			return false;
		File file = new File (fileName);
		Media musicFile = new Media(file.toURI().toURL().toString());
		mp = new MediaPlayer(musicFile);
		mp.setVolume(0.5);
		return true;
	}
	
	public void addVolume() {
		if(mp.getVolume() != 1.0)
			mp.setVolume(mp.getVolume()+0.1);
	}
	public void reduceVolume() {
		if(mp.getVolume() != 0.0)
			mp.setVolume(mp.getVolume()-0.1);
	}
	
	public void play() {
		mp.play();
	}
	public void pause() {
		mp.pause();
	}
	public void stop() {
		mp.stop();
	}

	//public static void main(String[] args) {
	//	launch(args);
	//}
	
	@Override
	public void start(Stage stage) throws Exception {
		
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
		
		File file = new File ("test.mp3");
		
		Media musicFile = new Media(file.toURI().toURL().toString());
		
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

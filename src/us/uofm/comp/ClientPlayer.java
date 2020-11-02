package us.uofm.comp;

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class ClientPlayer extends Thread {

	private MediaPlayer mp = null;
	
	public ClientPlayer(MediaPlayer mp) {
		this.mp = mp;
	}
	
	@Override
	public void run() {
		//mp.play();
	}
	
	public void play() {
		mp.play();
	}
	
	public void stopMusic() {
		mp.stop();
	}
	
	public void pause() {
		mp.pause();
	}
	
	public void skipPlay(double ms) {
		mp.play();
		double newTime = mp.getCurrentTime().toMillis() + ms;
		mp.seek(new Duration(newTime));
	}

}

package us.uofm.comp;

import org.apache.commons.net.ntp.TimeStamp;

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
		//TimeStamp systemNtpTime = TimeStamp.getCurrentTime();
		//String sysTime = systemNtpTime.toDateString();
		//System.out.println("I'M PLAYING AT THIS TIME:\t" + sysTime);
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

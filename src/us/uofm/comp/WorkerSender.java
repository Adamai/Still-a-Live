package us.uofm.comp;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class WorkerSender implements Runnable {

	protected ObjectOutputStream oos = null;
	protected FileContainer fc = null;
	
	public WorkerSender(ObjectOutputStream oos, FileContainer fc) {
		this.oos = oos;
		this.fc = fc;
	}
	
	@Override
	public void run() {
		if(oos != null) {
			try {
				oos.writeObject(fc);
				oos.flush();
				System.out.println("sender is done!");
				//outputS.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

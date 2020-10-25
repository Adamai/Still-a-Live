package us.uofm.comp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WorkerHost implements Runnable {

	protected Socket clientSocket = null;
	protected String clientName = null;
	protected String fileName = null;
	protected FileInputStream fis = null;
	protected BufferedInputStream bis = null;
	protected OutputStream outputS = null;
	protected ObjectOutputStream oos = null;
	protected Object lock = null;
	protected DataOutputStream dOut = null;

	public WorkerHost(Socket clientSocket, String clientName, String fileName, Object lock) {
		this.clientSocket = clientSocket;
		this.clientName = clientName;
		this.fileName = fileName;
		this.lock = lock;
	}

	public void run() {
		System.out.println("Client connected! Connected to: " + clientName);

		File file = new File(fileName);
		
		FileContainer fc = new FileContainer();
		

		byte[] bytearray = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);
			outputS = clientSocket.getOutputStream();
			oos = new ObjectOutputStream(outputS);
			fc.setFilename(fileName);
			fc.setData(bytearray);
			fc.setSize((int)file.length());

			System.out.println("Sending file: " + file + "(" + bytearray.length + "bytes)");
			
			//outputS.close();
			new Thread(new WorkerSender(oos, fc)).run();

			System.out.println("File sent. Now I'm going to wait until play is pressed");
			// Wait for order from Host class to play
			
			synchronized (lock) {
				lock.wait();
			}
			System.out.println("CORRE VADIA");
			// CONTINUE HERE: SEND MESSAGE TO CLIENT PLAY MUSIC
			dOut = new DataOutputStream(outputS);
			dOut.writeUTF("play");
			dOut.flush();
			
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null)
					bis.close();
				//if (outputS != null)
				//	outputS.close();
				if (clientSocket != null)
					clientSocket.close();
				if(dOut != null)
					dOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}

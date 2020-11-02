package us.uofm.comp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;

public class WorkerHost implements Runnable {

	protected Socket clientSocket = null;
	protected String clientName = null;
	protected String fileName = null;
	protected FileInputStream fis = null;
	protected BufferedInputStream bis = null;
	protected OutputStream outputS = null;
	protected ObjectOutputStream oos = null;
	protected Object lock = null;
	protected ArrayList<String> sharedS = null;

	public WorkerHost(Socket clientSocket, String clientName, String fileName, Object lock, ArrayList<String> sharedS) {
		this.clientSocket = clientSocket;
		this.clientName = clientName;
		this.fileName = fileName;
		this.lock = lock;
		this.sharedS = sharedS;
	}

	public void run() {
		System.out.println("Client connected! Connected to: " + clientName);

		File file = new File(fileName);
		
		FileContainer fc = new FileContainer();
		System.out.println(fileName);
		String[] pathSplit = fileName.split("\\\\");
		fc.setFilename(pathSplit[pathSplit.length - 1]);
		

		byte[] bytearray = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);
			outputS = clientSocket.getOutputStream();
			oos = new ObjectOutputStream(outputS);
			//fc.setFilename(fileName);
			fc.setData(bytearray);
			fc.setSize((int)file.length());

			System.out.println("Sending file: " + file + "(" + bytearray.length + "bytes)");
			
			//outputS.close();
			new Thread(new WorkerSender(oos, fc)).run();

			System.out.println("File sent. Now I'm going to wait until play is pressed");
			// Wait for order from Host class to play
			
			boolean quit = false;
			PrintWriter pw = new PrintWriter(outputS, true);
			
			while(!quit) {
				String inCommand = "";
				synchronized (lock) {
					lock.wait();
				}
				synchronized(sharedS) {
					inCommand = sharedS.get(sharedS.size()-1);
				}
				System.out.println("Worker detected the command: " + inCommand);
				
				if(inCommand.equals("quit")) {
					pw.println(inCommand);
					quit = true;
				} else{
					pw.println(inCommand);
				}
				
				
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (clientSocket != null)
					clientSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}

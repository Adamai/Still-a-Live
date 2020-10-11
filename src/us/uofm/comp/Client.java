package us.uofm.comp;

import java.net.*;
import java.io.*;

public class Client {
	
	public static int port = 4999;
	public static String server = "127.0.0.1";
	
	public static String fileSave = "songDownloaded.mp3";
	public static int fileSize = 888888;	//is this necessary?
	
	
	public static void main(String[] args) throws Exception {
		
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		InputStream is = null;
		
		int bytesRead = 0;
		int currentBytes = 0;
		
		Socket s = null;
		
		try {
		
			s = new Socket("localhost", port);
			System.out.println("Looking for host...");
			
			byte byteArray[] = new byte[fileSize];
			is = s.getInputStream();
			
			fos = new FileOutputStream(fileSave);
			bos = new BufferedOutputStream(fos);
			
			bytesRead = is.read(byteArray, 0, byteArray.length);
			currentBytes = bytesRead;
			
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
			
			MediaFX mfx = new MediaFX();
			if(mfx.setMedia(fileSave))
				mfx.play();
			
			
			
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
		
		
	}
}

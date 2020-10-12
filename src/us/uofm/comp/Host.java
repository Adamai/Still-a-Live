package us.uofm.comp;

import java.net.*;
import java.io.*;


public class Host {
	
	public static String fileP = "test.mp3";
	
	public static int port = 3462;
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket ss = new ServerSocket(port);
		
		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream outputS = null;
	    Socket s = null;
	    
		//while(true) {
			System.out.println("Attempting to find client...");
			
			try {
				s = ss.accept();
				
				System.out.println("Client connected! Connected to: " + s);
				
				File file = new File (fileP);
				
				byte [] bytearray  = new byte [(int)file.length()];
				
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				bis.read(bytearray,0,bytearray.length);
				outputS = s.getOutputStream();
				
				System.out.println("Sending file: " + file + "(" + bytearray.length + "bytes)");
				
				outputS.write(bytearray,0,bytearray.length);
				outputS.flush();
				
				
				
				
				//InputStreamReader in = new InputStreamReader(s.getInputStream());
				//BufferedReader bf = new BufferedReader(in);
				
				//String str = bf.readLine();
				//System.out.println("Client: " + str);
				
				//PrintWriter pr = new PrintWriter(s.getOutputStream());
				//pr.println("que pasa? Yo soy el host");
				//pr.flush();
				
			}
			finally{
				if (bis != null) bis.close();
		        if (outputS != null) outputS.close();
		        if (s !=null) s.close();
		        if (ss !=null) ss.close();
			}
			
		//}
		
		
		
		
	}
}

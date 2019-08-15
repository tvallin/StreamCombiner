package com.projects.streamcombiner;

import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/*
 * ClientConnection is the Client sending request to the server in order to get xml stream
 */
public class ClientConnection implements Runnable{
	
	private Socket connexion = null;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;
	private int numberXMLRequest = 2;
	private List<String> serverReplyList = Collections.synchronizedList(new ArrayList<String>());
	   
	private static int count = 0;
	private String name = "Client-";   
	   
	   public ClientConnection(String host, int port){
	      name += ++count;
	      try {
	         connexion = new Socket(host, port);
	      } catch (UnknownHostException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
	   }
	   
	   /*
	    * Goal : Get xml Stream from the server and gather it into serverReplyList list
	    */
	   public void run(){
		   
		   for(int i = 0; i < numberXMLRequest; i++){
  	         
			   try {   
				   
				   reader = new BufferedInputStream(connexion.getInputStream());

				   try {
					   //Send command to server
					   writer = new PrintWriter(connexion.getOutputStream(), true);
					   writer.write("GETXML");
					   writer.flush();
		            } catch (Exception e) {
		            	//Write error into log file
		            	FileWriter writer = new FileWriter(System.getProperty("user.dir") + "log.txt");
		            	String debug = "";
			            debug = "Thread : " + Thread.currentThread().getName() + ". ";
			            debug += "\t Client : " + name + "\n";
			            debug += "\t -> Command sent : GETXML \n";
		                writer.write(debug);
		                writer.close();
		            } 
				   
				   //Wait for respond
				   String response = read();
				   serverReplyList.add(response);
		    	            
			   } catch (IOException e1) {
				   e1.printStackTrace();
			   }
		    	         
			   try {
				   Thread.currentThread().sleep(1000);
			   } catch (InterruptedException e) {
		    	            e.printStackTrace();
			   }
		   }
		    	      
		   //Close the connection
		   writer.write("END");
           writer.flush();
		   writer.close();
	   }
	   
	   /*
	    * Goal : Read answer from Server
	    * 
	    * @Return : Server answer as a String
	    */
	   private String read() throws IOException{      
	      String response = "";
	      int stream;
	      byte[] b = new byte[4096];
	      stream = reader.read(b);
	      response = new String(b, 0, stream);      
	      return response;
	   }
	   
	   /*
	    * @Return : return the xml list from this client
	    */
	   public List<String> getList() {
		   return this.serverReplyList;
	   }
}

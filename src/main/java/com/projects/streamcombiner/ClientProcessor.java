package com.projects.streamcombiner;

import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ClientProcessor implements Runnable{
	private Socket sock;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;
	   
	   public ClientProcessor(Socket pSock){
	      sock = pSock;
	   }
	   
	   public void run(){

	      boolean closeConnexion = false;
	      //While active connection, treat the command
	      while(!sock.isClosed()){
	         
	         try {
	            
	            reader = new BufferedInputStream(sock.getInputStream());
	            
	            //Wait for client command           
	            String response = read();
	            
	            String toSend = "";
	            String timeStamp = "";
	            String amount = "";
	            
	            switch(response.toUpperCase()){
	               case "GETXML":
	            	  timeStamp = buildTS();
	            	  amount = buildAmount();
	            	  toSend = buildXML(timeStamp,amount);
	                  break;
	               case "END":
	                  toSend = "End of the communication"; 
	                  closeConnexion = true;
	                  break;
	               default :
	                  toSend = "Unknown command !";                     
	                  break;
	            }
	            
	            
	            try {
	            	//Send the response to the client
	            	writer = new PrintWriter(sock.getOutputStream());
	            	writer.write(toSend);
	            	writer.flush();
	            } catch (Exception e) {
	            	//Write error into log file
	            	FileWriter writer = new FileWriter(System.getProperty("user.dir") + "log.txt");
	            	InetSocketAddress remote = (InetSocketAddress)sock.getRemoteSocketAddress();
	            	String debug = "";
		            debug = "Thread : " + Thread.currentThread().getName() + ". ";
		            debug += "Address : " + remote.getAddress().getHostAddress() +".";
		            debug += "On port : " + remote.getPort() + ".\n";
		            debug += "\t -> Command received : " + response + "\n";
	                writer.write(debug);
	                writer.close();
	            }
	            
	            
	            if(closeConnexion){
	               writer = null;
	               reader = null;
	               sock.close();
	               break;
	            }
	            
	         }catch(SocketException e){
	            System.err.println("Connection interrupted !");
	            break;
	         } catch (IOException e) {
	            e.printStackTrace();
	         }         
	      }
	   }

	   //Read Command
	   private String read() throws IOException{      
	      String response = "";
	      int stream;
	      byte[] b = new byte[4096];
	      stream = reader.read(b);
	      response = new String(b, 0, stream);
	      return response;
	   }
	   
	   //Creates the timestamp
	   private String buildTS() {
		   Date date = new Date();
		   return Long.toString(date.getTime());
	   }
	   
	   //Creates the amount in [-10000;10000] range
	   private String buildAmount() {
		   Random rand = new Random();
		   return Double.toString((rand.nextDouble()*((10000+10000)+1))-10000);
	   }
	   
	   //Creates the XML string
	   private String buildXML(String timeStamp, String amount) {
		   return new StringBuilder("<data> <timeStamp>" + timeStamp + "</timeStamp> <amount>" + amount + "</amount> </data>").toString();
	   }
}

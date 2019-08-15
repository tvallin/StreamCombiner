package com.projects.streamcombiner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * Server class
 * 
 * Goal : Manage Client connection
 */
public class Server {
	
	//Count the number of Server created
	private static int count = 0;
	//port and hort value by default
	private int port = 2345;
	private String host = "127.0.0.1";
	private ServerSocket server = null;
	private boolean isRunning = true;
	private String name = "Server-"; 
	   
	   public Server(){
	      try {
	         server = new ServerSocket(port, 100, InetAddress.getByName(host));
	      } catch (UnknownHostException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
	   }
	   
	   public Server(String pHost, int pPort){
	      host = pHost;
	      port = pPort;
	      name += ++count;
	      try {
	         server = new ServerSocket(port, 100, InetAddress.getByName(host));
	      } catch (UnknownHostException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
	   }
	   
	   /*
	    * Goal : Start the server process into a new Thread
	    * 
	    * Process : Wait for client connection.
	    * 			Once connection made, treat the request from client into a new thread
	    */
	   public void open(){
	      
	      Thread t = new Thread(new Runnable(){
	         public void run(){
	            while(isRunning == true){
	               
	               try {
	                  //Wait for client connection
	                  Socket client = server.accept();
	                  
	                  //Once received, treat client connection
	                  System.out.println("Client connection received : " + name);                  
	                  Thread t = new Thread(new ClientProcessor(client));
	                  t.start();
	                  
	               } catch (IOException e) {
	                  e.printStackTrace();
	               }
	            }
	            
	            try {
	               server.close();
	            } catch (IOException e) {
	               e.printStackTrace();
	               server = null;
	            }
	         }
	      });
	      
	      t.start();
	   }
	   
	   /*
	    * Goal : When called, end the server process
	    */
	   public void close(){
	      isRunning = false;
	   }   
}

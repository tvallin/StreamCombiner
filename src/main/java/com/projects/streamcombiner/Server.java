package com.projects.streamcombiner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
	private static int count = 0;
	
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
	   
	   public void close(){
	      isRunning = false;
	   }   
}

package com.projects.streamcombiner;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Streamcombiner {
	
	public static void main( String[] args ) throws InterruptedException
    {
		//Number of server
		int N = 1;
        String host = "127.0.0.1";
        int port = 2347;
        
        File logFile = new File(System.getProperty("user.dir") + "log.txt");
        		
        try {
			if (logFile.createNewFile())
			{
			    System.out.println("Log File is created!");
			} else {
			    System.out.println("Log File already exists.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        //Contain all xml data from servers
        List<String> xmlData = Collections.synchronizedList(new ArrayList<String>());
        //Contain Json data from POJO
        List<String> jsonList =  new ArrayList<String>();
        //Contain all clients
        List<ClientConnection> clientConnectionList = new ArrayList<ClientConnection>();
        //Contain all Thread where Servers and Clients run
        List<Thread> clientThread = new ArrayList<Thread>();
        //Contain POJOs
        List<Data> pojoList = new ArrayList<Data>();

        //Creates N Server, Client and make them run on several Thread gathered into clientThread array
        for(int i = 0; i < N; i++){
        	Server ts = new Server(host, port);
            ts.open();
            //System.out.println("Server initialized");
            ClientConnection cc = new ClientConnection(host, port);
            clientConnectionList.add(cc);
            Thread t = new Thread(cc);
            clientThread.add(t);
            t.start();
            port++;
        }
        
        //Wait all Thread to finish gathering xml
        for(int j = 0; j < N; j++)
        {
        	clientThread.get(j).join();
        }
        
        //Gather all xml into a unique list
        for(int  k = 0; k < N; k++)
        {
        	xmlData.addAll(clientConnectionList.get(k).getList());
        }
        
        //Convert xml to POJO
        toPOJO(pojoList, xmlData);
        
        //Merge amount for same timestamp
        checkEqualTimeStamp(pojoList);
        
        //Sort the list thanks to timestamp
        sortPOJOList(pojoList);
        
        //Convert POJO to JSON
        toJSON(pojoList,jsonList);
        
        //Print Result
        printList(jsonList);
    }
	
	public static void checkEqualTimeStamp(List<Data> pojoList)
	{
		for (int i = 0; i < pojoList.size() - 1; i++) {
			for(int j = i;  j < pojoList.size(); j++)
			{
				if(i != j)
				{
					if (pojoList.get(i).getTimeStamp() == pojoList.get(j).getTimeStamp()) {
						pojoList.get(i).setAmount(pojoList.get(i).getAmount() + pojoList.get(j).getAmount());
						pojoList.remove(j);
		            }
				}
			}
        }
	}
	
	public static void toPOJO (List<Data> pPojoList, List<String> pXmlData) {
		
		for (int i = 0; i < pXmlData.size(); i++) {
			String[] tokens = pXmlData.get(i).split("[><]+");
			Data data = new Data();
			
			for(int j = 0; j < tokens.length; j++)
			{
				if(tokens[j].contentEquals("timeStamp") && (j+1) < tokens.length)
				{
					data.setTimeStamp(Long.parseLong(tokens[j+1]));
				}
				
				if(tokens[j].contentEquals("amount") && (j+1) < tokens.length)
				{
					data.setAmount(Double.parseDouble(tokens[j+1]));
				}
			}
			
			pPojoList.add(data);
        }
	}
	
	public static void sortPOJOList(List<Data> pojoList)
	{
		boolean sorted = false;
	    Data temp;
	    while(!sorted) {
	        sorted = true;
	        for (int i = 0; i < pojoList.size() - 1; i++) {
	            if (pojoList.get(i).getTimeStamp() > pojoList.get(i+1).getTimeStamp()) {
	                temp = pojoList.get(i);
	                pojoList.add(i,pojoList.get(i+1));
	                pojoList.add(i+1,temp);
	                sorted = false;
	            }
	        }
	    }
	}
	
	public static void toJSON(List<Data> pojoList, List<String> jsonList)
	{
		
		for(int i = 0; i < pojoList.size(); i++)
		{
			jsonList.add(new StringBuilder("{ \"data\": { \"timestamp\":").append(String.valueOf(pojoList.get(i).getTimeStamp())).append(", \"amount\":\"").append(String.valueOf(pojoList.get(i).getAmount())).append("\" }}").toString());
		}
	}
	
	public static void printList(List<String> pList)
	{
		System.out.println("Print list :");
		for(int i = 0; i < pList.size(); i++)
        {
        	System.out.println(pList.get(i));
        }
	}
}
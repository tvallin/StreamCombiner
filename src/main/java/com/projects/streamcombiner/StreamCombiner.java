package com.projects.streamcombiner;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.IOException;

/*
 * Main project Class
 * 		Goal : Gather xml stream from N server.
 * 		       Sort it by Timestamp and merge amount for equals Timestamp.
 * 		       Output the data in JSON format.
 * 
 * 		Step 1 : Generate N server and Client (Each Server/Client on separated thread)
 * 		Step 2 : Client(s) are collecting xml stream from sever(s)
 * 		Step 3 : Wait till all Thread are done
 * 		Step 4 : Gather all xml data from all clients into one single list of xml data
 * 		Step 5 : Convert the xml into POJO (class Data)
 * 		Step 6 : Check if there is equal timestamp, if yes : merge the amount
 * 		Step 7 : Sort the list by timestamp
 * 		Step 8 : Convert POJO into JSON
 */
public class StreamCombiner {
	
	public static void main( String[] args ) throws InterruptedException
	{
		//N: Number of server generated can be changed here
		int N = 2;
		String host = "127.0.0.1";
		int port = 2350;
        
		//A log file is create to gather all error during Client/Server process
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
		//Contains all xml data from servers
		List<String> xmlData = Collections.synchronizedList(new ArrayList<String>());
		//Contains Json data from POJO
		List<String> jsonList =  new ArrayList<String>();
		//Contains all clients
		List<ClientConnection> clientConnectionList = new ArrayList<ClientConnection>();
		//Contains all Thread where Servers and Clients run
		List<Thread> clientThread = new ArrayList<Thread>();
		//Contains POJOs
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
        
		//Wait all Clients to finish gathering xml
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
	
	/*
	 * Goal : Merge amount for equal timestamp
	 * 
	 * Details :
	 * 	Reach all element from the list to compare them.
	 * 	If same timestamp, the amount is merge and the element without the merged amount is removed from the list
	 * 
	 * @Param pojoList : List of Data object containing Timestamp and amount
	 */
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
	
	/*
	 * Goal : Convert Xml data into Data objects
	 * 
	 * @Param pPojoList : Output list of Data object
	 * @Param pXmlData  : Input list with
	 */
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
	
	/*
	 * Goal : Sort the POJO list by timestamp
	 * 
	 * Details : The bubble sort is used inside the method. 
	 * 
	 * @Param pojoList : List of Data objects
	 */
	public static void sortPOJOList(List<Data> pojoList)
	{
		boolean sorted = false;
		Data temp;
		while(!sorted) {
			sorted = true;
			for (int i = 0; i < pojoList.size() - 1; i++) {
				if (pojoList.get(i).getTimeStamp() > pojoList.get(i+1).getTimeStamp()) {
					temp = pojoList.get(i);
					pojoList.remove(i);
					pojoList.add(i+1,temp);
					sorted = false;
				}
			}
		}
	}
	
	/*
	 * Goal : Convert the Data object list into Json string list
	 * 
	 * @Param pojoList : Input Data object list
	 * @Param jsonList : Output String list with Json format
	 */
	public static void toJSON(List<Data> pojoList, List<String> jsonList)
	{
		for(int i = 0; i < pojoList.size(); i++)
		{
			jsonList.add(new String("{ \"data\": { \"timestamp\":" + String.valueOf(pojoList.get(i).getTimeStamp()) + ", \"amount\":\"" + String.valueOf(pojoList.get(i).getAmount()) + "\" }}"));
		}
	}
	
	/*
	 * Goal : Print list in command prompt/console
	 * 
	 * @Param pList: List of String to be printed
	 */
	public static void printList(List<String> pList)
	{
		System.out.println("Print list :");
		for(int i = 0; i < pList.size(); i++)
		{
			System.out.println(pList.get(i));
		}
	}
}

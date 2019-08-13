package com.projects.streamcombiner;

/*
 * POJO used to represent XML data
 */

public class Data 
{	
	
	private long timeStamp;
	private double amount;
	
	public Data()
	{
		this.timeStamp = 0;
		this.amount = 0;
	}
	
	public long getTimeStamp()
	{
		return  this.timeStamp;
	}
	
	public double getAmount()
	{
		return this.amount;
	}
	
	public void setTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
	}
	
	public void setAmount(double amount)
	{
		this.amount = amount;
	}
	
	//Print Data fields
	public String toString()
	{
		return new StringBuilder("timeStamp : "+ this.timeStamp +" amount : "+ this.amount).toString();	
	}
}

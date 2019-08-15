package com.projects.streamcombiner;

import static org.junit.Assert.assertEquals;
import java.net.Socket;
import org.junit.Test;

/*
 * Run tests on ClientProcessor class
 */
public class ClientProcessorTest {
	
	@Test
	public void buildXMLTest(){
		ClientProcessor cp = new ClientProcessor(new Socket());
		String xmlExpected = "<data> <timeStamp>123456789</timeStamp> <amount>1234.567890</amount> </data>";
		String xmlGenerated = cp.buildXML("123456789", "1234.567890");
		
		assertEquals(xmlExpected,xmlGenerated);
	}
}

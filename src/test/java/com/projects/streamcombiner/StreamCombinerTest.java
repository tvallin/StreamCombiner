package com.projects.streamcombiner;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/*
 * Test key methods of StreamCombiner class
 */
public class StreamCombinerTest {
	
	@Test
	public void toPojoTest() {
		List<String> xmlList = new ArrayList<String>();
		List<Data> pojoListParam = new ArrayList<Data>();
		List<Data> pojoListExpected = new ArrayList<Data>();
		
		xmlList.add("<data> <timeStamp>123456789</timeStamp> <amount>1234.56789</amount> </data>");
		pojoListExpected.add(new Data(123456789, 1234.567890));
		
		StreamCombiner.toPOJO(pojoListParam, xmlList);
		
		assertEquals(pojoListParam.get(0).getTimeStamp(),pojoListExpected.get(0).getTimeStamp());
		assertEquals(String.valueOf(pojoListParam.get(0).getAmount()),String.valueOf(pojoListExpected.get(0).getAmount()));
	}
	
	@Test
	public void checkEqualTimeStampTest() {
		List<Data> pojoListTested = new ArrayList<Data>();
		List<Data> pojoListExpected = new ArrayList<Data>();
		
		Data data0 = new Data(123456789, 1000.000001);
		Data data1 = new Data(123456789, 1000.000001);
		Data data2 = new Data(123456789, -1000.000001);
		
		pojoListTested.add(data0);
		pojoListTested.add(data1);
		pojoListTested.add(data2);
		pojoListExpected.add(new Data(123456789, 2000.000002));
		
		StreamCombiner.checkEqualTimeStamp(pojoListTested);
		
		assertEquals(pojoListTested.get(0).getTimeStamp(),pojoListExpected.get(0).getTimeStamp());
		assertEquals(String.valueOf(pojoListTested.get(0).getAmount()),String.valueOf(pojoListExpected.get(0).getAmount()));
	}
	
	@Test
	public void sortPOJOListTest() {
		List<Data> pojoListTested = new ArrayList<Data>();
		List<Data> pojoListExpected = new ArrayList<Data>();
		
		pojoListTested.add(new Data(123456792, 1000.000001));
		pojoListTested.add(new Data(123456791, 1000.000001));
		pojoListTested.add(new Data(123456790, 1000.000001));
		
		pojoListExpected.add(new Data(123456790, 1000.000001));
		pojoListExpected.add(new Data(123456791, 1000.000001));
		pojoListExpected.add(new Data(123456792, 1000.000001));
		
		StreamCombiner.sortPOJOList(pojoListTested);
		
		for(int i = 0; i < pojoListTested.size(); i++) {
			assertEquals(pojoListTested.get(i).getTimeStamp(),pojoListExpected.get(i).getTimeStamp());
			assertEquals(String.valueOf(pojoListTested.get(i).getAmount()),String.valueOf(pojoListExpected.get(i).getAmount()));
		}
		
	}
	
	@Test
	public void toJsonTest() {
		List<Data> pojoList = new ArrayList<Data>();
		List<String> jsonListParam = new ArrayList<String>();
		List<String> jsonListExpected = new ArrayList<String>();

		pojoList.add(new Data(123456789, 1234.567890));
		jsonListExpected.add("{ \"data\": { \"timestamp\":123456789, \"amount\":\"1234.56789\" }}");
		
		StreamCombiner.toJSON(pojoList,jsonListParam);
		
		assertEquals(jsonListParam.get(0),jsonListExpected.get(0));
	}
}

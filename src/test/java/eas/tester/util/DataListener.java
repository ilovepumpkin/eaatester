package eas.tester.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import com.google.common.eventbus.Subscribe;

import eaa.tester.data.DataLine;
import eaa.tester.event.DataLineChangeEvent;

public class DataListener {

	private List<DataLine> dataList = new ArrayList<DataLine>();

	@Subscribe
	public void handleData(DataLineChangeEvent dataEvent) {
		dataList.add(dataEvent.getDataLine());
	}
	
	public void assertAll(List<HashMap<String,Object>> expectedList){
		assertEquals(expectedList.size(),dataList.size());
		int i=0;
		for(DataLine dataLine:dataList){
			HashMap<String,Object> expected=expectedList.get(i++);
			expected.keySet().forEach(key -> {
				assertEquals(expected.get(key), dataLine.get(key));
			});
		}
	}
	
	public void clear(){
		dataList.clear();
	}

	public void assertLast(HashMap<String, Object> expected) {
		final DataLine lastOne = dataList.get(dataList.size() - 1);
		expected.keySet().forEach(key -> {
			assertEquals(expected.get(key), lastOne.get(key));
		});
	}
}

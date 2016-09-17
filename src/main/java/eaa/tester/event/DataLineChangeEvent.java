package eaa.tester.event;

import java.util.HashMap;

import eaa.tester.data.DataLine;

public class DataLineChangeEvent{
	private DataLine dataLine;
	public DataLineChangeEvent(DataLine dataLine){
		this.dataLine=dataLine;
	}
	
	public DataLine getDataLine(){
		return this.dataLine;
	}
}

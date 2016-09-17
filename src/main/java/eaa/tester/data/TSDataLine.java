package eaa.tester.data;

public class TSDataLine {
	private DataLine dataLine;
	private long thinkTime; //millseconds 
	public TSDataLine(DataLine dataLine, long thinkTime) {
		super();
		this.dataLine = dataLine;
		this.thinkTime = thinkTime;
	}
	
	public DataLine getDataLine(){
		return this.dataLine;
	}
	
	public long getThinkTime(){
		return this.thinkTime;
	}
}

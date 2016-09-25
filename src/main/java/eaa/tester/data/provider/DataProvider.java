package eaa.tester.data.provider;

import java.util.List;

import eaa.tester.data.TSDataLine;

public interface DataProvider {
	public List<TSDataLine> getDataLines();
	public List<String> getFieldNames();
	public String getDeviceType();
}

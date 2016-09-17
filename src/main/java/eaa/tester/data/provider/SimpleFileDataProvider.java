package eaa.tester.data.provider;

import eaa.tester.conf.Configuration;
import eaa.tester.data.DataLine;
import eaa.tester.data.TSDataLine;

public class SimpleFileDataProvider extends FileDataProvider {
	private long interval;

	public SimpleFileDataProvider(Configuration c) {
		this(c.getDataFilePath(),c.getInterval());
	}
	
	public SimpleFileDataProvider(String filePath, long interval) {
		super(filePath);
		this.interval = interval;
	}

	@Override
	protected TSDataLine handleDataLine(DataLine dataLine) {
		return new TSDataLine(dataLine, this.interval);
	}
}

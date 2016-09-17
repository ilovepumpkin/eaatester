package eaa.tester.data.provider;

import java.util.List;

import eaa.tester.conf.Configuration;
import eaa.tester.data.DataLine;
import eaa.tester.data.TSDataLine;

public class TSFileDataProvider extends FileDataProvider {

	private final static String FIELD_NAME_TIMELINE = "timeline";
	private final static String TIMELINE_DELIMITOR="\\.";
	
	private long prevTime = 0;

	public TSFileDataProvider(Configuration c) {
		this(c.getDataFilePath());
	}
	
	public TSFileDataProvider(String filePath) {
		super(filePath);
	}

	@Override
	protected TSDataLine handleDataLine(DataLine dataLine) {
		String timeline = dataLine.remove(FIELD_NAME_TIMELINE);
		final long currTime = parseTimelineStr(timeline);
		final long thinkTime = currTime - prevTime;
		prevTime = currTime;
		return new TSDataLine(dataLine, thinkTime);
	}

	private long parseTimelineStr(String timeline) {
		final String[] t = timeline.split(TIMELINE_DELIMITOR);
		final int h = Integer.parseInt(t[0]);
		final int m = Integer.parseInt(t[1]);
		final int s = Integer.parseInt(t[2]);
		return 1000 * (3600 * h + 60 * m + s);
	}
}

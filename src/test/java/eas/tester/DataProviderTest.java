package eas.tester;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;

import eaa.tester.conf.Configuration;
import eaa.tester.data.DataLine;
import eaa.tester.data.TSDataLine;
import eaa.tester.data.provider.DataProvider;
import eaa.tester.data.provider.DataProviderFactory;
import eas.tester.util.TestUtils;
import static eas.tester.util.TestUtils.*
;
public class DataProviderTest {
	private Configuration c = new Configuration();
	
	private String[][] getExpectedData(){
		return new String[][] { { "Tom", "11" }, { "John", "30" }, { "Mike", "22" } };
	}
	
	private long[] getTimeSeriesData(){
		return new long[]{5000,60*1000,3600*2*1000};
	}

	@Test
	public void testSimpleFileDataProvider() {
		c.setDataFilePath(getDataFilePath("simple.csv"));
		c.setInterval(2000);

		final String[][] expectedData=getExpectedData();
		
		DataProvider dp = DataProviderFactory.getDataProvider(DataProviderFactory.TYPE_SIMPLE_FILE,c);
		int i = 0;
		final List<TSDataLine> dataLines = dp.getDataLines();
		assertEquals(expectedData.length, dataLines.size());
		Iterator<TSDataLine> it = dataLines.iterator();
		while (it.hasNext()) {
			TSDataLine line = (TSDataLine) it.next();
			final DataLine dl = line.getDataLine();
			assertEquals(2000, line.getThinkTime());
			final String[] expectedLine = expectedData[i++];
			assertEquals(expectedLine[0], dl.get("name"));
			assertEquals(expectedLine[1], dl.get("age"));
		}
	}
	
	@Test
	public void testTimeSeriesFileDataProvider() {
		c.setDataFilePath(getDataFilePath("timeseries.csv"));

		final String[][] expectedData=getExpectedData();
		final long[] tsData=getTimeSeriesData();
		
		DataProvider dp = DataProviderFactory.getDataProvider(DataProviderFactory.TYPE_TIMESERIES_FILE,c);
		int i = 0;
		final List<TSDataLine> dataLines = dp.getDataLines();
		assertEquals(expectedData.length, dataLines.size());
		Iterator<TSDataLine> it = dataLines.iterator();
		while (it.hasNext()) {
			TSDataLine line = (TSDataLine) it.next();
			final DataLine dl = line.getDataLine();
			assertEquals(tsData[i], line.getThinkTime());
			final String[] expectedLine = expectedData[i];
			assertEquals(expectedLine[0], dl.get("name"));
			assertEquals(expectedLine[1], dl.get("age"));
			i++;
		}
	}
}

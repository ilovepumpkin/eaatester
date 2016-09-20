package eaa.tester.data.provider;

import eaa.tester.conf.Configuration;
import eaa.tester.exception.EAATesterException;

public class DataProviderFactory {

	public static final int TYPE_SIMPLE_FILE = 0;
	public static final int TYPE_TIMESERIES_FILE = 1;
	public static final int TYPE_MANUAL = 2;

	public static DataProvider getDataProvider(int type, Configuration c) {
		DataProvider provider = null;
		switch (type) {
		case TYPE_SIMPLE_FILE:
			provider = new SimpleFileDataProvider(c);
			break;
		case TYPE_TIMESERIES_FILE:
			provider = new TSFileDataProvider(c);
			break;
		default:
			throw new EAATesterException("Unknown DataProvider type!");
		}
		return provider;
	}
}

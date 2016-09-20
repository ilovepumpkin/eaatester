package eaa.tester.conf;

public class Configuration {
	private String proxyUrl;
	private String dataFilePath;
	private int interval;
	private int loopCount;
	private int dataSourceType;

	public Configuration() {
		super();
	}

	public int getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(int dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public String getProxyUrl() {
		return proxyUrl;
	}

	public void setProxyUrl(String proxyUrl) {
		this.proxyUrl = proxyUrl;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getLoopCount() {
		return loopCount;
	}

	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}
	
	

}

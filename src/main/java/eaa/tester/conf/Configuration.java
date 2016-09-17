package eaa.tester.conf;

public class Configuration {
	private static Configuration instance;
	private String proxyUrl;
	private String dataFilePath;
	private long interval;
	private boolean loop;
	private boolean automatic;
	
	private Configuration() {
		super();
	}

	public static Configuration getInstance() {
		if(instance == null){
			instance= new Configuration();
		}
		return instance;
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

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public boolean isAutomatic() {
		return automatic;
	}

	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}
	
}

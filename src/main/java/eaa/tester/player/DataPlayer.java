package eaa.tester.player;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.EventBus;

import eaa.tester.data.TSDataLine;
import eaa.tester.data.provider.DataProvider;
import eaa.tester.event.DataLineChangeEvent;
import eaa.tester.event.EAAEventBus;

public class DataPlayer {
	private DataProvider dlProvider;
	private EventBus eventBus = EAAEventBus.getInstance();

	private boolean isStopped;
	private int current;
	private List<TSDataLine> dataList;
	private int total;
	private int loopCount;

	public DataPlayer(DataProvider dlProvider) {
		this(dlProvider, 1);
	}

	public DataPlayer(DataProvider dlProvider, int loopCount) {
		this.dlProvider = dlProvider;
		dataList = this.dlProvider.getDataLines();
		this.total = dataList.size();
		this.loopCount=loopCount;
	}

	public boolean isTillEnd() {
		return this.current >= this.total;
	}

	public void play() {
		new Thread() {
			public void run() {
				while (!(isStopped || isTillEnd())) {
					next();
				}
			}
		}.start();
	}

	public void next() {
		final TSDataLine item = this.dataList.get(this.current);
		try {
			TimeUnit.MILLISECONDS.sleep(item.getThinkTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		eventBus.post(new DataLineChangeEvent(item.getDataLine()));
		this.current += 1;
		if(isTillEnd() && (--this.loopCount)>0){
			this.current=0;
		}
	}

	public void pause() {
		this.isStopped = true;
	}

	public void stop() {
		this.current = 0;
		this.isStopped = true;
	}

	public int total() {
		return dataList.size();
	}

	public int current() {
		return this.current;
	}

	public boolean isStopped() {
		return this.isStopped;
	}
}

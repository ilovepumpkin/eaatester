package eaa.tester.player;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.EventBus;

import eaa.tester.data.TSDataLine;
import eaa.tester.data.provider.DataProvider;
import eaa.tester.event.DataLineChangeEvent;
import eaa.tester.event.EAAEventBus;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DataPlayer {
	private DataProvider dlProvider;
	private EventBus eventBus = EAAEventBus.getInstance();

	// private boolean isStopped;
	private int current;
	private List<TSDataLine> dataList;
	private int total;
	private int loopCount;
	private int currentLoop = 1;

	private BooleanProperty isStopped = new SimpleBooleanProperty(true);

	public DataPlayer(DataProvider dlProvider) {
		this(dlProvider, 1);
	}

	public DataPlayer(DataProvider dlProvider, int loopCount) {
		this.dlProvider = dlProvider;
		dataList = this.dlProvider.getDataLines();
		this.total = dataList.size();
		this.loopCount = loopCount;
	}

	public boolean isTillEnd() {
		return this.current >= this.total;
	}

	public void play() {
		isStopped.set(false);
		new Thread() {
			public void run() {
				while (!(isStopped.get() || isTillEnd())) {
					internalNext(true);
				}
			}
		}.start();
	}

	public void next() {
		internalNext(false);
	}

	public void internalNext(boolean needThink) {
		final TSDataLine item = this.dataList.get(this.current);
		if (needThink) {
			try {
				TimeUnit.MILLISECONDS.sleep(item.getThinkTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		eventBus.post(new DataLineChangeEvent(item.getDataLine()));
		this.current += 1;
		if (isTillEnd() && this.currentLoop++ <= this.loopCount) {
			this.current = 0;
		}
	}

	public void pause() {
		this.isStopped.set(true);
	}

	public void stop() {
		this.current = 0;
		this.isStopped.set(true);
	}

	public int total() {
		return dataList.size();
	}

	public int current() {
		return this.current;
	}
	
	public int loopCount(){
		return this.loopCount;
	}
	
	public int currentLoop(){
		return this.currentLoop;
	}

	public BooleanProperty getIsStoppedProperty() {
		return this.isStopped;
	}
}

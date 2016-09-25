package eaa.tester.player;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.EventBus;

import eaa.tester.data.TSDataLine;
import eaa.tester.data.provider.DataProvider;
import eaa.tester.event.DataLineChangeEvent;
import eaa.tester.event.EAAEventBus;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DataPlayer {
	private DataProvider dlProvider;
	private EventBus eventBus = EAAEventBus.getInstance();

	// private boolean isStopped;
	private int current;
	private final List<TSDataLine> dataList;
	private int total;
	private int loopCount;
	private int currentLoop = 1;

	private BooleanProperty isStopped = new SimpleBooleanProperty(true);
	private Thread autoPlayThread;

	public DataPlayer(DataProvider dlProvider) {
		this(dlProvider, 1);
	}

	public DataPlayer(DataProvider dlProvider, int loopCount) {
		this.dlProvider = dlProvider;
		dataList = this.dlProvider.getDataLines();
		this.total = dataList.size();
		this.loopCount = loopCount;
	}

	public void play() {
		isStopped.set(false);
		autoPlayThread = new Thread() {
			public void run() {
				while (!isStopped.get()) {
					internalNext(true);
				}
			}
		};
		autoPlayThread.start();
	}

	public void next() {
		internalNext(false);
	}

	public void internalNext(boolean needThink) {
		try {
			final TSDataLine item = this.dataList.get(this.current);
			if (needThink) {
				TimeUnit.MILLISECONDS.sleep(item.getThinkTime());
			}
			eventBus.post(new DataLineChangeEvent(item.getDataLine()));
			this.current += 1;
			if (this.current == this.total) {
				if (this.currentLoop < this.loopCount) {
					this.current = 0;
					this.currentLoop += 1;
				} else {
					this.isStopped.set(true);
				}
			}
		} catch (InterruptedException e) {
			// Pause or Stop is clicked.
		}

	}

	public void pause() {
		this.isStopped.set(true);
		autoPlayThread.interrupt();
	}

	public void stop() {
		this.current = 0;
		this.isStopped.set(true);
		autoPlayThread.interrupt();
	}

	public int total() {
		return dataList.size();
	}

	public int current() {
		return this.current;
	}

	public int loopCount() {
		return this.loopCount;
	}

	public int currentLoop() {
		return this.currentLoop;
	}

	public BooleanProperty getIsStoppedProperty() {
		return this.isStopped;
	}
}

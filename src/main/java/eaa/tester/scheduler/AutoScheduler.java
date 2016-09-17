package eaa.tester.scheduler;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.EventBus;

import eaa.tester.data.TSDataLine;
import eaa.tester.data.provider.DataProvider;
import eaa.tester.event.EAAEventBus;

public class AutoScheduler {
	private DataProvider dlProvider;
	private EventBus eventBus=EAAEventBus.getInstance();

	public AutoScheduler(DataProvider dlProvider) {
		this.dlProvider = dlProvider;
	}
	
	public void play(){
		List<TSDataLine> dataList=this.dlProvider.getDataLines();
		dataList.forEach(item->{
			eventBus.post(item.getDataLine());
			try {
				TimeUnit.MILLISECONDS.sleep(item.getThinkTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}
}

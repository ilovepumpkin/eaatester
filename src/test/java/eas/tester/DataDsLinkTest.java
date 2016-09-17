package eas.tester;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.eventbus.EventBus;

import eaa.tester.data.DataLine;
import eaa.tester.dslink.DataDsLink;
import eaa.tester.event.DataLineChangeEvent;
import eaa.tester.event.EAAEventBus;

public class DataDsLinkTest {

	@Test
	public void testSendDataEvent() {
		EventBus eventBus = EAAEventBus.getInstance();
		DataDsLink dslink = new DataDsLink();
		eventBus.register(dslink);

		dslink.start();

		for (int i = 0; i < 10; i++) {
			DataLine dl = new DataLine();
			dl.put("value", i);
			DataLineChangeEvent evt = new DataLineChangeEvent(dl);
			eventBus.post(evt);
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		dslink.stop();
	}

}

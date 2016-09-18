package eas.tester;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

import eaa.tester.data.DataLine;
import eaa.tester.dslink.DataDsLink;
import eaa.tester.event.DataLineChangeEvent;
import eaa.tester.event.EAAEventBus;

public class DataDsLinkTest {
	private static EventBus eventBus = EAAEventBus.getInstance();
	private static DataDsLink dslink = new DataDsLink();

	
	@BeforeClass
	public static void setUp(){
		eventBus.register(dslink);
	}
	
	@AfterClass
	public static void tearDown(){
		eventBus.unregister(dslink);
	}

	@Test
	public void testSendDataEvent() {
		

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

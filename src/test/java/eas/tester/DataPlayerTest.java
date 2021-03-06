package eas.tester;

import static eas.tester.util.TestUtils.getDataFilePath;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import eaa.tester.conf.Configuration;
import eaa.tester.data.provider.DataProvider;
import eaa.tester.data.provider.DataProviderFactory;
import eaa.tester.event.EAAEventBus;
import eaa.tester.player.DataPlayer;
import eas.tester.util.DataListener;

public class DataPlayerTest {
	private Configuration c = new Configuration();

	private HashMap<String, Object> getExpected(String name, String age) {
		return new HashMap<String, Object>() {
			{
				put("name", name);
				put("age", age);
			}
		};
	}

	@Test
	public void testManual() {
		DataListener dataListener = new DataListener();
		EAAEventBus.getInstance().register(dataListener);
		
		c.setDataFilePath(getDataFilePath("dataplayer_test.csv"));
		c.setInterval(100);
		c.setDataSourceType(DataProviderFactory.TYPE_SIMPLE_FILE);
		
		DataProvider dp = DataProviderFactory.getDataProvider(c);
		DataPlayer player = new DataPlayer(dp);
		player.next();
		dataListener.assertLast(getExpected("Tom", "11"));
		player.next();
		player.next();
		player.next();
		player.next();
		dataListener.assertLast(getExpected("Alex", "38"));
		assertTrue(player.getIsStoppedProperty().get());
	}
	
	@Test
	public void testAutomatic() throws InterruptedException {
		DataListener dataListener = new DataListener();
		EAAEventBus.getInstance().register(dataListener);
		
		c.setDataFilePath(getDataFilePath("dataplayer_test.csv"));
		final int interval=100;
		c.setInterval(interval);
		c.setDataSourceType(DataProviderFactory.TYPE_SIMPLE_FILE);
		
		DataProvider dp = DataProviderFactory.getDataProvider(c);
		DataPlayer player = new DataPlayer(dp);
		player.play();
		TimeUnit.MILLISECONDS.sleep((long)(interval*2.5));
		player.pause();
		ArrayList<HashMap<String,Object>> expectedList=new ArrayList<HashMap<String,Object>>();
		expectedList.add(getExpected("Tom","11"));
		expectedList.add(getExpected("John","30"));
		dataListener.assertAll(expectedList);
		assertEquals(2,player.current());
		dataListener.clear();
		player.play();
		TimeUnit.MILLISECONDS.sleep((long)(interval*1.5));
		expectedList.clear();
		expectedList.add(getExpected("Mike","22"));
		dataListener.assertAll(expectedList);
		assertEquals(3,player.current());
		player.stop();
		assertEquals(0,player.current());
		assertTrue(player.getIsStoppedProperty().get());
	}
	
	@Test
	public void testAutomaticWithLoop() throws InterruptedException {
		DataListener dataListener = new DataListener();
		EAAEventBus.getInstance().register(dataListener);
		
		c.setDataFilePath(getDataFilePath("dataplayer_test.csv"));
		c.setInterval(100);
		c.setDataSourceType(DataProviderFactory.TYPE_SIMPLE_FILE);
		
		DataProvider dp = DataProviderFactory.getDataProvider(c);
		final int loopCount=2;
		DataPlayer player = new DataPlayer(dp,loopCount);
		player.play();
		TimeUnit.MILLISECONDS.sleep(1500);
		player.stop();
		ArrayList<HashMap<String,Object>> expectedList=new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<loopCount;i++){
			expectedList.add(getExpected("Tom","11"));
			expectedList.add(getExpected("John","30"));
			expectedList.add(getExpected("Mike","22"));
			expectedList.add(getExpected("Jim","50"));
			expectedList.add(getExpected("Alex","38"));	
		}
		dataListener.assertAll(expectedList);
		
	}
}

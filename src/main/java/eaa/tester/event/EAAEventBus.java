package eaa.tester.event;

import com.google.common.eventbus.EventBus;

public class EAAEventBus {
	private static EventBus eventBus = new EventBus();

	public static EventBus getInstance() {
		return eventBus;
	}
}

package eaa.tester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.dsa.iot.dslink.DSLink;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.NodeBuilder;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.node.value.ValueType;
import org.dsa.iot.dslink.util.Objects;

import com.google.common.eventbus.EventBus;

import eaa.tester.dslink.DataDsLink;
import eaa.tester.event.DataLineChangeEvent;
import eaa.tester.event.EAAEventBus;


public class EAATester {
	
	private static Consumer<DataDsLink> createSimpleLinkBuilder(List<Integer> data) {
		Consumer<DataDsLink> builder = new Consumer<DataDsLink>() {
			@Override
			public void accept(DataDsLink mockDevDsLink) {
				DSLink dsLink = mockDevDsLink.getDSLink();
				Node superRoot = dsLink.getNodeManager().getSuperRoot();

				mockDevDsLink.setAttributes(superRoot);

				NodeBuilder builder = superRoot.createChild("CPUUsage");
				builder.setValueType(ValueType.NUMBER);
				builder.setValue(new Value(0));
				Node node = builder.build();

				Objects.getThreadPool().submit(new Runnable() {
					@Override
					public void run() {
						data.forEach(num -> {
							System.out.println(">>>>"+num);
							Value val = new Value(num);
							node.setValue(val);
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						});
						mockDevDsLink.done();
					}
				});
			}
		};
		return builder;
	}

	public static void main(String[] args) {
		

	}

	
}

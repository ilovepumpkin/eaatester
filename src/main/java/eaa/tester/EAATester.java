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
import eaa.tester.dslink.MockDevDsLink;


public class EAATester {
	
	private static Consumer<MockDevDsLink> createSimpleLinkBuilder(List<Integer> data) {
		Consumer<MockDevDsLink> builder = new Consumer<MockDevDsLink>() {
			@Override
			public void accept(MockDevDsLink mockDevDsLink) {
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
		final List<Integer> data = new ArrayList();
		data.add(1);
		data.add(2);
		data.add(3);
		data.add(4);
		final Consumer<MockDevDsLink> builder = createSimpleLinkBuilder(data);
		MockDevDsLink dslink=new MockDevDsLink(builder);
		new Thread(dslink).start();
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(!dslink.isDone()){
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("### bye!");
				System.exit(0);
			}
			
		}).start();
	}

}

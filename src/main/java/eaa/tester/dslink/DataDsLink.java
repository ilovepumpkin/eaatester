package eaa.tester.dslink;

import java.io.File;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.dsa.iot.dslink.DSLink;
import org.dsa.iot.dslink.DSLinkFactory;
import org.dsa.iot.dslink.DSLinkHandler;
import org.dsa.iot.dslink.config.Arguments;
import org.dsa.iot.dslink.config.Configuration;
import org.dsa.iot.dslink.connection.ConnectionType;
import org.dsa.iot.dslink.handshake.LocalKeys;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.NodeBuilder;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.node.value.ValueType;
import org.dsa.iot.dslink.util.PropertyReference;
import org.dsa.iot.dslink.util.json.JsonObject;
import org.dsa.iot.dslink.util.log.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import eaa.tester.EdgeDsaTestHelper;
import eaa.tester.data.DataLine;
import eaa.tester.event.DataLineChangeEvent;

enum NODE_STATUS {
	NOT_INITIALIZED, INITIALIZED, DONE
}

public class DataDsLink extends DSLinkHandler implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataDsLink.class);

	private NODE_STATUS status = NODE_STATUS.NOT_INITIALIZED;

	private String devName;

	private String devType;

	private Consumer<DataDsLink> linkBuilder;

	private DSLink dsLink;

	public static final String NAME_PREFIX = "mockdev_";

	public static final String ATTR_TYPE_NAME = "type";
	public static final String ATTR_TYPE_VALUE = "device";

	public static final String ATTR_DEVICE_TYPE_NAME = "devicetype";

	private Node dataNode;

	public void setAttributes(Node node) {
		node.setAttribute(ATTR_TYPE_NAME, new Value(ATTR_TYPE_VALUE));
		node.setAttribute(ATTR_DEVICE_TYPE_NAME, new Value(getDevType()));
	}

	public DSLink getDSLink() {
		return dsLink;
	}

	public NODE_STATUS getStatus() {
		return status;
	}

	public void done() {
		this.status = NODE_STATUS.DONE;
	}

	public boolean isDone() {
		return this.status == NODE_STATUS.DONE;
	}

	@Override
	public Configuration getConfig() {
		Configuration defaults = new Configuration();
		defaults.setConnectionType(ConnectionType.WEB_SOCKET);
		defaults.setRequester(false);
		defaults.setResponder(true);
		defaults.setLinkData(null);

		String name = this.devName;
		String logLevel = "info";
		String brokerHost = EdgeDsaTestHelper.getDSBrockerUrl();
		String keyPath = "mockdev.key";
		String nodePath = "nodes_mockdev.json";
		String handlerClass = this.getClass().getName();

		String prop = System.getProperty(PropertyReference.VALIDATE, "true");
		boolean validate = Boolean.parseBoolean(prop);
		if (validate) {
			prop = PropertyReference.VALIDATE_HANDLER;
			prop = System.getProperty(prop, "true");
			validate = Boolean.parseBoolean(prop);
		}
		if (validate) {
			try {
				// Validate handler class
				ClassLoader loader = Configuration.class.getClassLoader();
				Class<?> clazz = loader.loadClass(handlerClass);
				if (!DSLinkHandler.class.isAssignableFrom(clazz)) {
					String err = "Class `" + handlerClass + "` does not extend";
					err += " " + DSLinkHandler.class.getName();
					throw new RuntimeException(err);
				}
			} catch (ClassNotFoundException e) {
				String err = "Handler class not found: " + handlerClass;
				throw new RuntimeException(err);
			}
		}

		defaults.setAuthEndpoint(brokerHost);
		// defaults.setToken(null);
		defaults.setDsId(name);

		File loc = new File(keyPath);
		defaults.setKeys(LocalKeys.getFromFileSystem(loc));

		loc = new File(nodePath);
		defaults.setSerializationPath(loc);
		return defaults;
	}

	@Override
	public void onResponderInitialized(DSLink link) {
		this.dsLink = link;
		// linkBuilder.accept(this);
		Node root = link.getNodeManager().getSuperRoot();
		root.setAttribute("type", new Value("device"));
		root.setAttribute("devicetype", new Value("EngineSensors"));
		addNodes(root);
		this.status = NODE_STATUS.INITIALIZED;
		LOGGER.info("Responder initialized");
	}

	@Override
	public void onResponderConnected(DSLink link) {
		LOGGER.info("Responder connected");
	}

	@Override
	public void onResponderDisconnected(DSLink link) {
		LOGGER.info("Oh no! The connected to the broker was lost");
	}

	@Override
	public boolean isResponder() {
		return true;
	}

	public DataDsLink() {
		super();
		this.devName = "EAATester";
		setProvider(DSLinkFactory.generate(this));
	}

	public DataDsLink(Consumer<DataDsLink> linkBuilder) {
		this.devName = NAME_PREFIX + new Random().nextInt();
		this.devType = devName + "-Type";
		setProvider(DSLinkFactory.generate(this));
		this.linkBuilder = linkBuilder;
	}

	public DataDsLink(String devName, Consumer<DataDsLink> linkBuilder) {
		this.devName = devName;
		this.devType = devName + "-Type";
		setProvider(DSLinkFactory.generate(this));
		this.linkBuilder = linkBuilder;
	}

	public DataDsLink(String devName, String devType, Consumer<DataDsLink> linkBuilder) {
		this.devName = devName;
		this.devType = devType;
		setProvider(DSLinkFactory.generate(this));
		this.linkBuilder = linkBuilder;
	}

	public String getDevName() {
		return this.devName;
	}

	public String getDevType() {
		return this.devType;
	}

	/**
	 * clear all children of a dslink node
	 * 
	 * @param node
	 */
	public synchronized void clearChildrenNodes() {
		// dsLink.getLinkHandler().getProvider().stop();
		// dsLink.getLinkHandler().getProvider().start();

		DSLink dsLink = getDSLink();
		Node superRoot = dsLink.getNodeManager().getSuperRoot();
		superRoot.clearChildren();
	}

	@Override
	public void run() {
		new Thread() {
			@Override
			public void run() {
				while (status != NODE_STATUS.DONE) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				getProvider().stop();
			}
		}.start();
		getProvider().start();
		getProvider().sleep();
	}

	/*
	 * ==========================
	 */

	public void start() {
		new Thread(this).start();
	    int tryCount=5;
		while (!isReady() && tryCount-->0) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		if(tryCount<=0){
			throw new RuntimeException("The dslink was not started within the given time.");
		}
	}

	public void stop() {
		this.status = NODE_STATUS.DONE;
	}

	public boolean isReady() {
		return this.status == NODE_STATUS.INITIALIZED;
	}

	private void addNodes(Node superRoot) {
		NodeBuilder builder = superRoot.createChild("EAATesterType");
		builder.setDisplayName("EAATesterType");
		builder.setValueType(ValueType.MAP);
		dataNode = builder.build();

	}

	@Subscribe
	public void handleDataChange(DataLineChangeEvent dataEvent) {
		DataLine dl=dataEvent.getDataLine();
		LOGGER.info("Data line: {}",dl);
		dataNode.setValue(new Value(dl));
	}
}

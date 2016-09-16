package eaa.tester;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeDsaTestHelper {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EdgeDsaTestHelper.class);

	public static String getDSBrockerUrl() {
		String broker = System.getProperty("broker",
				"http://localhost:8080/conn");
		return broker;
	}
}

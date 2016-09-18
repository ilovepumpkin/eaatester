package eas.tester.util;

import com.google.common.io.Resources;

public class TestUtils {

	public static String getDataFilePath(String fileName) {
		return Resources.getResource(fileName).getFile();
	}
}

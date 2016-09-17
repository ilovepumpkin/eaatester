package eaa.tester.data.provider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

import eaa.tester.data.DataLine;
import eaa.tester.data.TSDataLine;

public abstract class FileDataProvider implements DataProvider {

	private String filePath;
	private List<String> fieldNames;
	protected final static String FIELD_SEPERATOR = ",";

	public FileDataProvider(String filePath) {
		this.filePath = filePath;
	}

	protected List<String> parseFieldNames(String headLine) {
		List<String> names = Arrays.asList(headLine.split(","));
		return new ArrayList(names);
	}

	public List<String> getFieldNames() {
		return this.fieldNames;
	}

	@Override
	public List<TSDataLine> getDataLines() {
		List<TSDataLine> dataLines = new ArrayList<TSDataLine>();
		try {
			CharSource cs = Files.asCharSource(new File(filePath), Charsets.UTF_8);
			fieldNames = parseFieldNames(cs.readFirstLine());
			ImmutableList<String> lines = cs.readLines();
			lines=lines.subList(1, lines.size());
			lines.forEach(line -> {
				String[] fields = line.split(FIELD_SEPERATOR);
				DataLine dataLine = new DataLine();
				for (int i = 0; i < fields.length; i++) {
					String fieldValue = fields[i];
					dataLine.put(this.getFieldNames().get(i), fieldValue);
				}
				dataLines.add(handleDataLine(dataLine));
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataLines;
	}

	protected abstract TSDataLine handleDataLine(DataLine dataLine);
}

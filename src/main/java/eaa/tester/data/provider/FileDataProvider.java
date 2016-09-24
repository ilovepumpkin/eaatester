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

	protected final static String FIELD_SEPERATOR = ",";
	private String filePath;
	private List<String> fieldNames;
	private List<TSDataLine> dataLines;

	public FileDataProvider(String filePath) {
		this.filePath = filePath;
		this.fieldNames=readFieldNames();
	}

	public List<String> getFieldNames() {
		return this.fieldNames;
	}
	
	protected abstract TSDataLine handleDataLine(DataLine dataLine);
	
	private List<String> readFieldNames() {
		CharSource cs = Files.asCharSource(new File(filePath), Charsets.UTF_8);
		String headLine;
		try {
			headLine = cs.readFirstLine();
			List<String> names = Arrays.asList(headLine.split(","));
			return new ArrayList<String>(names);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<TSDataLine> getDataLines(){
		if(this.dataLines==null){
			this.dataLines = new ArrayList<TSDataLine>();
			try {
				CharSource cs = Files.asCharSource(new File(filePath), Charsets.UTF_8);
				ImmutableList<String> lines = cs.readLines();
				lines=lines.subList(1, lines.size());
				lines.forEach(line -> {
					String[] fields = line.split(FIELD_SEPERATOR);
					DataLine dataLine = new DataLine();
					for (int i = 0; i < fields.length; i++) {
						String fieldValue = fields[i];
						dataLine.put(this.fieldNames.get(i), fieldValue);
					}
					dataLines.add(handleDataLine(dataLine));
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.dataLines;
	}
}

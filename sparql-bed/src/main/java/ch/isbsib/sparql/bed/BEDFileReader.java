package ch.isbsib.sparql.bed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class BEDFileReader implements Iterator<String[]> {
	private final Scanner scanner;
	private final Pattern tab = Pattern.compile("\t");
	
	private final Pattern digits = Pattern.compile("\\d+");
	private String[] nexts;

	public BEDFileReader(File bedFile) throws FileNotFoundException,
			IOException {
		super();
		if (bedFile.getName().endsWith("gz"))
			this.scanner = new Scanner(new GZIPInputStream(new FileInputStream(
					bedFile)));
		else
			this.scanner = new Scanner(bedFile);
	}

	@Override
	public boolean hasNext() {
		while (scanner.hasNextLine()) {
			nexts = parseLine(scanner.nextLine());
			if (nexts != null)
				return true;
		}
		return false;
	}

	@Override
	public String[] next() {
		try {
			return nexts;
		} finally {
			nexts = null;
		}
	}

	@Override
	public void remove() {

	}

	private String[] parseLine(String line) {
		// max 12 fields in a bed file line
		String[] record = new String[12];
		int fieldId = 0;
		for (String field : tab.split(line)) {
			if (fieldId < 12) {
				record[fieldId] = field;
				fieldId++;
			}
		}
		// Comment lines might be in your data which do not have tabs/or
		// digits
		if (record[1] != null && record[2] != null
				&& digits.matcher(record[1]).find()
				&& digits.matcher(record[2]).find())
			return record;
		else
			return null;
	}
}

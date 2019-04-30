package rmc.console;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

public class RebblMonteCarlo {

	private final static int SIM_NUMBER = 100000;

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException(
					"This should be run with two arguments, the file name containing the fixture list, and the number of playoff spots.");
		}
		String fileName = args[0];
		Integer playoffSpots = Integer.valueOf(args[1]);

		File fixtureFile = new File(fileName);
		CSVParser fixtureParser = CSVParser.parse(fixtureFile, Charset.defaultCharset(), CSVFormat.EXCEL);

		List<Entry<String, Double>> sortedPercentage = MonteCarloConsoleRunner.run(fixtureParser, playoffSpots, SIM_NUMBER);

		fixtureParser.close();

		for (Entry<String, Double> teamEntry : sortedPercentage) {
			System.out.printf("%30s | %.3f%%%n", teamEntry.getKey(), teamEntry.getValue());
		}
	}

}

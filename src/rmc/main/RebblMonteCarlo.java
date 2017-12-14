package rmc.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map.Entry;

public class RebblMonteCarlo {

	private final static int SIM_NUMBER = 100000;

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException(
					"This should be run with two arguments, the file name containing the fixture list, and the number of playoff spots.");
		}
		String fileName = args[0];
		Integer playoffSpots = Integer.valueOf(args[1]);

		FileReader fixtureFile = new FileReader(fileName);
		BufferedReader fileStream = new BufferedReader(fixtureFile);

		List<Entry<String, Double>> sortedPercentage = MonteCarloRunner.run(fileStream, playoffSpots, SIM_NUMBER);

		fileStream.close();
		fixtureFile.close();

		for (Entry<String, Double> teamEntry : sortedPercentage) {
			System.out.printf("%30s | %.3f%%%n", teamEntry.getKey(), teamEntry.getValue());
		}
	}

}

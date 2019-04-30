package rmc.console;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVParser;

import rmc.data.Schedule;
import rmc.engines.DranarPredictiveEngine;

public class MonteCarloConsoleRunner {

	public static List<Entry<String, Double>> run(CSVParser fixtureParser, int playoffSpots, long simNumber)
			throws Exception {
		Schedule baseSchedule = new Schedule(fixtureParser);
		List<String> allPlayoffTeams = new ArrayList<String>();

		for (int i = 0; i < simNumber; i++) {
			Schedule simSchedule = (Schedule) baseSchedule.clone();
			simSchedule.fillMissingScores(new DranarPredictiveEngine());
			List<String> playoffTeams = simSchedule.getTopTeams(Integer.valueOf(playoffSpots));
			allPlayoffTeams.addAll(playoffTeams);
		}

		Map<String, Long> playoffTimes = allPlayoffTeams.parallelStream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		Map<String, Double> playoffPercentage = new HashMap<>();
		playoffTimes.forEach((k, v) -> playoffPercentage.put(k, 100 * ((double) v / simNumber)));

		List<Entry<String, Double>> sortedPercentage = playoffPercentage.entrySet().stream()
				.sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())).collect(Collectors.toList());

		return sortedPercentage;
	}
}

package rmc.main;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import rmc.data.MatchResult;
import rmc.data.Schedule;

public class MonteCarloRunner {

	public static List<Entry<String, Double>> run(BufferedReader inputFile, int playoffSpots, long simNumber)
			throws Exception {
		Schedule baseSchedule = new Schedule();
		while (inputFile.ready()) {
			String fixtureLine = inputFile.readLine();
			if (fixtureLine != null) {
				String[] fixtureSplit = fixtureLine.split(",");
				if (fixtureSplit.length == 4) {
					String teamName1 = fixtureSplit[0];
					String teamName2 = fixtureSplit[3];
					String score1 = fixtureSplit[1];
					String score2 = fixtureSplit[2];

					if (!teamName1.isEmpty() && !teamName2.isEmpty()) {
						if (score1.isEmpty() || fixtureSplit[2].isEmpty()) {
							baseSchedule.addResult(new MatchResult(teamName1, teamName2));
						}
						else {
							baseSchedule.addResult(new MatchResult(teamName1, Integer.valueOf(score1),
									Integer.valueOf(score2), teamName2));
						}
					}
				}
			}
		}

		List<String> allPlayoffTeams = new ArrayList<String>();

		for (int i = 0; i < simNumber; i++) {
			Schedule simSchedule = (Schedule) baseSchedule.clone();
			simSchedule.fillMissingScores();
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

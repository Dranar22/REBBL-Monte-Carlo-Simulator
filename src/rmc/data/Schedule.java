package rmc.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import rmc.engines.AbstractMonteCarloEngine;
import rmc.utils.StandingsComparator;

public class Schedule implements Cloneable {

	private List<MatchResult> gameList = new ArrayList<MatchResult>();
	private Map<String, TeamInfo> teams = new HashMap<String, TeamInfo>();

	public void addResult(MatchResult result) {
		gameList.add(result);

		// Fill in missing TeamInfo items if needed.
		String teamOneName = result.getTeamOne();
		String teamTwoName = result.getTeamTwo();

		if (!teams.containsKey(teamOneName)) {
			teams.put(teamOneName, new TeamInfo(teamOneName));
		}
		if (!teams.containsKey(teamTwoName)) {
			teams.put(teamTwoName, new TeamInfo(teamTwoName));
		}

		if (result.hasScores()) {
			processResult(result);
		}
	}

	private void processResult(MatchResult result) {
		String teamOneName = result.getTeamOne();
		String teamTwoName = result.getTeamTwo();

		// Compare scores, update TeamInfo objects.
		int scoreOne = result.getScoreOne();
		int scoreTwo = result.getScoreTwo();

		if (scoreOne > scoreTwo) {
			// Team One won... won one. 1.
			teams.get(teamOneName).addWin(scoreOne, scoreTwo, teamTwoName);
			teams.get(teamTwoName).addLoss(scoreTwo, scoreOne);
		}
		else if (scoreOne < scoreTwo) {
			// Team Two won... zero.
			teams.get(teamOneName).addLoss(scoreOne, scoreTwo);
			teams.get(teamTwoName).addWin(scoreTwo, scoreOne, teamOneName);
		}
		else {
			// A tie. Yawn.
			teams.get(teamOneName).addTie(scoreOne, scoreTwo);
			teams.get(teamTwoName).addTie(scoreTwo, scoreOne);
		}

		result.setProcessed();
	}

	public void fillMissingScores(AbstractMonteCarloEngine engine) {
		for (MatchResult result : gameList) {
			if (!result.hasScores()) {
				TeamInfo teamOne = teams.get(result.getTeamOne());
				TeamInfo teamTwo = teams.get(result.getTeamTwo());
				Pair<Integer, Integer> scores = engine.getScoresForTeams(teamOne, teamTwo);
				result.setScoreOne(scores.getKey());
				result.setScoreTwo(scores.getValue());
			}
		}
	}

	public List<String> getTopTeams(int numOfTeams) {
		for (MatchResult result : gameList) {
			if (!result.isProcessed()) {
				processResult(result);
			}
		}

		List<TeamInfo> finalStandings = new ArrayList<TeamInfo>(teams.values());
		finalStandings.sort(new StandingsComparator());

		List<String> topTeamNames = new ArrayList<String>();
		for (TeamInfo topTeamInfo : finalStandings.subList(0, numOfTeams)) {
			topTeamNames.add(topTeamInfo.getTeamName());
		}
		return topTeamNames;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Schedule clonedSchedule = new Schedule();
		for (MatchResult result : gameList) {
			clonedSchedule.addResult((MatchResult) result.clone());
		}
		return clonedSchedule;
	}
}

package rmc.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rmc.utils.RandomUtils;
import rmc.utils.StandingsComparator;

public class Schedule implements Cloneable {

	private List<MatchResult> gameList = new ArrayList<MatchResult>();

	public void addResult(MatchResult result) {
		gameList.add(result);
	}

	public void fillMissingScores() {
		for (MatchResult result : gameList) {
			if (!result.hasScores()) {
				result.setScoreOne(RandomUtils.getRandomScore());
				result.setScoreTwo(RandomUtils.getRandomScore());
			}
		}
	}

	public List<String> getTopTeams(int numOfTeams) {
		Map<String, TeamInfo> teams = new HashMap<String, TeamInfo>();

		for (MatchResult result : gameList) {
			// Fill in missing TeamInfo items if needed.
			String teamOneName = result.getTeamOne();
			String teamTwoName = result.getTeamTwo();

			if (!teams.containsKey(teamOneName)) {
				teams.put(teamOneName, new TeamInfo(teamOneName));
			}
			if (!teams.containsKey(teamTwoName)) {
				teams.put(teamTwoName, new TeamInfo(teamTwoName));
			}

			// Compare scores, update TeamInfo objects.
			int scoreOne = result.getScoreOne();
			int scoreTwo = result.getScoreTwo();
			int diff = Math.abs(scoreOne - scoreTwo);

			if (scoreOne > scoreTwo) {
				// Team One won... won one. 1.
				teams.get(teamOneName).addWin(diff, teamTwoName);
				teams.get(teamTwoName).addLoss(diff);
			}
			else if (scoreOne < scoreTwo) {
				// Team Two won... zero.
				teams.get(teamTwoName).addWin(diff, teamOneName);
				teams.get(teamOneName).addLoss(diff);
			}
			else {
				// A tie. Yawn.
				teams.get(teamOneName).addTie();
				teams.get(teamTwoName).addTie();
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

package rmc.data;

import java.util.*;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import javafx.util.Pair;
import rmc.engines.AbstractMonteCarloEngine;
import rmc.utils.StandingsComparator;

public class Schedule implements Cloneable {

	private static final String HOME_TEAM = "homeTeam";
	private static final String AWAY_TEAM = "awayTeam";
	private static final String HOME_SCORE = "homeScore";
	private static final String AWAY_SCORE = "awayScore";
	private static final String NULL = "null";

	private List<MatchResult> gameList = new ArrayList<MatchResult>();
	private Map<String, TeamInfo> teams = new HashMap<String, TeamInfo>();
	private List<TeamInfo> finalStandings = null;

	public Schedule() {}

	public Schedule(CSVParser fileParser) {
		for (CSVRecord fixture : fileParser) {
			String teamName1 = fixture.get(0);
			String teamName2 = fixture.get(3);
			String score1 = fixture.get(1);
			String score2 = fixture.get(2);

			if (!teamName1.isEmpty() && !teamName2.isEmpty()) {
				if (score1.isEmpty() || score2.isEmpty()) {
					addResult(new MatchResult(teamName1, teamName2));
				}
				else {
					addResult(new MatchResult(teamName1, Integer.valueOf(score1), Integer.valueOf(score2), teamName2));
				}

			}
		}
	}

	public Schedule(JsonArray scheduleJson) {
		for (Object match : scheduleJson) {
			JsonObject matchJson = (JsonObject) match;

			String teamName1 = matchJson.containsKey(HOME_TEAM) ? String.valueOf(matchJson.get(HOME_TEAM)) : "";
			String teamName2 = matchJson.containsKey(AWAY_TEAM) ? String.valueOf(matchJson.get(AWAY_TEAM)) : "";
			String score1 = matchJson.containsKey(HOME_SCORE) ? String.valueOf(matchJson.get(HOME_SCORE)) : "";
			String score2 = matchJson.containsKey(AWAY_SCORE) ? String.valueOf(matchJson.get(AWAY_SCORE)) : "";

			if (!teamName1.isEmpty() && !teamName2.isEmpty()) {
				if (score1.isEmpty() || score2.isEmpty() || score1.equals(NULL) || score2.equals(NULL)) {
					addResult(new MatchResult(teamName1, teamName2));
				}
				else {
					addResult(new MatchResult(teamName1, Integer.valueOf(score1), Integer.valueOf(score2), teamName2));
				}

			}
		}
	}

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
		if (finalStandings == null) {
			for (MatchResult result : gameList) {
				if (!result.isProcessed()) {
					processResult(result);
				}
			}

			finalStandings = new ArrayList<TeamInfo>(teams.values());
			finalStandings.sort(new StandingsComparator());
		}

		List<String> topTeamNames = new ArrayList<String>();
		for (TeamInfo topTeamInfo : finalStandings.subList(0, numOfTeams)) {
			topTeamNames.add(topTeamInfo.getTeamName());
		}
		return topTeamNames;
	}

	public List<String> getTeamNames() {
		List<TeamInfo> currentStandings = new ArrayList<TeamInfo>(teams.values());
		currentStandings.sort(new StandingsComparator());

		List<String> topTeamNames = new ArrayList<String>();
		for (TeamInfo topTeamInfo : currentStandings) {
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

	public Map<String, ManualGamePrediction> getManualPredictionMap() {
		Map<String, ManualGamePrediction> predictionMap = new LinkedHashMap<String, ManualGamePrediction>();
		for (MatchResult result : gameList) {
			if (!result.hasScores()) {
				TeamInfo teamOne = teams.get(result.getTeamOne());
				TeamInfo teamTwo = teams.get(result.getTeamTwo());
				ManualGamePrediction predictionInfo = new ManualGamePrediction(teamOne.getTeamName(),
						teamTwo.getTeamName());
				predictionMap.put(predictionInfo.getMatchKey(), predictionInfo);
			}
		}
		return predictionMap;
	}

	public List<MatchResult> getAllMatches() {
		return new ArrayList<MatchResult>(gameList);
	}
}

package rmc.engines;

import java.util.Map;

import javafx.util.Pair;
import rmc.data.ManualGamePrediction;
import rmc.data.TeamInfo;
import rmc.utils.RandomUtils;

public class TselfManualPredictionEngine extends AbstractMonteCarloEngine {

	Map<String, ManualGamePrediction> gPredictions;

	public TselfManualPredictionEngine(Map<String, ManualGamePrediction> predictions) {
		gPredictions = predictions;
	}

	@Override
	public Pair<Integer, Integer> getScoresForTeams(TeamInfo teamOne, TeamInfo teamTwo) {

		if (byeWeeks.contains(teamOne.getTeamName())) {
			if (byeWeeks.contains(teamTwo.getTeamName())) {
				// In case a div has two bye weeks.
				return new Pair<Integer, Integer>(0, 0);
			}
			else {
				// First team is a bye week team, give the 1 score win to second team.
				return new Pair<Integer, Integer>(0, 1);
			}
		}
		else if (byeWeeks.contains(teamTwo.getTeamName())) {
			// Second team is a bye week.
			return new Pair<Integer, Integer>(1, 0);
		}

		String key = ManualGamePrediction.getMatchKey(teamOne.getTeamName(), teamTwo.getTeamName());

		ManualGamePrediction prediction = gPredictions.get(key);

		double teamOneBreakPoint = prediction.getChanceOne();
		double teamTwoBreakPoint = 100.0 - prediction.getChanceTwo();

		double result = RandomUtils.rollOneHundred();

		int scoreOne = RandomUtils.getRandomScore();
		int scoreTwo = RandomUtils.getRandomScore();
		while (scoreOne == scoreTwo) {
			scoreTwo = RandomUtils.getRandomScore();
		}

		int winningScore = Math.max(scoreOne, scoreTwo);
		int losingScore = Math.min(scoreOne, scoreTwo);

		if (result < teamOneBreakPoint) {
			// Team One wins
			return new Pair<Integer, Integer>(winningScore, losingScore);
		}
		else if (result > teamTwoBreakPoint) {
			// Team Two Wins
			return new Pair<Integer, Integer>(losingScore, winningScore);
		}
		else {
			// Tie
			return new Pair<Integer, Integer>(winningScore, winningScore);
		}
	}

}

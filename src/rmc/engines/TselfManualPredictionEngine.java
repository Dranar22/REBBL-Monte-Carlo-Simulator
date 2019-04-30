package rmc.engines;

import java.util.Map;

import javafx.util.Pair;
import rmc.data.ManualGamePrediction;
import rmc.data.TeamInfo;
import rmc.utils.RandomUtils;

public class TselfManualPredictionEngine implements AbstractMonteCarloEngine {

	Map<String, ManualGamePrediction> gPredictions;

	public TselfManualPredictionEngine(Map<String, ManualGamePrediction> predictions) {
		gPredictions = predictions;
	}

	@Override
	public Pair<Integer, Integer> getScoresForTeams(TeamInfo teamOne, TeamInfo teamTwo) {
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

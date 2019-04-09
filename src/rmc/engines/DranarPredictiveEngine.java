package rmc.engines;

import java.util.Random;

import javafx.util.Pair;
import rmc.data.TeamInfo;

public class DranarPredictiveEngine implements AbstractMonteCarloEngine {

	@Override
	public Pair<Integer, Integer> getScoresForTeams(TeamInfo teamOne, TeamInfo teamTwo) {
		Random random = new Random();

		double teamOneAvgTDsScored = teamOne.getTDsScored() / (double) teamOne.getGamesPlayed();
		double teamOneAvgTDsGiven = teamOne.getTDsGiven() / (double) teamOne.getGamesPlayed();
		double teamTwoAvgTDsScored = teamTwo.getTDsScored() / (double) teamTwo.getGamesPlayed();
		double teamTwoAvgTDsGiven = teamTwo.getTDsGiven() / (double) teamTwo.getGamesPlayed();

		double teamOneExpectedScore = (teamOneAvgTDsScored + teamTwoAvgTDsGiven) / 2.0;
		double teamTwoExpectedScore = (teamTwoAvgTDsScored + teamOneAvgTDsGiven) / 2.0;

		double teamOneAdjustedScore = teamOneExpectedScore + (2 * random.nextGaussian());
		double teamTwoAdjustedScore = teamTwoExpectedScore + (2 * random.nextGaussian());

		int teamOneTDs = (int) Math.round(teamOneAdjustedScore);
		if (teamOneTDs < 0) {
			teamOneTDs = 0;
		}

		int teamTwoTDs = (int) Math.round(teamTwoAdjustedScore);
		if (teamTwoTDs < 0) {
			teamTwoTDs = 0;
		}

		return new Pair<Integer, Integer>(teamOneTDs, teamTwoTDs);
	}

}
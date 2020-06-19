package rmc.engines;

import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import rmc.data.TeamInfo;

public class DranarPredictiveEngine extends AbstractMonteCarloEngine {

	@Override
	public Pair<Integer, Integer> getScoresForTeams(TeamInfo teamOne, TeamInfo teamTwo) {

		if (byeWeeks.contains(teamOne.getTeamName())) {
			if (byeWeeks.contains(teamTwo.getTeamName())) {
				// In case a div has two bye weeks.
				return new ImmutablePair<Integer, Integer>(0, 0);
			}
			else {
				// First team is a bye week team, give the 1 score win to second team.
				return new ImmutablePair<Integer, Integer>(0, 1);
			}
		}
		else if (byeWeeks.contains(teamTwo.getTeamName())) {
			// Second team is a bye week.
			return new ImmutablePair<Integer, Integer>(1, 0);
		}

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

		return new ImmutablePair<Integer, Integer>(teamOneTDs, teamTwoTDs);
	}

}
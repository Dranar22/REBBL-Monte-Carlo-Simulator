package rmc.engines;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import rmc.data.TeamInfo;
import rmc.utils.RandomUtils;

public class AllRandomEngine extends AbstractMonteCarloEngine {

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

		return new ImmutablePair<Integer, Integer>(RandomUtils.getRandomScore(), RandomUtils.getRandomScore());
	}

}
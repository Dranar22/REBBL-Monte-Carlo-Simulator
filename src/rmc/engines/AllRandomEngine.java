package rmc.engines;

import javafx.util.Pair;
import rmc.data.TeamInfo;
import rmc.utils.RandomUtils;

public class AllRandomEngine implements AbstractMonteCarloEngine {

	@Override
	public Pair<Integer, Integer> getScoresForTeams(TeamInfo teamOne, TeamInfo teamTwo) {
		return new Pair<Integer, Integer>(RandomUtils.getRandomScore(), RandomUtils.getRandomScore());
	}

}
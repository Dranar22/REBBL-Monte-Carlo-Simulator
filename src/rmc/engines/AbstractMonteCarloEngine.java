package rmc.engines;

import javafx.util.Pair;
import rmc.data.TeamInfo;

public interface AbstractMonteCarloEngine {

	public Pair<Integer, Integer> getScoresForTeams(TeamInfo teamOne, TeamInfo teamTwo);

}
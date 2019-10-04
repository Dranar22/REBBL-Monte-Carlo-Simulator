package rmc.engines;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;
import rmc.data.TeamInfo;

public abstract class AbstractMonteCarloEngine {

	List<String> byeWeeks = new ArrayList<String>();

	public abstract Pair<Integer, Integer> getScoresForTeams(TeamInfo teamOne, TeamInfo teamTwo);

	public void setByeWeeks(List<String> byeWeeks) {
		this.byeWeeks = byeWeeks;
	}

}
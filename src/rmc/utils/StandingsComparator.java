package rmc.utils;

import java.util.Comparator;

import rmc.data.TeamInfo;

public class StandingsComparator implements Comparator<TeamInfo> {

	@Override
	public int compare(TeamInfo o1, TeamInfo o2) {
		int ret = 0;

		// First check Standings points (3 for win, 1 for tie, 0 for loss)
		ret = Integer.compare(o1.getStandingPoints(), o2.getStandingPoints());
		if (ret != 0) {
			// Reverse it because numbers go from low to high
			return -ret;
		}

		// Second check TD-differential
		ret = Integer.compare(o1.getTDDifferential(), o2.getTDDifferential());
		if (ret != 0) {
			// Reverse it because numbers go from low to high
			return -ret;
		}

		// Third check number of losses
		ret = Integer.compare(o1.getLossCount(), o2.getLossCount());
		if (ret != 0) {
			// Reverse it because numbers go from low to high
			return -ret;
		}

		// Fourth check head to head
		if (o2.wasTeamDefeated(o1.getTeamName())) {
			return 1;
		}
		else if (o1.wasTeamDefeated(o2.getTeamName())) {
			return -1;
		}

		// Finally, flip a damn coin.
		if (RandomUtils.flipCoin()) {
			return 1;
		}
		else {
			return -1;
		}
	}

}
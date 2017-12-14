package rmc.data;

import java.util.HashSet;
import java.util.Set;

public class TeamInfo {
	private String teamName;
	private int tdDiff = 0;
	private int standingPoints = 0;
	private int losses = 0;
	private Set<String> defeatedTeams = new HashSet<String>();

	public TeamInfo(String name) {
		teamName = name;
	}

	public String getTeamName() {
		return teamName;
	}

	public void addWin(int diff, String teamName) {
		tdDiff += diff;
		standingPoints += 3;
		defeatedTeams.add(teamName);
	}

	public void addLoss(int diff) {
		tdDiff -= Math.abs(diff);
		losses++;
	}

	public void addTie() {
		standingPoints += 1;
	}

	public int getStandingPoints() {
		return standingPoints;
	}

	public int getTDDifferential() {
		return tdDiff;
	}

	public int getLossCount() {
		return losses;
	}

	public boolean wasTeamDefeated(String teamName) {
		return defeatedTeams.contains(teamName);
	}
}

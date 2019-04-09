package rmc.data;

import java.util.HashSet;
import java.util.Set;

public class TeamInfo {
	private String teamName;
	private int tdsScored = 0;
	private int tdsAgainst = 0;
	private int standingPoints = 0;
	private int gamesPlayed = 0;
	private int losses = 0;
	private Set<String> defeatedTeams = new HashSet<String>();

	public TeamInfo(String name) {
		teamName = name;
	}

	public String getTeamName() {
		return teamName;
	}

	public void addWin(int tdsScored, int tdsAgainst, String teamName) {
		assert (tdsScored > tdsAgainst);
		this.tdsScored += tdsScored;
		this.tdsAgainst += tdsAgainst;
		standingPoints += 3;
		gamesPlayed++;
		defeatedTeams.add(teamName);
	}

	public void addLoss(int tdsScored, int tdsAgainst) {
		assert (tdsScored < tdsAgainst);
		this.tdsScored += tdsScored;
		this.tdsAgainst += tdsAgainst;
		losses++;
		gamesPlayed++;
	}

	public void addTie(int tdsScored, int tdsAgainst) {
		assert (tdsScored == tdsAgainst);
		this.tdsScored += tdsScored;
		this.tdsAgainst += tdsAgainst;
		standingPoints += 1;
		gamesPlayed++;
	}

	public int getStandingPoints() {
		return standingPoints;
	}

	public int getTDDifferential() {
		return tdsScored - tdsAgainst;
	}

	public int getTDsScored() {
		return tdsScored;
	}

	public int getTDsGiven() {
		return tdsAgainst;
	}

	public int getLossCount() {
		return losses;
	}

	public boolean wasTeamDefeated(String teamName) {
		return defeatedTeams.contains(teamName);
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}
}

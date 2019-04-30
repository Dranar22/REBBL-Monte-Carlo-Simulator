package rmc.data;

public class ManualGamePrediction {

	private String teamOne;
	private String teamTwo;
	private double chanceOne = 33.33;
	private double chanceTwo = 33.33;

	public ManualGamePrediction(String teamOne, String teamTwo) {
		this.teamOne = teamOne;
		this.teamTwo = teamTwo;
	}

	public String getTeamOne() {
		return teamOne;
	}

	public String getTeamTwo() {
		return teamTwo;
	}

	public void setChanceOne(double chance) {
		chanceOne = chance;
	}

	public double getChanceOne() {
		return chanceOne;
	}

	public void setChanceTwo(double chance) {
		chanceTwo = chance;
	}

	public double getChanceTwo() {
		return chanceTwo;
	}

	public String getMatchKey() {
		return getMatchKey(teamOne, teamTwo);
	}

	public static String getMatchKey(String teamOne, String teamTwo) {
		return teamOne + "-" + teamTwo;
	}
}
package rmc.data;

public class MatchResult implements Cloneable {

	private String teamOne;
	private String teamTwo;
	private int scoreOne = -1;
	private int scoreTwo = -1;
	private boolean processed = false;

	public MatchResult(String teamOne, int scoreOne, int scoreTwo, String teamTwo) {
		this.teamOne = teamOne;
		this.teamTwo = teamTwo;
		this.scoreOne = scoreOne;
		this.scoreTwo = scoreTwo;
	}

	public MatchResult(String teamOne, String teamTwo) {
		this.teamOne = teamOne;
		this.teamTwo = teamTwo;
	}

	public String getTeamOne() {
		return teamOne;
	}

	public String getTeamTwo() {
		return teamTwo;
	}

	public void setScoreOne(int score) {
		scoreOne = score;
	}

	public int getScoreOne() {
		return scoreOne;
	}

	public void setScoreTwo(int score) {
		scoreTwo = score;
	}

	public int getScoreTwo() {
		return scoreTwo;
	}

	public boolean hasScores() {
		return ((scoreOne != -1) && (scoreTwo != -1));
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed() {
		processed = true;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new MatchResult(new String(teamOne), scoreOne, scoreTwo, new String(teamTwo));
	}
}

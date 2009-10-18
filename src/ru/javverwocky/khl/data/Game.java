package ru.javverwocky.khl.data;

public class Game {
	private String homeTeam;
	private String awayTeam;
	private String homeTeamScore;
	private String awayTeamScore;
	private String time;
	private String extra;
	private String detailsLink;

	@Override
	public String toString() {
		return "Match [awayTeam=" + awayTeam + ", awayTeamScore="
				+ awayTeamScore + ", homeTeam=" + homeTeam + ", homeTeamScore="
				+ homeTeamScore + ", time=" + time + "]";
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}

	public String getHomeTeamScore() {
		return homeTeamScore;
	}

	public void setHomeTeamScore(String homeTeamScore) {
		this.homeTeamScore = homeTeamScore;
	}

	public String getAwayTeamScore() {
		return awayTeamScore;
	}

	public void setAwayTeamScore(String awayTeamScore) {
		this.awayTeamScore = awayTeamScore;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getDetailsLink() {
		return detailsLink;
	}

	public void setDetailsLink(String detailsLink) {
		this.detailsLink = detailsLink;
	}
}

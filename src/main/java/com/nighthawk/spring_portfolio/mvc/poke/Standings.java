package com.nighthawk.spring_portfolio.mvc.poke;
// NOT IN USE
public class Standings {
	private String teamName;
	private int wins;
	private int losses;
	private double wlPercentage;
	private int pointsFor;
	private int pointsAgainst;
	private int pointsDiff;
	private double marginOfVictory;
	private double SOS;
	private double SRS;
	private double OSRS;
	private double DSRS;
	
	public Standings(String teamName, int wins, int losses, double wlPercentage, int pointsFor, int pointsAgainst,
			int pointsDiff, double marginOfVictory, double sOS, double sRS, double oSRS, double dSRS) {
		super();
		this.teamName = teamName;
		this.wins = wins;
		this.losses = losses;
		this.wlPercentage = wlPercentage;
		this.pointsFor = pointsFor;
		this.pointsAgainst = pointsAgainst;
		this.pointsDiff = pointsDiff;
		this.marginOfVictory = marginOfVictory;
		SOS = sOS;
		SRS = sRS;
		OSRS = oSRS;
		DSRS = dSRS;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getLosses() {
		return losses;
	}
	public void setLosses(int losses) {
		this.losses = losses;
	}
	public double getWlPercentage() {
		return wlPercentage;
	}
	public void setWlPercentage(double wlPercentage) {
		this.wlPercentage = wlPercentage;
	}
	public int getPointsFor() {
		return pointsFor;
	}
	public void setPointsFor(int pointsFor) {
		this.pointsFor = pointsFor;
	}
	public int getPointsAgainst() {
		return pointsAgainst;
	}
	public void setPointsAgainst(int pointsAgainst) {
		this.pointsAgainst = pointsAgainst;
	}
	public int getPointsDiff() {
		return pointsDiff;
	}
	public void setPointsDiff(int pointsDiff) {
		this.pointsDiff = pointsDiff;
	}
	public double getMarginOfVictory() {
		return marginOfVictory;
	}
	public void setMarginOfVictory(double marginOfVictory) {
		this.marginOfVictory = marginOfVictory;
	}
	public double getSOS() {
		return SOS;
	}
	public void setSOS(double sOS) {
		SOS = sOS;
	}
	public double getSRS() {
		return SRS;
	}
	public void setSRS(double sRS) {
		SRS = sRS;
	}
	public double getOSRS() {
		return OSRS;
	}
	public void setOSRS(double oSRS) {
		OSRS = oSRS;
	}
	public double getDSRS() {
		return DSRS;
	}
	public void setDSRS(double dSRS) {
		DSRS = dSRS;
	}
}

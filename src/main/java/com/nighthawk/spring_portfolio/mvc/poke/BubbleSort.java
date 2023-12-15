package com.nighthawk.spring_portfolio.mvc.poke;

public class BubbleSort {
	static void bubbleSort(Standings teamStandings[], String sortKey, boolean ascending) {
		Standings temp;
		boolean swapped;

		// outer loop
		for (int i = 0; i < teamStandings.length - 1; i++) {
			// initialize swapped to false
			swapped = false;

			/*
			 * Tm W L W-L% PF PA PD Mov SoS SRS OSRS DSRS
			 */
			// inner loop
			for (int j = 0; j < teamStandings.length - i - 1; j++) {
				if (sortKey.equals("Tm")) {
					String firstTeamName = "";
					String secondTeamName = "";

					firstTeamName = teamStandings[j].getTeamName();
					secondTeamName = teamStandings[j + 1].getTeamName();

					// if ascending order is requested
					if (ascending) {
						if (firstTeamName.compareTo(secondTeamName) > 0) {
							// Swap teamStandings[j] and teamStandings[j+1]
							temp = teamStandings[j];
							teamStandings[j] = teamStandings[j + 1];
							teamStandings[j + 1] = temp;
							swapped = true;
						}
					} else { // if descending order is requested
						if (firstTeamName.compareTo(secondTeamName) < 0) {
							// Swap teamStandings[j] and teamStandings[j+1]
							temp = teamStandings[j];
							teamStandings[j] = teamStandings[j + 1];
							teamStandings[j + 1] = temp;
							swapped = true;
						}
					}
				} else if (sortKey.equals("W") || sortKey.equals("L") || sortKey.equals("PF") || sortKey.equals("PA")
						|| sortKey.equals("PD")) {
					int firstNum = 0;
					int secondNum = 0;

					if (sortKey.equals("W")) {
						firstNum = teamStandings[j].getWins();
						secondNum = teamStandings[j + 1].getWins();
					} else if (sortKey.equals("L")) {
						firstNum = teamStandings[j].getLosses();
						secondNum = teamStandings[j + 1].getLosses();
					} else if (sortKey.equals("PA")) {
						firstNum = teamStandings[j].getPointsAgainst();
						secondNum = teamStandings[j + 1].getPointsAgainst();
					} else if (sortKey.equals("PD")) {
						firstNum = teamStandings[j].getPointsDiff();
						secondNum = teamStandings[j + 1].getPointsDiff();
					} else if (sortKey.equals("PF")) {
						firstNum = teamStandings[j].getPointsFor();
						secondNum = teamStandings[j + 1].getPointsFor();
					}

					// if ascending order is requested
					if (ascending) {
						if (firstNum > secondNum) {
							// Swap teamStandings[j] and teamStandings[j+1]
							temp = teamStandings[j];
							teamStandings[j] = teamStandings[j + 1];
							teamStandings[j + 1] = temp;
							swapped = true;
						}
					} else { // if descending order is requested
						if (firstNum < secondNum) {
							// Swap teamStandings[j] and teamStandings[j+1]
							temp = teamStandings[j];
							teamStandings[j] = teamStandings[j + 1];
							teamStandings[j + 1] = temp;
							swapped = true;
						}
					}
				} else if (sortKey.equals("W-L") || sortKey.equals("MoV") || sortKey.equals("SoS")
						|| sortKey.equals("SRS") || sortKey.equals("OSRS") || sortKey.equals("DSRS")) {
					double firstNum = 0;
					double secondNum = 0;

					if (sortKey.equals("W-L")) {
						firstNum = teamStandings[j].getWlPercentage();
						secondNum = teamStandings[j + 1].getWlPercentage();
					} else if (sortKey.equals("MoV")) {
						firstNum = teamStandings[j].getMarginOfVictory();
						secondNum = teamStandings[j + 1].getMarginOfVictory();
					} else if (sortKey.equals("SoS")) {
						firstNum = teamStandings[j].getSOS();
						secondNum = teamStandings[j + 1].getSOS();
					} else if (sortKey.equals("SRS")) {
						firstNum = teamStandings[j].getSRS();
						secondNum = teamStandings[j + 1].getSRS();
					} else if (sortKey.equals("OSRS")) {
						firstNum = teamStandings[j].getOSRS();
						secondNum = teamStandings[j + 1].getOSRS();
					} else if (sortKey.equals("DSRS")) {
						firstNum = teamStandings[j].getDSRS();
						secondNum = teamStandings[j + 1].getDSRS();
					}

					// if ascending order is requested
					if (ascending) {
						if (firstNum > secondNum) {
							// Swap teamStandings[j] and teamStandings[j+1]
							temp = teamStandings[j];
							teamStandings[j] = teamStandings[j + 1];
							teamStandings[j + 1] = temp;
							swapped = true;
						}
					} else { // if descending order is requested
						if (firstNum < secondNum) {
							// Swap teamStandings[j] and teamStandings[j+1]
							temp = teamStandings[j];
							teamStandings[j] = teamStandings[j + 1];
							teamStandings[j + 1] = temp;
							swapped = true;
						}
					}
				}
			}

			// break if nothing to swap
			if (!swapped)
				break;
		}
	}

	// print values to show the result
	static void printArray(Standings teamStandings[]) {
		for (Standings standing : teamStandings)
			System.out.println(standing.getTeamName());
	}

	// test program
	public static void main(String args[]) {
		Standings s1 = new Standings("TeamA", 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		Standings s2 = new Standings("TeamB", 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		Standings s3 = new Standings("TeamC", 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		Standings s4 = new Standings("TeamD", 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		Standings s5 = new Standings("TeamE", 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		Standings s6 = new Standings("TeamF", 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		Standings teamStandings[] = { s6, s1, s3, s2, s5, s4 };
		BubbleSort.bubbleSort(teamStandings, "Tm", false);
		System.out.println("Sorted: ");
		BubbleSort.printArray(teamStandings);
	}
}

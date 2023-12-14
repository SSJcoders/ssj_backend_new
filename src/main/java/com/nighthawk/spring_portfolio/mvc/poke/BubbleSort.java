package com.nighthawk.spring_portfolio.mvc.poke;

public class BubbleSort {
	static void bubbleSort(Standings teamStandings[], String sortKey, boolean ascending) {
		Standings temp;
		boolean swapped;
		
		for (int i = 0; i < teamStandings.length - 1; i++) {
			swapped = false;
			
			for (int j = 0; j < teamStandings.length - i - 1; j++) {
				
				if (ascending) {
					if (teamStandings[j].getWins() > teamStandings[j + 1].getWins()) {

						// Swap teamStandings[j] and teamStandings[j+1]
						temp = teamStandings[j];
						teamStandings[j] = teamStandings[j + 1];
						teamStandings[j + 1] = temp;
						swapped = true;
					}
				} else {
					if (teamStandings[j].getWins() < teamStandings[j + 1].getWins()) {

						// Swap teamStandings[j] and teamStandings[j+1]
						temp = teamStandings[j];
						teamStandings[j] = teamStandings[j + 1];
						teamStandings[j + 1] = temp;
						swapped = true;
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
		BubbleSort.bubbleSort(teamStandings, "wins", false);
		System.out.println("Sorted: ");
		BubbleSort.printArray(teamStandings);
	}
}

package com.example.demo;

import com.nighthawk.spring_portfolio.mvc.poke.NFLStandingsController;

public class HelloWorld {
	public static void main(String[] args) {
		System.out.println("Hello World");

		NFLStandingsController c = new NFLStandingsController();
		System.out.println(c.getSortedData("W", "b", "c"));
	}
}

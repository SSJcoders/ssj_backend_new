package com.nighthawk.spring_portfolio.mvc.poke;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Test {
	
	public static void main (String[] args) throws IOException {
		URL path = Test.class.getResource("nflstandings.json");
		File f = new File(path.getFile());
		System.out.println(f.getAbsolutePath());
	}

}

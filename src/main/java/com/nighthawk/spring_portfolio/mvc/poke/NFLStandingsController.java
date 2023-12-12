package com.nighthawk.spring_portfolio.mvc.poke;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // annotation to create a RESTful web services
@RequestMapping("/api/nflstandings")
public class NFLStandingsController extends PokeAbstractController {

	private JSONObject body; // last run result
	private HttpStatus status; // last run status

	// GET NFL Stats - unsorted
	@GetMapping("/") // added to end of prefix as endpoint
	@CrossOrigin(origins = "http://localhost:8080")
	public ResponseEntity<JSONObject> getUnsortedData() {
		return null;
	}

	// cached data to avoid paid api calls to get fresh data
	public static String NFL_STANDINGS = "";
	static {
		try {
			URL path = Test.class.getResource("nflstandings.json");
			File f = new File(path.getFile());
			BufferedReader br = new BufferedReader(new FileReader(f));
			String nflData = br.readLine();
			NFL_STANDINGS = nflData;
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	// GET NFL Stats - sorted
	@GetMapping(value = { "/sorted/", "/sorted/{sortKey}/{sortOrder}/{algorithm}" })
	public ResponseEntity<JSONObject> getSortedData(@PathVariable(required = false) String sortKey,
			@PathVariable(required = false) String sortOrder, @PathVariable(required = false) String algorithm) {

		try { 
			if (NFL_STANDINGS.length() == 0) {
				readNFLData();
			}

			JSONObject originalBody = (JSONObject) new JSONParser().parse(NFL_STANDINGS);

			// Extracting the list of NFL Team stats
			JSONObject teamStats = (JSONObject) originalBody.get("stats");
			Set teams = teamStats.keySet();

			for (Object team : teams) {
				System.out.println(team);
				break;
			}

			// this.body = (JSONObject) new JSONParser().parse(response.body());
			// System.out.println(body);
			this.status = HttpStatus.OK;
		} catch (Exception e) { // capture failure info
			HashMap<String, String> status = new HashMap<>();
			status.put("status", "RapidApi failure: " + e);

			// Setup object for error
			// this.body = (JSONObject) status;
			this.status = HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		// return JSONObject in RESTful style
		return new ResponseEntity<>(body, status);
	}

	private void readNFLData() throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://nfl-team-stats1.p.rapidapi.com/teamStats"))
				.header("X-RapidAPI-Key", "3aa60a1cb9msh07c4f9a1dcbe87bp1f7a8fjsne20cc9c3e9f1")
				.header("X-RapidAPI-Host", "nfl-team-stats1.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody()).build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		NFL_STANDINGS = response.body();
	}

	public static void main(String args) {
		NFLStandingsController c = new NFLStandingsController();
		c.getSortedData("a", "b", "c");
	}

}

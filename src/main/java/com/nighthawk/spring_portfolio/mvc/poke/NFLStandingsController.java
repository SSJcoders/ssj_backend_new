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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONArray;
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
	// cached data to avoid paid api calls to get fresh data
	public static String NFL_STANDINGS = "";

	static {
		BufferedReader br = null;
		try {
			URL path = NFLStandingsController.class.getResource("nflstandings.json");
			File f = new File(path.getFile());
			br = new BufferedReader(new FileReader(f));
			String nflData = br.readLine();
			NFL_STANDINGS = nflData;
			System.out.println("Read " + NFL_STANDINGS.length() + " bytes from cache");
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private JSONObject body = new JSONObject(); // last run result
	private HttpStatus status; // last run status

	// GET NFL Stats - unsorted
	@GetMapping(value = { "/", "" }) // added to end of prefix as endpoint
	@CrossOrigin(origins = "http://localhost:8080")
	public ResponseEntity<JSONObject> getUnsortedData() {
		try {
			if (NFL_STANDINGS.length() == 0) {
				readNFLData();
			}

			JSONObject originalBody = (JSONObject) new JSONParser().parse(NFL_STANDINGS);
			JSONObject stats = (JSONObject) originalBody.get("stats");
			Set<Entry<String, Object>> teams = stats.entrySet();
			JSONArray teamsStandings = new JSONArray();

			for (Entry<String, Object> team : teams) {
				String teamName = team.getKey();
				// System.out.println("Team Name: " + teamName);
				Object teamStat = team.getValue();
				JSONObject teamStatJSON = (JSONObject) teamStat;
				JSONObject teamStanding = getTeamStanding(teamStatJSON);

				if (teamStanding != null) {
					teamsStandings.add(teamStanding);
				} else {
					System.out.println("Team Name: " + teamName + " are missing standings data.");
				}
			}

			body.put("data", teamsStandings);
			this.status = HttpStatus.OK;
		} catch (Exception e) { // capture failure info
			HashMap<String, String> status = new HashMap<>();
			status.put("status", "Api failure: " + e.getMessage());

			// Setup object for error
			this.body.putAll(status);
			this.status = HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		// return JSONObject in RESTful style
		return new ResponseEntity<>(body, status);
	}

	// GET NFL Stats - sorted
	@GetMapping(value = { "/sorted/", "/sorted", "/sorted/{sortKey}/{sortOrder}/{algorithm}" })
	public ResponseEntity<JSONObject> getSortedData(@PathVariable(required = false) String sortKey,
			@PathVariable(required = false) String sortOrder, @PathVariable(required = false) String algorithm) {

		try {
			if (NFL_STANDINGS.length() == 0) {
				readNFLData();
			}

			JSONObject originalBody = (JSONObject) new JSONParser().parse(NFL_STANDINGS);
			JSONObject stats = (JSONObject) originalBody.get("stats");
			Set<Entry<String, Object>> teams = stats.entrySet();

			Map<String, JSONObject> dataToSort = new TreeMap<String, JSONObject>();

			for (Entry<String, Object> team : teams) {
				String teamName = team.getKey();
				// System.out.println("Team Name: " + teamName);
				Object teamStat = team.getValue();
				JSONObject teamStatJSON = (JSONObject) teamStat;
				JSONObject teamStanding = getTeamStanding(teamStatJSON);

				if (teamStanding != null) {
					// teamsStandings.add(teamStanding);
					dataToSort.put(teamName, teamStanding);

				} else {
					System.out.println("Team Name: " + teamName + " are missing standings data.");
				}
			}

			JSONArray teamsStandings = new JSONArray();

			for (Entry<String, JSONObject> entry : dataToSort.entrySet()) {
				teamsStandings.add(entry.getValue());
			}

			if (sortKey != null) {
				System.out.println("Sorting on: " + sortKey + ". Sort Order: " + sortOrder);

				Collections.sort(teamsStandings, new Comparator<JSONObject>() {

					public int compare(JSONObject o1, JSONObject o2) {
						String tempSortKey = sortKey;
						
						if (tempSortKey.equals("W-L")) {
							tempSortKey = "W-L%";
						}
						
						String firstVal = (String) o1.get(tempSortKey);
						String secondVal = (String) o2.get(tempSortKey);

						// handle int types
						if (sortKey.equals("W") || sortKey.equals("L") || sortKey.equals("PF") || sortKey.equals("PA")
								|| sortKey.equals("PD")) {
							int secondValue = Integer.parseInt(secondVal);
							int firstValue = Integer.parseInt(firstVal);
							
							if (sortOrder.equals("DESC"))
								return Integer.compare(secondValue, firstValue);
							else
								return Integer.compare(firstValue, secondValue);
						}
						// handle double types
						else if (sortKey.equals("W-L") || sortKey.equals("Mov") || sortKey.equals("SoS")
								|| sortKey.equals("SRS") || sortKey.equals("OSRS") || sortKey.equals("DSRS")) {
							double secondValue = Double.parseDouble(secondVal);
							double firstValue = Double.parseDouble(firstVal);
							
							if (sortOrder.equals("DESC"))
								return Double.compare(secondValue, firstValue);
							else 
								return Double.compare(firstValue, secondValue);
						}

						// handle String types
						if (sortOrder.equals("DESC"))
							return secondVal.compareTo(firstVal);
						else
							return firstVal.compareTo(secondVal);
					}
				});
			}
			//System.out.println(teamsStandings.toJSONString());
			body.put("data", teamsStandings);
			this.status = HttpStatus.OK;
		} catch (Exception e) { // capture failure info
			HashMap<String, String> status = new HashMap<>();
			status.put("status", "Api failure: " + e.getMessage());

			// Setup object for error
			this.body.putAll(status);
			this.status = HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		// return JSONObject in RESTful style
		return new ResponseEntity<>(body, status);
	}

	/**
	 * Retrieve Standings from the team stats
	 * 
	 * @param teamStatJSON
	 * @return
	 */
	private JSONObject getTeamStanding(JSONObject teamStatJSON) {
		Set<Entry<String, Object>> teamStats = teamStatJSON.entrySet();

		for (Entry<String, Object> teamStat : teamStats) {
			String statName = teamStat.getKey();
			// System.out.println("Stat Name: " + statName);

			if (statName.equals("Standings")) {
				Object teamStandings = teamStat.getValue();
				JSONObject teamStandingsJSON = (JSONObject) teamStandings;
				return teamStandingsJSON;
			}
		}
		return null;
	}

	/**
	 * Read NFL data from the rest end point
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void readNFLData() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://nfl-team-stats1.p.rapidapi.com/teamStats"))
				.header("X-RapidAPI-Key", "3aa60a1cb9msh07c4f9a1dcbe87bp1f7a8fjsne20cc9c3e9f1")
				.header("X-RapidAPI-Host", "nfl-team-stats1.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody()).build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		NFL_STANDINGS = response.body();

		System.out.println("Read " + NFL_STANDINGS.length() + " bytes from NFL Stats API");
	}
}

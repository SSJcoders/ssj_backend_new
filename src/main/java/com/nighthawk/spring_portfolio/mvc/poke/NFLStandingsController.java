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
import java.util.Arrays;
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

	// static block to pre-load the NFL Standings from cached json file
	static {
		BufferedReader br = null;
		try {
			// load the file a resource using NFLStandingsController's classpath
			URL path = NFLStandingsController.class.getResource("nflstandings.json");
			File f = new File(path.getFile());
			// easier and efficient to read a file using a buffered reader
			br = new BufferedReader(new FileReader(f));
			// readline reads a single line from a file.
			// Since the json file has a single line,
			// we do not need to worry about end of file or try to read past first line
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
	@CrossOrigin(origins = "http://localhost:8080") // do not think this is needed anymore
	public ResponseEntity<JSONObject> getUnsortedData() {
		try {

			// if the cached file doesn't exist or is empty,
			// then read the data from the NFL Standings REST endpoint
			// do this only when needed
			// put the read data in cache for future use
			if (NFL_STANDINGS.length() == 0) {
				readNFLData();
				writeNFLDataToCache();
			}

			// read NFL Standings JSON as JSONObject
			JSONObject originalBody = (JSONObject) new JSONParser().parse(NFL_STANDINGS);

			// read stats element
			JSONObject stats = (JSONObject) originalBody.get("stats");

			// under stats, there are stats for each team. Read that
			// as a set of key value pairs. Where key is the team name
			// value is a JSON Object, which contains all different types of stats
			Set<Entry<String, Object>> teams = stats.entrySet();
			JSONArray teamsStandings = new JSONArray();

			for (Entry<String, Object> team : teams) {
				// key is the team's name
				String teamName = team.getKey();
				// System.out.println("Team Name: " + teamName);
				// value is the team's stats
				Object teamStat = team.getValue();
				JSONObject teamStatJSON = (JSONObject) teamStat;
				// from all the stats, extract only the Standings data
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

	// TODO - to be implemented using filewriter
	private void writeNFLDataToCache() {
		// TODO Auto-generated method stub

	}

	// GET NFL Stats - sorted
	@SuppressWarnings("unchecked")
	@GetMapping(value = { "/sorted/", "/sorted", "/sorted/{sortKey}/{sortOrder}/{algorithm}" })
	public ResponseEntity<JSONObject> getSortedData(@PathVariable(required = false) String sortKey,
			@PathVariable(required = false) String sortOrder, @PathVariable(required = false) String algorithm) {
		// to track how long it took to sort
		long sortTime = 0l;
		
		try {
			// if the cached file doesn't exist or is empty,
			// then read the data from the NFL Standings REST endpoint
			// do this only when needed
			// put the read data in cache for future use
			if (NFL_STANDINGS.length() == 0) {
				readNFLData();
			}

			// read NFL Standings JSON as JSONObject
			JSONObject originalBody = (JSONObject) new JSONParser().parse(NFL_STANDINGS);

			// read stats element
			JSONObject stats = (JSONObject) originalBody.get("stats");

			// under stats, there are stats for each team. Read that
			// as a set of key value pairs. Where key is the team name
			// value is a JSON Object, which contains all different types of stats
			Set<Entry<String, Object>> teams = stats.entrySet();

			// treemap to sort the data based on the team name by default
			Map<String, JSONObject> dataToSort = new TreeMap<String, JSONObject>();

			for (Entry<String, Object> team : teams) {
				// key is the team's name
				String teamName = team.getKey();
				// System.out.println("Team Name: " + teamName);
				// value is the team's stats
				Object teamStat = team.getValue();
				JSONObject teamStatJSON = (JSONObject) teamStat;
				// from all the stats, extract only the Standings data
				JSONObject teamStanding = getTeamStanding(teamStatJSON);

				if (teamStanding != null) {
					// teamsStandings.add(teamStanding);
					// this will sort the data using Team's name by default
					dataToSort.put(teamName, teamStanding);
				} else {
					System.out.println("Team Name: " + teamName + " are missing standings data.");
				}
			}

			// retrieve data in the sorted order in an array
			JSONArray teamsStandings = new JSONArray();

			for (Entry<String, JSONObject> entry : dataToSort.entrySet()) {
				teamsStandings.add(entry.getValue());
			}

			// if sortKey is not null, then we need to sort based on the sort key / sort
			// order
			if (sortKey != null) {
				System.out.println("Sorting on: " + sortKey + ". Sort Order: " + sortOrder);

				// if algorithm is selected as BUBBLE
				if (algorithm.equals("BUBBLE")) {
					// convert JSON array into Object array
					// Standings class is a POJO (Plain Old Java Object)
					Standings[] standings = getStandings(teamsStandings);
					
					long startTime = System.currentTimeMillis();
					
					BubbleSort.bubbleSort(standings, sortKey, sortOrder.equals("ASC"));
					
					sortTime = System.currentTimeMillis() - startTime; 
					// convert the object array into JSON Array
					teamsStandings = getJsonArrayFromStandingsData(standings);
				} else { // this is java default using collections.sort
					// Collections.sort will sort based on the natural order
					long startTime = System.currentTimeMillis();
					
					Collections.sort(teamsStandings, new Comparator<JSONObject>() {
						public int compare(JSONObject o1, JSONObject o2) {
							String tempSortKey = sortKey;

							// % char is causing issue, hence this check
							if (tempSortKey.equals("W-L")) {
								tempSortKey = "W-L%";
							}

							String firstVal = (String) o1.get(tempSortKey);
							String secondVal = (String) o2.get(tempSortKey);

							// handle int types
							if (sortKey.equals("W") || sortKey.equals("L") || sortKey.equals("PF")
									|| sortKey.equals("PA") || sortKey.equals("PD")) {
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
					
					sortTime = System.currentTimeMillis() - startTime; 
				}
			}
			// System.out.println(teamsStandings.toJSONString());
			body.put("data", teamsStandings);
			this.status = HttpStatus.OK;
			System.out.println("Sort time: "+ sortTime + " ms for " + algorithm);
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

	private JSONArray getJsonArrayFromStandingsData(Standings[] standings) {
		JSONArray teamsStandings = new JSONArray();
		
		for (Standings aStanding : standings) {
			JSONObject anObject = new JSONObject();
			anObject.put("OSRS", aStanding.getOSRS());
			anObject.put("DSRS", aStanding.getDSRS());
			anObject.put("PA", aStanding.getPointsFor());
			anObject.put("PD", aStanding.getPointsDiff());
			anObject.put("MoV", aStanding.getMarginOfVictory());
			anObject.put("SRS", aStanding.getSRS());
			anObject.put("PF", aStanding.getPointsFor());
			anObject.put("SoS", aStanding.getSOS());
			anObject.put("W", aStanding.getWins());
			anObject.put("W-L%", aStanding.getWlPercentage());
			anObject.put("Tm", aStanding.getTeamName());
			anObject.put("L", aStanding.getLosses());
			
			teamsStandings.add(anObject);
		}
		
		return teamsStandings;
	}

	private Standings[] getStandings(JSONArray teamsStandings) {
		// initialize array
		Standings[] standings = new Standings[teamsStandings.size()];

		// initialize the counter
		int i = 0;

		for (Object obj : teamsStandings) {
			// get JSON object and get individual standing column values
			JSONObject jObj = (JSONObject) obj;
			String teamName = (String) jObj.get("Tm");
			int wins = Integer.parseInt((String) jObj.get("W"));
			int losses = Integer.parseInt((String) jObj.get("L"));
			double wlPercentage = Double.parseDouble((String) jObj.get("W-L%"));
			int pointsFor = Integer.parseInt((String) jObj.get("PF"));
			int pointsAgainst = Integer.parseInt((String) jObj.get("PA"));
			int pointsDiff = Integer.parseInt((String) jObj.get("PD"));
			double marginOfVictory = Double.parseDouble((String) jObj.get("MoV"));
			double SOS = Double.parseDouble((String) jObj.get("SoS"));
			double SRS = Double.parseDouble((String) jObj.get("SRS"));
			double OSRS = Double.parseDouble((String) jObj.get("OSRS"));
			double DSRS = Double.parseDouble((String) jObj.get("DSRS"));
			
			// create the standing object
			Standings aStanding = new Standings(teamName, wins, losses, wlPercentage, pointsFor, pointsAgainst,
					pointsDiff, marginOfVictory, SOS, SRS, OSRS, DSRS);
			
			// set the value at ith index in array and post increment i to next index
			standings[i++] = aStanding;
		}

		return standings;
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

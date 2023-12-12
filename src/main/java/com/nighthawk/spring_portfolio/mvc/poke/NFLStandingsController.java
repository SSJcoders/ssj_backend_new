package com.nighthawk.spring_portfolio.mvc.poke;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
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

	private JSONObject body; // last run result
	private HttpStatus status; // last run status

	// GET NFL Stats - unsorted
	@GetMapping("/") // added to end of prefix as endpoint
	@CrossOrigin(origins = "http://localhost:8080")
	public ResponseEntity<JSONObject> getUnsortedData() {
		return null;
	}
	
	// cached data to avoid paid api calls to get fresh data
	public static final String NFL_STANDINGS = "";
	

	// GET NFL Stats - sorted
	@GetMapping(value = { "/sorted/", "/sorted/{sortKey}/{sortOrder}/{algorithm}" })
	public ResponseEntity<JSONObject> getSortedData(@PathVariable(required = false) String sortKey,
			@PathVariable(required = false) String sortOrder, @PathVariable(required = false) String algorithm) {

		try { // APIs can fail (ie Internet or Service down)

			/*
			 * HttpRequest request = HttpRequest.newBuilder()
			 * .uri(URI.create("https://nfl-team-stats1.p.rapidapi.com/teamStats"))
			 * .header("X-RapidAPI-Key",
			 * "3aa60a1cb9msh07c4f9a1dcbe87bp1f7a8fjsne20cc9c3e9f1")
			 * .header("X-RapidAPI-Host", "nfl-team-stats1.p.rapidapi.com") .method("GET",
			 * HttpRequest.BodyPublishers.noBody()) .build(); HttpResponse<String> response
			 * = HttpClient.newHttpClient().send(request,
			 * HttpResponse.BodyHandlers.ofString());
			 */
			// JSONParser extracts text body and parses to JSONObject
			// JSONObject originalBody = (JSONObject) new
			// JSONParser().parse(response.body());
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

	@GetMapping("/api/nflstandings/sorted") // added to end of prefix as endpoint
	public ResponseEntity<JSONObject> getPokemonInAlphabetOrder() {

		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://pokeapi.co/api/v2/pokemon?limit=5000"))
					.method("GET", HttpRequest.BodyPublishers.noBody()).build();

			HttpResponse<String> response = HttpClient.newHttpClient().send(request,
					HttpResponse.BodyHandlers.ofString());

			// JSONParser extracts text body and parses to JSONObject
			JSONObject originalBody = (JSONObject) new JSONParser().parse(response.body());

			// Extracting the list of Pokemon names
			JSONArray originalPokemonList = (JSONArray) originalBody.get("results");

			Map<String, Map<String, String>> pokemonNames = new TreeMap<String, Map<String, String>>();

			for (Object pokemonObj : originalPokemonList) {
				Map<String, String> dict = new HashMap<>();
				JSONObject pokemon = (JSONObject) pokemonObj;
				String pokemonName = (String) pokemon.get("name");
				String pokemonUrl = (String) pokemon.get("url");
				dict.put("name", pokemonName);
				dict.put("url", pokemonUrl);
				pokemonNames.put(pokemonName, dict);
			}
			System.out.println(pokemonNames);
			// Creating a new JSON object with sorted Pokemon names
			JSONObject sortedBody = new JSONObject();
			sortedBody.put("pokemon", pokemonNames);
			System.out.println(sortedBody);
			this.body = sortedBody;
			this.status = HttpStatus.OK;
		} catch (Exception e) {
			HashMap<String, String> status = new HashMap<>();
			status.put("status", "RapidApi failure: " + e);

			// Setup object for error
			this.body = (JSONObject) status;
			this.status = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		// return JSONObject in RESTful style
		return new ResponseEntity<>(body, status);
	}

	public static void main(String args) {

		NFLStandingsController c = new NFLStandingsController();
		c.getSortedData("a", "b", "c");
	}
	
	
}

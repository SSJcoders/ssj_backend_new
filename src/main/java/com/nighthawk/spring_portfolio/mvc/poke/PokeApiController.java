package com.nighthawk.spring_portfolio.mvc.poke;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // annotation to create a RESTful web services
@RequestMapping("/api/poke")
public class PokeApiController extends PokeAbstractController{

    private JSONObject body = new JSONObject(); //last run result
    private HttpStatus status; //last run status
    
    // GET Covid 19 Stats
    @GetMapping(value = {"","/"})   //added to end of prefix as endpoint
    @CrossOrigin(origins = "http://localhost:8080")
    public ResponseEntity<JSONObject> getUnsortedData() {
         try {  //APIs can fail (ie Internet or Service down)
             
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://pokeapi.co/api/v2/pokemon?limit=5000"))
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

             JSONArray originalPokemonList = (JSONArray) ((JSONObject) new JSONParser().parse(response.body())).get("results");
             //JSONParser extracts text body and parses to JSONObject
             System.out.println(originalPokemonList);
             
             this.body.put("data", originalPokemonList);
             //System.out.println(body);
             this.status = HttpStatus.OK;
         }
         catch (Exception e) {  //capture failure info
            e.printStackTrace();
             HashMap<String, String> status = new HashMap<>();
             status.put("status", "RapidApi failure: " + e);

             //Setup object for error
             this.body.putAll(status);
             this.status = HttpStatus.INTERNAL_SERVER_ERROR;
         }
     //return JSONObject in RESTful style
     return new ResponseEntity<>(body, status);
    }

    // note: retaining alphabet mapping for backward compatibility until view is fixed
    @GetMapping(value = { "/alphabet/","/alphabet","/sorted/","/sorted", "/sorted/{sortKey}/{sortOrder}/{algorithm}" })   //added to end of prefix as endpoint
    @CrossOrigin(origins = "http://localhost:8080")
    public ResponseEntity<JSONObject> getSortedData(@PathVariable(required = false) String sortKey,
			@PathVariable(required = false) String sortOrder, @PathVariable(required = false) String algorithm) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pokeapi.co/api/v2/pokemon?limit=5000"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            
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
            //System.out.println(pokemonNames);
            // Creating a new JSON object with sorted Pokemon names
            
            JSONArray list = new JSONArray();
            //ArrayList<Map<String, String>> values = new ArrayList<Map<String, String>>(pokemonNames.size());

            for (Entry<String, Map<String, String>> entry : pokemonNames.entrySet()) {
                Map<String, String> value = entry.getValue();
                //System.out.println(value);
                Set<Entry<String, String>> aValue = value.entrySet();
                JSONObject anEntry = new JSONObject();
                for (Entry<String, String> theValue : aValue){
                    System.out.println(theValue);
                    anEntry.put(theValue.getKey(), theValue.getValue()); 
                    
                }
                //System.out.println(anEntry.toJSONString());
                list.add(anEntry);
            }

            body.put("data", list);
            System.out.println(body);
            this.status = HttpStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
            HashMap<String, String> status = new HashMap<>();
            status.put("status", "RapidApi failure: " + e);

            // Setup object for error
            this.body.putAll(status);
            this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // return JSONObject in RESTful style
        return new ResponseEntity<>(body, status);
    }
}

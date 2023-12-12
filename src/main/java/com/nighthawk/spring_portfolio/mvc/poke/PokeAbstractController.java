package com.nighthawk.spring_portfolio.mvc.poke;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

public abstract class PokeAbstractController {
	public abstract ResponseEntity<JSONObject> getUnsortedData();
	public abstract ResponseEntity<JSONObject> getSortedData(String sortKey, String sortOrder, String algorithm);
}

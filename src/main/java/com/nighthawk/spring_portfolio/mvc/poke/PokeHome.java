package com.nighthawk.spring_portfolio.mvc.poke;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PokeHome {
	@GetMapping("/")
	@CrossOrigin(origins = "http://localhost:8080")
	public ResponseEntity<JSONObject> getHome() {
		JSONObject body = new JSONObject();
		JSONObject message = new JSONObject();
		message.put("message", "Welcome to Pokemon Application");
		body.put("data", message);
		return new ResponseEntity<>(body, HttpStatus.OK);
	}
}

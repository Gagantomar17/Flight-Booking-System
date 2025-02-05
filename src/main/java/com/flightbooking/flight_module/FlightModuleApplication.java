package com.flightbooking.flight_module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class FlightModuleApplication {
	@GetMapping
	public static void main(String[] args) {
		SpringApplication.run(FlightModuleApplication.class, args);
	}

}

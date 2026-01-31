package com.fleetmanagement.fleetsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FleetSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetSystemApplication.class, args);
	}

}

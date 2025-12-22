package com.gym.club;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GymClubApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymClubApplication.class, args);
	}

}

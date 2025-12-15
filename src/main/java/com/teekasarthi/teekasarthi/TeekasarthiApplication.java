package com.teekasarthi.teekasarthi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TeekasarthiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeekasarthiApplication.class, args);
	}

}

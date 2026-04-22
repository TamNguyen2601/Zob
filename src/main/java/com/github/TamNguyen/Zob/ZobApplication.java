package com.github.TamNguyen.Zob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZobApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZobApplication.class, args);
	}

}

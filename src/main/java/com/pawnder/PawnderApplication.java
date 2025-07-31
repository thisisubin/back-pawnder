package com.pawnder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class PawnderApplication {

	public static void main(String[] args) {
		SpringApplication.run(PawnderApplication.class, args);
	}

}

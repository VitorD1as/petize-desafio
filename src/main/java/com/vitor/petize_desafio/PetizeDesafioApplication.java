package com.vitor.petize_desafio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.vitor.petize_desafio.security.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class PetizeDesafioApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetizeDesafioApplication.class, args);
	}

}

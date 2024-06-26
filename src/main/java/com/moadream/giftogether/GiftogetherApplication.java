package com.moadream.giftogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GiftogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiftogetherApplication.class, args);
	}

}

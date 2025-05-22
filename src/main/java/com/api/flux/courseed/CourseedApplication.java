package com.api.flux.courseed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing
public class CourseedApplication {

	public static void main(String[] args) {
		System.getenv("MONGODB_URI");
		System.getenv("PAYU_API_KEY");
		System.getenv("PAYU_MERCHANT_ID");
		System.getenv("VITE_BASE_URL");
		
		SpringApplication.run(CourseedApplication.class, args);
	}

}

package com.api.flux.courseed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing
public class CourseedApplication {

	public static void main(String[] args) {
		System.getenv("SERVER_PORT");
		System.getenv("MONGODB_URI");
		System.getenv("ADMIN_EMAIL");
		System.getenv("ADMIN_PASS");
		System.getenv("PAYU_API_KEY");
		System.getenv("PAYU_MERCHANT_ID");
		System.getenv("VITE_BASE_URL");
		
		System.out.println(">>> Starting Courseed Application");
		SpringApplication.run(CourseedApplication.class, args);
	}

}

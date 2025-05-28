package ru.osiptsoff.npaws_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class NpawsAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(NpawsAuthApplication.class, args);
	}

}

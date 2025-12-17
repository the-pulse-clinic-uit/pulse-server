package com.pulseclinic.pulse_server;

import com.pulseclinic.pulse_server.security.config.OtpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(OtpProperties.class)
@SpringBootApplication
public class PulseClinicApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PulseClinicApiApplication.class, args);
	}

}

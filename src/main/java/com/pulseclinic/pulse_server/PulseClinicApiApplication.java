package com.pulseclinic.pulse_server;

import com.pulseclinic.pulse_server.security.config.OtpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@EnableAsync
@EnableConfigurationProperties(OtpProperties.class)
@EnableJpaRepositories(basePackages = "com.pulseclinic.pulse_server")
@SpringBootApplication
public class PulseClinicApiApplication {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }

	public static void main(String[] args) {
		SpringApplication.run(PulseClinicApiApplication.class, args);
	}

}

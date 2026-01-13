package com.pulseclinic.pulse_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:3000,https://www.pulse-clinic.xyz,https://pulse-clinic.xyz,https://staff.pulse-clinic.xyz,https://doctor.pulse-clinic.xyz,https://hms.pulse-clinic.xyz}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
    }
}

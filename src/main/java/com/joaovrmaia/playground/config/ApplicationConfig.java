package com.joaovrmaia.playground.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class ApplicationConfig {

    @Value("${application.name}")
    private String APPLICATION_NAME;

    @Value("${application.version}")
    private String APPLICATION_VERSION;

    @Bean
    public String getApplicationName() {
        return Optional.of(APPLICATION_NAME).orElse("unknown");
    }

    @Bean
    public String getApplicationVersion() {
        return Optional.of(APPLICATION_VERSION).orElse("unknown");
    }

}

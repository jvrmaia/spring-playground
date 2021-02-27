package com.joaovrmaia.playground.controller;

import com.joaovrmaia.playground.config.ApplicationConfig;
import com.joaovrmaia.playground.util.System;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


@RestController
public class HomeController {

    private Logger logger = LogManager.getLogger(HomeController.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private ApplicationConfig config;

    @Autowired
    public HomeController(ApplicationConfig config) {
        this.config = config;
    }

    @RequestMapping(value = "/")
    public ResponseEntity home() {
        Map<String, Object> responseData = new LinkedHashMap<String, Object>();
        responseData.put("applicationName", config.getApplicationName());
        responseData.put("applicationVersion", config.getApplicationVersion());
        responseData.put("applicationHost", System.getHost());

        try {
            String json = mapper.writeValueAsString(responseData);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json);
        } catch (IOException e) {
            logger.error("Failed to parse object response", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"" + e.getMessage() +"\"}");
        }
    }

}

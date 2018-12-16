package com.navisow.k8sdemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    private final AtomicInteger counter = new AtomicInteger();

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/")
    public String whereami(@Value("${message.prefix}") String prefix) {
        String host = System.getenv().getOrDefault("HOSTNAME", "localhost");
        logger.info(String.format("Running On: %s | Total Request: %d", host, counter.incrementAndGet()));

        return String.format("%s from %s", prefix, host);
    }

    @RequestMapping("/services")
    public List<String> services() {
        return this.discoveryClient.getServices();
    }

}

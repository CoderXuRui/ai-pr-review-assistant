package com.ai.pr.reviewer.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ai.pr.reviewer")
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
        System.out.println("\n" +
            "╔════════════════════════════════════════════════════════════╗\n" +
            "║                                                            ║\n" +
            "║   🤖 AI PR Review Assistant Web UI is Ready!              ║\n" +
            "║                                                            ║\n" +
            "║   Open: http://localhost:8080                             ║\n" +
            "║                                                            ║\n" +
            "╚════════════════════════════════════════════════════════════╝\n");
    }
}

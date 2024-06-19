package com.dvelenteienko.services.currency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//@EnableWebMvc
public class ClientWebConfig implements WebMvcConfigurer {

//    @Bean
//    public MethodValidationPostProcessor methodValidationPostProcessor() {
//        return new MethodValidationPostProcessor();
//    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/v1/**")
//                .allowedOrigins("http://localhost:3000")
//                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
//                .allowedHeaders("Access-Control-Allow-Origin")
//                .allowCredentials(false).maxAge(3600);
//    }
}

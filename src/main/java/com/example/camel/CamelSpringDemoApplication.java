package com.example.camel;

import com.example.camel.model.Person;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

import static org.apache.camel.builder.ProcessorBuilder.setHeader;

@SpringBootApplication
public class CamelSpringDemoApplication {


    private String contextPath = "/camel";
    private Integer serverPort = 8080;

    public static void main(String[] args) {
        SpringApplication.run(CamelSpringDemoApplication.class, args);
    }


}

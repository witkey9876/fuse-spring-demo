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

/**
 * @see <a href="https://github.com/jboss-fuse/redhat-fuse">redhat-fuse</a>
 */
@SpringBootApplication
public class CamelSpringDemoApplication {



    public static void main(String[] args) {
        SpringApplication.run(CamelSpringDemoApplication.class, args);
    }


}

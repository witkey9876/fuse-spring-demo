package com.example.camel.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ApiConfigure {


    public static final String DEFAULT_HTTP_SCHEME_PREFIX = "http://%s:%s";
    @Autowired
    private ServerProperties serverProperties;


    public String getDefaultLocalFullPath(String api) {
//        String contextPath = serverProperties.getServlet().getContextPath();
        String contextPath = "";
        contextPath = Objects.isNull(contextPath) ? "" : contextPath;
        Integer port = serverProperties.getPort();
        port = Objects.isNull(port) ? 8080 : port;
        return "http://localhost:" + port + contextPath + api;
    }


    public interface APIRoute {
        String INDEX = "/";

        String PERSON_JSON = "/persons/json";
        String PERSON_UPDATE = "/persons/update";
        String PERSON_XML = "/persons/xml";

        String REST_TEST = "/api/person";
    }

}

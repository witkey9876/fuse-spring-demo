package com.example.camel.controller;


import com.example.camel.configuration.ApiConfigure;
import com.example.camel.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


import java.util.Random;
import java.util.UUID;

@RestController
public class IndexController {

    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping(ApiConfigure.APIRoute.INDEX)
    public String index() {
        return "welcome";
    }


    @RequestMapping(ApiConfigure.APIRoute.PERSON_JSON)
    public Person json() {
        return new Person(UUID.randomUUID().toString(), new Random().nextInt(100));
    }


    @RequestMapping(value = ApiConfigure.APIRoute.PERSON_UPDATE, consumes = "application/json")
    public Integer updatePerson(@RequestBody Person person) {
        logger.info("req msg:{}", person);
//        throw new RuntimeException("save failure");
        return 1;
    }

    @RequestMapping(value = ApiConfigure.APIRoute.PERSON_XML, produces = {"application/xml;charset=UTF-8"})
    @ResponseBody
    public Person xml() {
        return new Person(UUID.randomUUID().toString(), new Random().nextInt(100));
    }

    @RequestMapping(ApiConfigure.APIRoute.REST_TEST)
    public Person rest() {
        logger.info("request ...........");
        return new Person(UUID.randomUUID().toString(), new Random().nextInt(100));
    }


    @RequestMapping(ApiConfigure.APIRoute.ORDERS)
    public String order() {
        return "Order1000";
    }


    @RequestMapping(ApiConfigure.APIRoute.ORDERS_SHOW)
    public String orderShow(@PathVariable String id) {
        return "orderId:" + id;
    }


}

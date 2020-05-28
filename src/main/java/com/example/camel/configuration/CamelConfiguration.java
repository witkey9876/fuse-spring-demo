package com.example.camel.configuration;


import com.example.camel.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsMessage;
import org.apache.camel.model.dataformat.JacksonXMLDataFormat;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Configuration
public class CamelConfiguration {

    private static final String VM_BROKER_URL = "vm://localhost";


    @Autowired
    private ApiConfigure apiConfigure;


    /**
     * 配置 camel jms 的消息链接
     *
     * @param camelContext
     * @return ConnectionFactory
     */
    @Bean
    public ConnectionFactory getConnection(@Autowired CamelContext camelContext) {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(VM_BROKER_URL);
        camelContext.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        return connectionFactory;
    }


    public JacksonDataFormat getUnmarshalType(Class<?> clazz) {
        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
        jacksonDataFormat.setUnmarshalType(clazz);
        return jacksonDataFormat;
    }


    /**
     * <step>
     * 1. 启用一个定时器从
     * 2. 访问 http服务
     * 3. 打印log日志
     * <p>
     * 4. 访问 http服务 获取json消息
     * 5. 将 json 反序列为化 bean
     *
     * </step>
     *
     * @see <a href="https://camel.apache.org/components/latest/http-component.html">http-component</a>
     */
    @Component
    public class HttpRoute extends RouteBuilder {


        @Override
        public void configure() throws Exception {

            //https://camel.apache.org/components/latest/timer-component.html
            from("timer:json?repeatCount=5")
                    .to(apiConfigure.getDefaultLocalFullPath(ApiConfigure.APIRoute.INDEX))
                    .log("log:http resp msg ${body}")
                    .end()
                    .to(apiConfigure.getDefaultLocalFullPath(ApiConfigure.APIRoute.PERSON_JSON))
                    .unmarshal(getUnmarshalType(Person.class))
                    .to("jms:queue:createPerson");

        }

    }

    /**
     * <step>
     * 1. 启用一个定时器从
     * 2. 访问 http服务
     * 3. 打印log日志
     * <p>
     * 4. 访问 http服务 获取json消息
     * 5. 将 json 反序列为化 bean
     *
     * </step>
     *
     * @see <a href="https://camel.apache.org/components/latest/http-component.html">http-component</a>
     */
    @Component
    public class XMLToJmsRoute extends RouteBuilder {


        @Override
        public void configure() throws Exception {

            //https://camel.apache.org/components/latest/timer-component.html
            from("timer:xml?repeatCount=15")
                    .to(apiConfigure.getDefaultLocalFullPath(ApiConfigure.APIRoute.PERSON_XML))
                    .unmarshal(new JacksonXMLDataFormat())
                    .to("jms:queue:xmlPersonQueue");


        }

    }

    /**
     * 1. 接受mq消息队列的消息
     * 2.
     *
     * @see <a href="https://camel.apache.org/components/latest/jms-component.html">http-component</a>
     */
    @Component
    public class JmsRoute extends RouteBuilder {


        @Override
        public void configure() throws Exception {

            //https://camel.apache.org/components/latest/jms-component.html
            from("jms:queue:createPerson")
                    .log("log:Consume the createPerson queue message ${body}")
                    .marshal(getUnmarshalType(Person.class))
                    .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                    .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                    // .log("log:JmsRoute----${body}")
                    .to(apiConfigure.getDefaultLocalFullPath(ApiConfigure.APIRoute.PERSON_UPDATE))
                    .log("log: JmsRoute----${body}")
                    .end();

        }
    }


    /**
     * 1. 接受mq消息队列的消息
     * 2. xml to json
     * 3. base ccontent json
     *
     * @see <a href="https://camel.apache.org/components/latest/jms-component.html">http-component</a>
     */
    @Component
    public class XmlToJsonRoute extends RouteBuilder {


        ObjectMapper objectMapper = new ObjectMapper();
        private JsonMapper jsonMapper = new JsonMapper();

        @Override
        public void configure() throws Exception {

            JacksonDataFormat format = new JacksonDataFormat();
            format.setInclude("NON_NULL");
            //https://camel.apache.org/components/latest/jms-component.html
            from("jms:queue:xmlPersonQueue")
                    .log("log:Consume the xmlPersonQueue queue message ${body}")
                    .marshal(format)
                    .log("log:XmlToJsonRoute----${body}")
                    .choice()
                    .when((p) -> than20(p))
                    .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                    .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                    .to(apiConfigure.getDefaultLocalFullPath(ApiConfigure.APIRoute.PERSON_UPDATE))
                    .log("log: XmlToJsonRoute----${body}")
                    .otherwise()
                    .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                    .to(apiConfigure.getDefaultLocalFullPath(ApiConfigure.APIRoute.INDEX))
                    .log("log: XmlToJsonRoute----${body}")
                    .end();

        }

        private boolean than20(Exchange exchange) {

            try {
                Map map = objectMapper.readValue(((byte[]) exchange.getIn().getBody()), Map.class);
                return Integer.parseInt(map.get("age").toString()) > 20;
            } catch (IOException e) {
                return Boolean.FALSE;
            }

        }
    }


    /**
     * 1. 接受mq消息队列的消息
     * 2.
     *
     * @see <a href="https://camel.apache.org/components/latest/rest-component.html">http-component</a>
     */

    @Component
    class RestApi extends RouteBuilder {

        @Override
        public void configure() {


            restConfiguration().contextPath("/camel") //
                    .port(8080)
                    .enableCORS(true)
                    .component("servlet")
                    .bindingMode(RestBindingMode.json)
                    .dataFormatProperty("prettyPrint", "true");

            // http://localhost:8080/camel/person/create
            rest("/person/").description("Teste REST Service")
                    .get("/create")
                    .produces(MediaType.APPLICATION_JSON.getType())
                    .consumes(MediaType.APPLICATION_JSON.getType())
                    .bindingMode(RestBindingMode.auto)
                    .type(Person.class)
                    .enableCORS(true)
                    .to("direct:remoteService");

            from("direct:remoteService")
                    .routeId("direct-route")
                    .tracing()
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            exchange.getIn().setBody(new Person(UUID.randomUUID().toString(), new Random().nextInt(100)));
                        }
                    })
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));
        }
    }
}

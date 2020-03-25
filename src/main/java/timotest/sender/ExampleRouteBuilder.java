package timotest.sender;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.util.Date;

@Component
public class ExampleRouteBuilder extends RouteBuilder {

    private static final String SEND_ROUTE = "direct:send-rest";

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Greeting REST API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiProperty("base.path", "camel/")
                .apiProperty("api.path", "/")
                .apiProperty("host", "")
                .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        from("timer://foo-timer?fixedRate=true&period=60000")
                .routeId("timer-route")
                .process(exchange -> exchange
                        .getIn()
                        .setBody(new GreetingDTO("timed from " + Inet4Address.getLocalHost().getHostName() + " " + new Date())))
                .to(SEND_ROUTE);

        rest("/message")
                .description("Message {message}")
                .get("/{message}").outType(String.class)
                .route()
                .routeId("rest-api-route")
                .process(exchange -> {
                    String msg = String.valueOf(exchange.getIn().getHeader("message"));
                    exchange.getIn().setBody(new GreetingDTO(msg));
                    exchange.getIn().getHeaders().clear();

                })
                .log("sending ${body}")
                .to(SEND_ROUTE);

        rest("/message-sink")
                .description("Used for getting messages...")
                .post()
                .route()
                .routeId("rest-message-sink-route")
                .log("got message:${body}")
                .end();

        from(SEND_ROUTE)
                .routeId("send-rest-route")
                .streamCaching()
                .log("http post called with body ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("log:DEBUG?showBody=true&showHeaders=true")
                .marshal().json(JsonLibrary.Jackson)
                .recipientList(simple("{{target.host}}"))
                .process(exchange -> {
                            log.info("The response code is: {}", exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
                            exchange.getOut().setBody("{}");
                        }
                );
    }
}

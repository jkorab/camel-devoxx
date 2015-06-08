package com.ameliant.devoxx;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jetty.CamelHttpClient;
import org.apache.camel.component.jetty.JettyHttpComponent;
import org.apache.camel.component.jetty.JettyHttpEndpoint;
import org.apache.camel.component.jetty8.JettyHttpComponent8;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class RestJmsProxyRouteTest extends CamelTestSupport {

    @Override
    protected CamelContext createCamelContext() throws Exception {
        DefaultCamelContext camelContext = new DefaultCamelContext();
        camelContext.addComponent("jetty", new JettyHttpComponent8());
        return camelContext;
    }

    @Override
    protected RouteBuilder[] createRouteBuilders() throws Exception {
        return new RouteBuilder[] {
                new RestJmsProxyRoute(),
                new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        int testJettyPort = AvailablePortFinder.getNextAvailable();
                        // Cannot find RestConsumerFactory in Registry or as a Component to use
                        // do this to get the direct test to run
                        // see https://camel.apache.org/rest-dsl.html
                        restConfiguration()
                                .component("jetty")
                                .host("localhost")
                                .port(testJettyPort)
                                .bindingMode(RestBindingMode.auto);

                        // add this to engage the http component
                        // see https://camel.apache.org/http.html
                        // see https://camel.apache.org/jetty.html
                        from("direct:harness")
                                .setHeader(Exchange.HTTP_PATH,
                                        simple("/orders/${header[id]}"))
                                .to("jetty:http://localhost:" + testJettyPort);
                    }
                }
        };
    }

    @Test
    public void testFetchOrders() {
        String response = template.requestBodyAndHeader("direct:fetchOrders", new Object(), "id", 123, String.class);
        assertEquals("I got your order right here: 123", response);
    }

        @Test
    public void testFetchOrdersOverHttp() {
        String response = template.requestBodyAndHeader("direct:harness", null, "id", 345, String.class);
        assertEquals("I got your order right here: 345", response);
    }

}
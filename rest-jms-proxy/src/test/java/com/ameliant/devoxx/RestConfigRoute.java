package com.ameliant.devoxx;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.test.AvailablePortFinder;

/**
 * @author jkorab
 */
public class RestConfigRoute extends RouteBuilder {

    private int jettyPort = AvailablePortFinder.getNextAvailable();

    public int getJettyPort() {
        return jettyPort;
    }

    @Override
    public void configure() throws Exception {
        // Cannot find RestConsumerFactory in Registry or as a Component to use
        // do this to get the direct test to run
        // see https://camel.apache.org/rest-dsl.html
        restConfiguration()
                .component("jetty")
                .host("localhost")
                .port(jettyPort)
                .bindingMode(RestBindingMode.off);
    }

}

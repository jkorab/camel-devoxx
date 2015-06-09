# camel-devoxx
## Hands-on Lab code for "Testable integrations with Apache Camel"

Lab description: http://cfp.devoxx.co.uk/2015/talk/NOL-7004/Testable_integrations_with_Apache_Camel

In this workshop we will develop a simple (but representative) integration for Rest-JMS proxying.
We will then test it via a couple of different strategies using JUnit:

* interacting with routes from outside of Camel
* mocking backend endpoints
* using actual components that communicate via embedded transports with test harnesses, for integration tests that require no physical backends

We will also see how it its possible to use Camel to create backend simulators or stubs, by looking at the details of a simple stub project,
and show how the HawtIO console (http://hawt.io) can be used to get an insight into our integrations at runtime.

The code here is broken down into 3 projects:

* `rest-jms-proxy` - the main project in which we will write our integrations
* `backend-model` - a JAXB annotated POJO model that represents the messages our backend system will use
* `backend-stub` - a webapp project that acts a full system stub for the backend; allows us to see our integrations working via a browser.Also incorporates an embedded ActiveMQ broker listening on port 61616.

To run the embedded Jetty container with our applications, go to the root project and run `mvn jetty:run`.
If you get port conflicts at startup, make sure that there is nothing running on the following ports:

* 8080 - Jetty HTTP port
* 9999 - Jetty "stop" port
* 61616 - ActiveMQ TCP port

We will interact with the test project through the following URLs:

* http://localhost:8080/devoxx/ping - simple route to show a Camel route working in a webapp
* http://localhost:8080/devoxx/orders/123 - proxy route that asks for the details of an order by its id (123)
* http://localhost:8080/hawtio - the HawtIO console

The master branch of this project contains a stripped back version of the code that will be completed during the workshop.
A completed version of this project is available by checking out the `full-version` tag from this repository.

## Need assistance with Apache Camel?

Ameliant is a specialist consultancy based in London that offers Apache Camel, ActiveMQ and ServiceMix consulting services.
As well as helping teams get integration projects going on the right track (and keeping them there),
we deliver commercial training courses that are open-admission, which can be tailored specifically to your team's needs and taught on-site.

* http://ameliant.com/camel
* http://ameliant.com/training

For individual technical questions around Apache Camel, the best places to ask are:

* http://camel.465427.n5.nabble.com/ - the official Apache Camel users mailing list on Nabble
* http://stackoverflow.com - chock full of helpful people including Camel committers

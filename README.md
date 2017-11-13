# TQdev.com's Metrics

This is a light-weight Java library to measure the behavior of critical components in a production environment.

### Modules

- **metrics-jetty**: Instrumentation of the Jetty thread pool and request handler to identify application bottlenecks by status code and method.
- **metrics-aspectj**: Instrumentation of any method using a simple annotation on the class or method using the power of AspectJ weaving.
- **metrics-jmx**: Support for publishing the metrics from instrumented components via JMX.
- **metrics-jvm**: A module for getting memory, disk and CPU statistics from the JVM.
- **metrics-influxdb**: Support for publishing the metrics from instrumented components to InfluxDB.
- **metrics-spring**: Instrumentation of Spring requests to identify application bottlenecks by handler name and path.

### Philosophy

Everything is measured as a long integer, being an increasing value (monotonically increasing counter). 
So instead of measuring 10, 10, 10 for a constant value you will have 10, 20, 30.
This way you can get cheap aggregates (at any given resolution), as you don't have to visit every value.

It uses at max two long integers per metric: one for duration in nanoseconds and one for invocation count.
For on-demand measured values (Gauges) it also uses a long integer to unify the storage model. It is
recommended that you send the metrics every 10 seconds to a time series database such as InfluxDB.
In InfluxDB you can then use the "`non_negative_derivative`" function to graph the measured values.

### Getting started

If you are using Spring you can add a "ComponentScan" annotation to your application to add 
"com.tqdev.metrics.spring.loaders" as a scanned package, as you can see here:

```java
@SpringBootApplication
@ComponentScan({ "org.springframework.samples.petclinic", "com.tqdev.metrics.spring.loaders" })
public class PetClinicApplication {
    public static void main(String[] args) throws Exception {
	    SpringApplication.run(PetClinicApplication.class, args);
    }
}
```

This is all you have to change in your code to get started (apart from adding the dependencies to your
maven or gradle config). After application has started you may connect to it using "jconsole" and see
the collected metrics via JMX under "com.tqdev.metrics".

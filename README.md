[![Build Status](https://travis-ci.org/mevdschee/tqdev-metrics.svg?branch=master)](https://travis-ci.org/mevdschee/tqdev-metrics?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mevdschee/tqdev-metrics/badge.svg?branch=master)](https://coveralls.io/github/mevdschee/tqdev-metrics?branch=master)

# TQdev.com's Metrics

This is a light-weight Java library to measure the behavior of critical components in a production environment.

### Requirements

- Java 8

### Modules

- metrics-core stores your metrics (you always need this module)               |
- metrics-aspectj instruments Java methods aggregated on method name
- metrics-jdbc instruments SQL queries aggregated on prepared statement
- metrics-jetty instruments HTTP requests aggregated on HTTP verb and response status and also instruments current thread counts
- metrics-spring-security instruments Spring requests aggregated on authenticated username
- metrics-spring-webmvc instruments Spring requests aggregated on request path and handler name
- metrics-jvm instruments current JVM system properties
- metrics-sigar _is not implemented yet_
- metrics-influxdb exports metrics to disk (rotated) and HTTP endpoint using the InfluxDB line protocol
- metrics-jmx exports metrics over a JMX connection
- metrics-spring-loaders can be component scanned to load all modules

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

    @SpringBootApplication
    @ComponentScan({ "org.springframework.samples.petclinic", "com.tqdev.metrics.spring.loaders" })
    public class PetClinicApplication {
        public static void main(String[] args) throws Exception {
    	    SpringApplication.run(PetClinicApplication.class, args);
        }
    }

This is all you have to change in your code to get started (apart from adding the dependencies to your
maven or gradle config). After application has started you may connect to it using "jconsole" and see
the collected metrics via JMX under "com.tqdev.metrics".

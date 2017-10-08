# TQdev.com's Metrics

This is a light-weight Java library to measure the behavior of critical components in a production environment.

### Modules

- **metrics-jetty**: Instrumentation of the Jetty thread pool and request handler to identify application bottlenecks by status code, method and path.
- **metrics-aspectj**: Instrumentation of any method using a simple annotation on the class or method using the power of AspectJ weaving.
- **metrics-jmx**: Support for publishing the metrics from instrumented components via JMX.
- **metrics-influxdb**: Support for publishing the metrics from instrumented components to InfluxDB.

### Philosophy

Everything is measured as a long integer, being an increasing value (monotonically increasing counter). 
So instead of measuring 10, 10, 10 for a constant value you will have 10, 20, 30.
This way you can get cheap aggregates (at any given resolution), as you don't have to visit every value.

It uses at max two long integers per metric: one for duration in nanoseconds and one for invocation count.
For on-demand measured values (Gauges) it also uses a long integer to unify the storage model.
If you need historic values you should hook the metrics up to a time series database such as InfluxDB.
In InfluxDB you can then use the "`non_negative_derivative`" function to graph the measures values.

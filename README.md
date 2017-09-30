# tqdev-metrics

TQdev.com's Metrics is a light-weight Java library to measure the behavior of critical components in a production environment.

- **metrics-jetty**: Instrumentation of the Jetty thread pool and request handler to identify application bottlenecks by status code, method and path.
- **metrics-aspectj**: Instrumentation of any method using a simple annotation on the class or method using the power of AspectJ weaving.
- **metrics-jmx**: Support for publishing the metrics from instrumented components via JMX.

package com.tqdev.metrics.jetty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tqdev.metrics.core.MetricRegistry;

public class InstrumentedHandlerTest {

	protected final MetricRegistry registry = spy(MetricRegistry.getInstance());

	protected final HttpClient client = new HttpClient();

	protected final Server server = new Server();

	protected String url = "http://localhost";

	/** The current time . */
	protected long now = 1510373758000000000L;

	@Before
	public void setUp() throws Exception {
		when(registry.getNanos()).thenAnswer(i -> now += 123456789);
		ServerConnector connector = new ServerConnector(server);
		InstrumentedHandler handler = new InstrumentedHandler(registry);
		handler.setHandler(new HelloWorldHandler());
		server.addConnector(connector);
		server.setHandler(handler);
		server.start();
		client.start();
		url += ":" + connector.getLocalPort();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
		client.stop();
	}

	@Test
	public void shouldCreateCounters() {
		for (int responseStatus = 1; responseStatus <= 5; responseStatus++) {
			assertThat(registry.getKeys("jetty.Response.Invocations")).contains(responseStatus + "xx-responses");
			assertThat(registry.getKeys("jetty.Response.Durations")).contains(responseStatus + "xx-responses");
		}
		assertThat(registry.getKeys("jetty.Response.Invocations")).contains("other-responses");
		assertThat(registry.getKeys("jetty.Response.Durations")).contains("other-responses");
		for (HttpMethod method : HttpMethod.values()) {
			String name = method.asString().toLowerCase();
			assertThat(registry.getKeys("jetty.Request.Invocations")).contains(name + "-requests");
			assertThat(registry.getKeys("jetty.Request.Durations")).contains(name + "-requests");
		}
		assertThat(registry.getKeys("jetty.Request.Invocations")).contains("other-requests");
		assertThat(registry.getKeys("jetty.Request.Durations")).contains("other-requests");
		assertThat(registry.getKeys("jetty.Aggregated.Invocations")).contains("requests");
		assertThat(registry.getKeys("jetty.Aggregated.Durations")).contains("requests");
	}

	@Test
	public void shouldCreateGauges() {
		assertThat(registry.getKeys("jetty.Thread.Gauges")).contains("threads");
		assertThat(registry.getKeys("jetty.Thread.Gauges")).contains("idle-threads");
		assertThat(registry.getKeys("jetty.Thread.Gauges")).contains("busy-threads");
		assertThat(registry.getKeys("jetty.Thread.Gauges")).contains("min-threads");
		assertThat(registry.getKeys("jetty.Thread.Gauges")).contains("max-threads");
	}

	@Test
	public void shouldInitializeCounters() {
		for (int responseStatus = 1; responseStatus <= 5; responseStatus++) {
			assertThat(registry.get("jetty.Response.Invocations", responseStatus + "xx-responses")).isEqualTo(0);
			assertThat(registry.get("jetty.Response.Durations", responseStatus + "xx-responses")).isEqualTo(0);
		}
		assertThat(registry.get("jetty.Response.Invocations", "other-responses")).isEqualTo(0);
		assertThat(registry.get("jetty.Response.Durations", "other-responses")).isEqualTo(0);
		for (HttpMethod method : HttpMethod.values()) {
			String name = method.asString().toLowerCase();
			assertThat(registry.get("jetty.Request.Invocations", name + "-requests")).isEqualTo(0);
			assertThat(registry.get("jetty.Request.Durations", name + "-requests")).isEqualTo(0);
		}
		assertThat(registry.get("jetty.Request.Invocations", "other-requests")).isEqualTo(0);
		assertThat(registry.get("jetty.Request.Durations", "other-requests")).isEqualTo(0);
		assertThat(registry.get("jetty.Aggregated.Invocations", "requests")).isEqualTo(0);
		assertThat(registry.get("jetty.Aggregated.Durations", "requests")).isEqualTo(0);
	}

	@Test
	public void shouldMeasureGauges() {
		assertThat(registry.get("jetty.Thread.Gauges", "threads")).isGreaterThan(0);
		assertThat(registry.get("jetty.Thread.Gauges", "idle-threads")).isGreaterThanOrEqualTo(0);
		assertThat(registry.get("jetty.Thread.Gauges", "busy-threads")).isGreaterThanOrEqualTo(0);
		assertThat(registry.get("jetty.Thread.Gauges", "min-threads")).isGreaterThanOrEqualTo(0);
		assertThat(registry.get("jetty.Thread.Gauges", "max-threads")).isGreaterThan(0);
	}

	@Test
	public void shouldMeasureHello() throws InterruptedException, ExecutionException, TimeoutException {
		ContentResponse response = client.GET(url + "/hello");

		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).isEqualTo("Hello World!");
		assertThat(registry.get("jetty.Response.Invocations", "2xx-responses")).isEqualTo(1L);
		assertThat(registry.get("jetty.Response.Durations", "2xx-responses")).isEqualTo(123456789L);
	}

	@Test
	public void shouldMeasureHelloAsync() throws Exception {
		ContentResponse response = client.GET(url + "/hello-async");

		// async would be flaky if you don't wait for server to complete
		server.stop();
		server.join();

		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).isEqualTo("Hello World!");
		assertThat(registry.get("jetty.Response.Invocations", "2xx-responses")).isEqualTo(1L);
		assertThat(registry.get("jetty.Response.Durations", "2xx-responses")).isEqualTo(123456789L);
	}

	@Test
	public void shouldMeasureNotFound() throws InterruptedException, ExecutionException, TimeoutException {
		ContentResponse response = client.GET(url + "/does-not-exist");

		assertThat(response.getStatus()).isEqualTo(404);
		assertThat(response.getContentAsString()).isEqualTo("Not found.");
		assertThat(registry.get("jetty.Response.Invocations", "4xx-responses")).isEqualTo(1L);
		assertThat(registry.get("jetty.Response.Durations", "4xx-responses")).isEqualTo(123456789L);
	}

}

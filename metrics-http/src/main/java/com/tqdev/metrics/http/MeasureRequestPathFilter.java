package com.tqdev.metrics.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.tqdev.metrics.core.MetricRegistry;

public class MeasureRequestPathFilter implements Filter {
	private final MetricRegistry registry;

	/**
	 * The content types for which the path is grouped, e.g: "json|xml|html|csv"
	 */
	private String contentTypes;

	public MeasureRequestPathFilter() {
		this(MetricRegistry.getInstance(), null);
	}

	public MeasureRequestPathFilter(MetricRegistry registry, String contentTypes) {
		this.registry = registry;
		this.contentTypes = contentTypes;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (this.contentTypes == null) {
			this.contentTypes = filterConfig.getInitParameter("contentTypes");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!registry.isEnabled()) {
			chain.doFilter(request, response);
			return;
		}

		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestURI = httpRequest.getRequestURI();
		String contentType = response.getContentType();

		final long startTime = registry.getNanos();
		try {
			chain.doFilter(request, response);
		} finally {
			final long duration = registry.getNanos() - startTime;
			registerMeasurement(requestURI, contentType, duration);
		}
	}

	private void registerMeasurement(String requestURI, String contentType, long duration) {
		final String pathGroup = getPathGroup(requestURI, contentType);
		if (pathGroup != null) {
			registry.increment("http.Path.Invocations", pathGroup);
			registry.add("http.Path.Durations", pathGroup, duration);
		}
	}

	/**
	 * Get a grouping identifier for metrics based on path.
	 *
	 * @param requestURI
	 *            the request URI
	 * @param contentType
	 *            the content type
	 * @return the string
	 */
	private String getPathGroup(String requestURI, String contentType) {
		if (requestURI == null || contentType == null || !contentType.matches(".*(" + contentTypes + ").*")) {
			return "(other)";
		}
		String parts[] = requestURI.split("/");
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")) {
				parts[i] = "(uuid)";
			} else if (parts[i].matches("[a-f0-9]{128}")) {
				parts[i] = "(sha512)";
			} else if (parts[i].matches("[a-f0-9]{64}")) {
				parts[i] = "(sha256)";
			} else if (parts[i].matches("[a-f0-9]{40}")) {
				parts[i] = "(sha1)";
			} else if (parts[i].matches("[a-f0-9]{32}")) {
				parts[i] = "(md5)";
			} else if (parts[i].matches("[^a-zA-Z]+") && parts[i].matches(".*[0-9].*")) {
				parts[i] = "(number)";
			}
		}
		String path = String.join("/", parts);
		if (path.isEmpty()) {
			path = "/";
		}
		return path;
	}
}

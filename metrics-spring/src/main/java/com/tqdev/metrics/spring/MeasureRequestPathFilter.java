/* Copyright (C) 2017 Maurits van der Schee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tqdev.metrics.spring;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.tqdev.metrics.core.MetricRegistry;

public class MeasureRequestPathFilter extends OncePerRequestFilter {

	private final MetricRegistry registry;

	/**
	 * The content types for which the path is grouped, e.g: "json|xml|html|csv"
	 */
	private final String contentTypes;

	/**
	 * Instantiates a new measure request path filter.
	 *
	 * @param registry
	 *            the registry
	 * @param contentTypes
	 *            the content types
	 */
	public MeasureRequestPathFilter(MetricRegistry registry, String contentTypes) {
		this.registry = registry;
		this.contentTypes = contentTypes;
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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final long startTime = registry.getTime();
		filterChain.doFilter(request, response);
		final long duration = registry.getTime() - startTime;

		final String pathGroup = getPathGroup(request.getRequestURI(), response.getContentType());
		if (pathGroup != null) {
			registry.increment("spring.Path.Invocations", pathGroup);
			registry.add("spring.Path.Durations", pathGroup, duration);
		}
	}
}

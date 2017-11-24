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
package com.tqdev.metrics.spring.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tqdev.metrics.core.MetricRegistry;

public class MeasureUserActivityFilter extends OncePerRequestFilter {

	private final MetricRegistry registry;

	/**
	 * Instantiates a new measure request path filter.
	 *
	 * @param registry
	 *            the registry
	 */
	public MeasureUserActivityFilter(MetricRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Get the username to aggregate metrics on.
	 *
	 * @return the string
	 */
	private String getUsername() {
		final SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			final Authentication authentication = context.getAuthentication();
			if (authentication != null) {
				final String username = authentication.getName();
				if (username != null) {
					return username;
				}
			}
		}
		return "(unknown)";
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (!registry.isEnabled()) {
			filterChain.doFilter(request, response);
			return;
		}
		final long startTime = registry.getTime();
		filterChain.doFilter(request, response);
		final long duration = registry.getTime() - startTime;

		final String username = getUsername();
		registry.increment("spring.Username.Invocations", username);
		registry.add("spring.Username.Durations", username, duration);
	}
}

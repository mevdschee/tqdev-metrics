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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class MeasureRequestPathFilterTestBase contains the engine to run the
 * MeasureRequestPathFilter tests.
 */
abstract class MeasureUserActivityFilterTestBase {

	/** The registry. */
	protected final MetricRegistry registry = spy(MetricRegistry.getInstance());

	/** The filter. */
	private final MeasureUserActivityFilter filter = new MeasureUserActivityFilter(registry);

	/**
	 * Simulate a request with authenticated user with specified username for a
	 * specified duration in nanoseconds.
	 *
	 * @param username
	 *            the username
	 * @param durationInNanoseconds
	 *            the duration in nanoseconds
	 */
	protected void request(String username, long durationInNanoseconds) {
		long now = 1510373758000000000L;
		when(registry.getNanos()).thenReturn(now, now + durationInNanoseconds);

		if (username != null) {
			User user = new User(username, "", new ArrayList<GrantedAuthority>());
			Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		try {
			filter.doFilterInternal(mock(HttpServletRequest.class), mock(HttpServletResponse.class),
					mock(FilterChain.class));
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

}

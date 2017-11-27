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
package com.tqdev.metrics.spring.webmvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class MeasureRequestPathFilterTestBase contains the engine to run the
 * MeasureRequestPathFilter tests.
 */
abstract class MeasureRequestPathFilterTestBase {

	/** The registry. */
	protected MetricRegistry registry;

	/** The filter. */
	private MeasureRequestPathFilter filter;

	/**
	 * Initialize.
	 */
	@Before
	public void setUp() {
		registry = spy(new MetricRegistry());
		filter = new MeasureRequestPathFilter(registry, "application/json|text/html|text/xml");
	}

	/**
	 * Gets a mocked request.
	 *
	 * @param requestUri
	 *            the request URI
	 * @return the request
	 */
	private HttpServletRequest getRequest(String requestUri) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn(requestUri);
		return request;
	}

	/**
	 * Gets a mocked response.
	 *
	 * @param contentType
	 *            the content type
	 * @return the response
	 */
	private HttpServletResponse getResponse(String contentType) {
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(contentType);
		return response;
	}

	/**
	 * Simulate a request with an URI and a content type for a specified
	 * duration in nanoseconds.
	 *
	 * @param requestUri
	 *            the request URI
	 * @param contentType
	 *            the content type
	 * @param durationInNanoseconds
	 *            the duration in nanoseconds
	 */
	protected void request(String requestUri, String contentType, long durationInNanoseconds) {
		long now = 1510373758000000000L;
		when(registry.getNanos()).thenReturn(now, now + durationInNanoseconds);
		try {
			filter.doFilterInternal(getRequest(requestUri), getResponse(contentType), mock(FilterChain.class));
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

}
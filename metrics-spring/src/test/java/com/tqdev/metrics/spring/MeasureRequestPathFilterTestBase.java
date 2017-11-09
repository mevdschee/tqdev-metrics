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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class MeasureRequestPathFilterTestBase.
 */
public class MeasureRequestPathFilterTestBase {

	/** Number of nanoseconds in a millisecond. */
	static int NS_IN_MS = 1000000;

	/** The registry. */
	final MetricRegistry registry = MetricRegistry.getInstance();

	/** The filter. */
	final MeasureRequestPathFilter filter = new MeasureRequestPathFilter(registry,
			"application/json|text/html|text/xml");

	/**
	 * The Class FilterChainMock.
	 */
	protected class FilterChainMock implements FilterChain {

		/** The milliseconds to sleep. */
		private int millisecondsToSleep;

		/**
		 * Instantiates a new filter chain mock.
		 *
		 * @param millisToSleep
		 *            the milliseconds to sleep
		 */
		public FilterChainMock(int millisecondsToSleep) {
			this.millisecondsToSleep = millisecondsToSleep;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.servlet.FilterChain#doFilter(javax.servlet.ServletRequest,
		 * javax.servlet.ServletResponse)
		 */
		@Override
		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			try {
				Thread.sleep(millisecondsToSleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the request.
	 *
	 * @param requestUri
	 *            the request uri
	 * @return the request
	 */
	protected HttpServletRequest getRequest(String requestUri) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn(requestUri);
		return request;
	}

	/**
	 * Gets the response.
	 *
	 * @param contentType
	 *            the content type
	 * @return the response
	 */
	protected HttpServletResponse getResponse(String contentType) {
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(contentType);
		return response;
	}

	/**
	 * Simulate a request with an URI and a content type for a specified
	 * duration in milliseconds.
	 *
	 * @param requestUri
	 *            the request URI
	 * @param contentType
	 *            the content type
	 * @param durationInMillis
	 *            the duration in milliseconds
	 */
	protected void request(String requestUri, String contentType, int durationInMilliseconds) {
		try {
			filter.doFilterInternal(getRequest(requestUri), getResponse(contentType),
					new FilterChainMock(durationInMilliseconds));
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
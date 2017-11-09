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

public class MeasureRequestPathFilterTestBase {

	final MetricRegistry registry = MetricRegistry.getInstance();
	final MeasureRequestPathFilter filter = new MeasureRequestPathFilter(registry,
			"application/json|text/html|text/xml");

	protected class FilterChainMock implements FilterChain {

		private int millisToSleep;

		public FilterChainMock(int millisToSleep) {
			this.millisToSleep = millisToSleep;
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			try {
				Thread.sleep(millisToSleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected HttpServletRequest getRequest(String requestUri) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn(requestUri);
		return request;
	}

	protected HttpServletResponse getResponse(String contentType) {
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(contentType);
		return response;
	}

	protected void request(String requestUri, String contentType, int durationInMillis) {
		try {
			filter.doFilterInternal(getRequest(requestUri), getResponse(contentType),
					new FilterChainMock(durationInMillis));
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
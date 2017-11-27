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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.method.HandlerMethod;

import com.tqdev.metrics.core.MetricRegistry;
import com.tqdev.metrics.spring.webmvc.MvcDurationInterceptor;

/**
 * The Class MeasureRequestPathFilterTestBase contains the engine to run the
 * MeasureRequestPathFilter tests.
 */
abstract class MvcDurationInterceptorTestBase {

	/** The registry. */
	protected final MetricRegistry registry = spy(MetricRegistry.getInstance());

	/** The interceptor. */
	private final MvcDurationInterceptor interceptor = spy(new MvcDurationInterceptor(registry));

	/**
	 * Gets a mocked request.
	 *
	 * @param startTime
	 *            the start time of the request
	 * @return the request
	 */
	private HttpServletRequest getRequest(long startTime) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getAttribute("startTime")).thenReturn(startTime);
		return request;
	}

	/**
	 * Simulate a request on an action on a controller for a specified duration
	 * in nanoseconds.
	 *
	 * @param controllerName
	 *            the controller name
	 * @param actionName
	 *            the action name
	 * @param durationInNanoseconds
	 *            the duration in nanoseconds
	 * @throws Exception
	 */
	protected void request(String controllerName, String actionName, long durationInNanoseconds) {
		long now = 1510373758000000000L;
		HttpServletRequest request = getRequest(now);
		when(registry.getNanos()).thenReturn(now + durationInNanoseconds);
		HandlerMethod handlerMethod = mock(HandlerMethod.class);
		doReturn(controllerName).when(interceptor).getControllerName(handlerMethod);
		doReturn(actionName).when(interceptor).getActionName(handlerMethod);
		Object handler = controllerName == null ? null : handlerMethod;
		try {
			interceptor.preHandle(request, null, null);
			interceptor.afterCompletion(request, null, handler, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
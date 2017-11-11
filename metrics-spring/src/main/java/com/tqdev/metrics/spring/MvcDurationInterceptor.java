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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class MvcDurationInterceptor.
 */
public class MvcDurationInterceptor extends HandlerInterceptorAdapter {

	/** The registry. */
	private final MetricRegistry registry;

	/**
	 * Instantiates a new MVC duration interceptor.
	 *
	 * @param registry
	 *            the registry
	 */
	public MvcDurationInterceptor(MetricRegistry registry) {
		this.registry = registry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
	 * preHandle(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		request.setAttribute("startTime", registry.getTime());
		return true;
	}

	/**
	 * Gets the action name from the handler.
	 *
	 * @param handler
	 *            the handler
	 * @return the method name
	 */
	public String getActionName(HandlerMethod handler) {
		return handler.getMethod().getName();
	}

	/**
	 * Gets the controller name from the handler.
	 *
	 * @param handler
	 *            the handler
	 * @return the class name
	 */
	public String getControllerName(HandlerMethod handler) {
		return handler.getMethod().getDeclaringClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
	 * afterCompletion(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object,
	 * java.lang.Exception)
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		final long duration = registry.getTime() - (Long) request.getAttribute("startTime");
		final String name;

		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			name = getControllerName(handlerMethod) + "." + getActionName(handlerMethod);
		} else {
			name = "(other)";
		}

		registry.add("spring.Handler.Durations", name, duration);
		registry.increment("spring.Handler.Invocations", name);
	}
}

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
package com.tqdev.metrics.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class MeasureAspect keeps track of total duration and invocation count of
 * public functions using AspectJ weaving.
 */
@Aspect
public class MeasureAspect {

	/** The registry. */
	private final MetricRegistry registry = MetricRegistry.getInstance();

	/**
	 * Measure the total duration and invocation count of public functions of a
	 * class using the "MeasuredClass" annotation. This method should not be
	 * invoked manually. It should be automatically invoked using AspectJ
	 * weaving.
	 *
	 * @param joinPoint
	 *            the public function of the class on which the measurement is
	 *            executed
	 * @param annotation
	 *            the annotation of the class
	 * @return the return value of the public function that is measured
	 * @throws Throwable
	 *             any exception that may be thrown by the public function that
	 *             is measured
	 */
	/*
	 * Advice for the public methods in a class, where the class is marked with
	 * Annotation "MeasuredClass"
	 */
	@Around("execution(public * *(..)) && @within(annotation) && !annotation(MeasuredMethod)")
	public Object MeasuredClass(final ProceedingJoinPoint joinPoint, final MeasuredClass annotation) throws Throwable {
		long start = System.nanoTime();
		Object result = joinPoint.proceed();
		long duration = System.nanoTime() - start;
		String signature = joinPoint.getSignature().toShortString();
		String category = annotation.category();
		if (category.length() == 0) {
			category = "Uncategorized";
		}
		registry.increment("aspectj." + category + ".Invocations", signature);
		registry.add("aspectj." + category + ".Durations", signature, duration);
		return result;
	}

	/**
	 * Measure the total duration and invocation count of a public function
	 * using the "MeasuredMethod" annotation.
	 *
	 * @param joinPoint
	 *            the public function (the method) on which the measurement is
	 *            executed
	 * @param annotation
	 *            the annotation of the method
	 * @return the return value of the public function that is measured
	 * @throws Throwable
	 *             any exception that may be thrown by the public function that
	 *             is measured
	 */
	/*
	 * Advice for the public methods in a class, where the methods are marked
	 * with Annotation "MeasuredMethod"
	 */
	@Around("execution(public * *(..)) && @annotation(annotation)")
	public Object MeasuredMethod(final ProceedingJoinPoint joinPoint, final MeasuredMethod annotation) throws Throwable {
		long start = System.nanoTime();
		Object result = joinPoint.proceed();
		long duration = System.nanoTime() - start;
		String category = annotation.category();
		if (category.length() == 0) {
			category = "Uncategorized";
		}
		String signature = annotation.signature();
		if (signature.length() == 0) {
			signature = joinPoint.getSignature().toShortString();
		}
		registry.increment("aspectj." + category + ".Invocations", signature);
		registry.add("aspectj." + category + ".Durations", signature, duration);
		return result;
	}

}
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

@Aspect
public class MeasureAspect {

	private final MetricRegistry registry = MetricRegistry.getInstance();

	/*
	 * Advice for the public methods in a class, where the class is marked with
	 * Annotation "MeasureClass"
	 */
	@Around("execution(public * *(..)) && @within(annotation)")
	public Object MeasureClass(final ProceedingJoinPoint joinPoint, final MeasureClass annotation) throws Throwable {
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

	/*
	 * Advice for the public methods in a class, where the methods are marked
	 * with Annotation "MeasureMethod"
	 */
	@Around("execution(public * *(..)) && @annotation(annotation)")
	public Object MeasureMethod(final ProceedingJoinPoint joinPoint, final MeasureMethod annotation) throws Throwable {
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
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * The Class MeasureRequestPathFilterTest.
 */
public class MvcDurationInterceptorTest extends MvcDurationInterceptorTestBase {

	/** Number of nanoseconds in a millisecond. */
	public static long NS_IN_MS = 1000000;

	/**
	 * Initialize.
	 */
	@Before
	public void initialize() {
		registry.reset();
	}

	/**
	 * Should measure root path.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldMeasureRootPath() {
		request("WelcomeController", "welcome", 10 * NS_IN_MS);
		assertThat(registry.get("spring.Handler.Invocations", "WelcomeController.welcome")).isEqualTo(1);
		assertThat(registry.get("spring.Handler.Durations", "WelcomeController.welcome")).isEqualTo(10 * NS_IN_MS);
	}

	/**
	 * Should measure long path.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldBeOtherForUnknownHandler() {
		request(null, null, 15 * NS_IN_MS);
		assertThat(registry.get("spring.Handler.Invocations", "(other)")).isEqualTo(1);
		assertThat(registry.get("spring.Handler.Durations", "(other)")).isEqualTo(15 * NS_IN_MS);
	}

	/**
	 * Should measure very fast reply.
	 */
	@Test
	public void shouldMeasureVeryFastReply() {
		request("WelcomeController", "welcome", 10);
		assertThat(registry.get("spring.Handler.Invocations", "WelcomeController.welcome")).isEqualTo(1);
		assertThat(registry.get("spring.Handler.Durations", "WelcomeController.welcome")).isEqualTo(10);
	}

	/**
	 * Should accept UTF-8 characters in URI.
	 */
	@Test
	public void shouldAcceptUtf8CharactersInControllerAndAction() {
		request("◷", "↶", 15 * NS_IN_MS);
		assertThat(registry.get("spring.Handler.Invocations", "◷.↶")).isEqualTo(1);
		assertThat(registry.get("spring.Handler.Durations", "◷.↶")).isEqualTo(15 * NS_IN_MS);
	}
}
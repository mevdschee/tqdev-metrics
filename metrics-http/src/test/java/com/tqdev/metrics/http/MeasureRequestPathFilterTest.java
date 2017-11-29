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
package com.tqdev.metrics.http;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The Class MeasureRequestPathFilterTest.
 */
public class MeasureRequestPathFilterTest extends MeasureRequestPathFilterTestBase {

	/** Number of nanoseconds in a millisecond. */
	public static long NS_IN_MS = 1000000;

	/**
	 * Should measure root path.
	 */
	@Test
	public void shouldMeasureRootPath() {
		request("/", "text/html", 10 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "/")).isEqualTo(1);
		assertThat(registry.get("http.path.Durations", "/")).isEqualTo(10 * NS_IN_MS);
	}

	/**
	 * Should measure long path.
	 */
	@Test
	public void shouldMeasureLongPath() {
		String url = "/blog/2017-How-to-mix-a-good-Mojito-without-using-a-cocktail-shaker";
		request(url, "text/html", 15 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", url)).isEqualTo(1);
		assertThat(registry.get("http.path.Durations", url)).isEqualTo(15 * NS_IN_MS);
	}

	/**
	 * Should measure very fast reply.
	 */
	@Test
	public void shouldMeasureVeryFastReply() {
		request("/", "text/html", 10);
		assertThat(registry.get("http.path.Invocations", "/")).isEqualTo(1);
		assertThat(registry.get("http.path.Durations", "/")).isEqualTo(10);
	}

	/**
	 * Should unify with and without trailing slash.
	 */
	@Test
	public void shouldUnifyWithAndWithoutTrailingSlash() {
		request("/dir", "text/html", 15 * NS_IN_MS);
		request("/dir/", "text/html", 15 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "/dir")).isEqualTo(2);
		assertThat(registry.get("http.path.Durations", "/dir")).isEqualTo(30 * NS_IN_MS);
	}

	/**
	 * Should accept UTF-8 characters in URI.
	 */
	@Test
	public void shouldAcceptUtf8CharactersInUri() {
		String url = "/submit/name%3A%E7%8E%8B/msg%3AHello%20world!";
		request(url, "text/html", 15 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", url)).isEqualTo(1);
		assertThat(registry.get("http.path.Durations", url)).isEqualTo(15 * NS_IN_MS);
	}

	/**
	 * Should replace numeric path segment at the end.
	 */
	@Test
	public void shouldReplaceNumericPathSegmentAtTheEnd() {
		request("/posts/42", "text/html", 15 * NS_IN_MS);
		request("/posts/321", "text/html; charset=utf-8", 15 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "/posts/(number)")).isEqualTo(2);
		assertThat(registry.get("http.path.Durations", "/posts/(number)")).isEqualTo(30 * NS_IN_MS);
	}

	/**
	 * Should replace numeric path segment at the start.
	 */
	@Test
	public void shouldReplaceNumericPathSegmentAtTheStart() {
		request("/2017/highscores", "text/html", 15 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "/(number)/highscores")).isEqualTo(1);
		assertThat(registry.get("http.path.Durations", "/(number)/highscores")).isEqualTo(15 * NS_IN_MS);
	}

	/**
	 * Should identify images as "other".
	 */
	@Test
	public void shouldIdentifyImagesAsOther() {
		request("/img/logo.svg", "image/svg+xml", 10 * NS_IN_MS);
		request("/img/logo2.svg", "image/svg+xml; charset=utf-8", 10 * NS_IN_MS);
		request("/img/123/logo.png", "image/png", 10 * NS_IN_MS);
		request("/banner_468x60.jpg", "image/jpg", 10 * NS_IN_MS);
		request("/spinner.gif", "image/gif", 10 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "(other)")).isEqualTo(5);
		assertThat(registry.get("http.path.Durations", "(other)")).isEqualTo(50 * NS_IN_MS);
	}

	/**
	 * Should identify JavaScript as "other".
	 */
	@Test
	public void shouldIdentifyJavascriptAsOther() {
		request("/js/jquery.min.js", "application/javascript", 10 * NS_IN_MS);
		request("/drupal.js", "application/javascript", 10 * NS_IN_MS);
		request("a17c46a74a325d95550017961ce57f40.js", "application/javascript; charset=utf-8", 10 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "(other)")).isEqualTo(3);
		assertThat(registry.get("http.path.Durations", "(other)")).isEqualTo(30 * NS_IN_MS);
	}

	/**
	 * Should identify CSS as "other".
	 */
	@Test
	public void shouldIdentifyCssAsOther() {
		request("/css/aui.min.css", "text/css", 10 * NS_IN_MS);
		request("/static/css/styles.js", "text/css", 10 * NS_IN_MS);
		request("/min/", "text/css; charset=utf-8", 10 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "(other)")).isEqualTo(3);
		assertThat(registry.get("http.path.Durations", "(other)")).isEqualTo(30 * NS_IN_MS);
	}

	/**
	 * Should identify number segment.
	 */
	@Test
	public void shouldIdentifyNumberSegment() {
		request("/user/1", "text/html", 10 * NS_IN_MS);
		request("/user/0123", "text/html", 10 * NS_IN_MS);
		request("/user/_12-3(4)5.67", "text/html", 10 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "/user/(number)")).isEqualTo(3);
		assertThat(registry.get("http.path.Durations", "/user/(number)")).isEqualTo(30 * NS_IN_MS);
	}

	/**
	 * Should identify MD5 segment.
	 */
	@Test
	public void shouldIdentifyMd5Segment() {
		request("/images/a17c46a74a325d95550017961ce57f40", "text/html", 10 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "/images/(md5)")).isEqualTo(1);
		assertThat(registry.get("http.path.Durations", "/images/(md5)")).isEqualTo(10 * NS_IN_MS);
	}

	/**
	 * Should identify SHA1 segment.
	 */
	@Test
	public void shouldIdentifySha1Segment() {
		request("/keys/ba1a9cac68dd7ae1966884b0af3ad249916aa1c2", "text/html", 10 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "/keys/(sha1)")).isEqualTo(1);
		assertThat(registry.get("http.path.Durations", "/keys/(sha1)")).isEqualTo(10 * NS_IN_MS);
	}

	/**
	 * Should identify SHA256 segment.
	 */
	@Test
	public void shouldIdentifySha256Segment() {
		request("/keys/22c867c4ec7d0bc1f360337ef62a32d8ed28c7228c8e3181565ce9ba85defa36", "text/html", 10 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "/keys/(sha256)")).isEqualTo(1);
		assertThat(registry.get("http.path.Durations", "/keys/(sha256)")).isEqualTo(10 * NS_IN_MS);
	}

	/**
	 * Should identify SHA512 segment.
	 */
	@Test
	public void shouldIdentifySha512Segment() {
		request("/keys/a0aa121fcd4373a83f60a05b2a42016917b019397bc94825fb09ac2a2a0e25fea147611367badbea781aaf7fd5911a9bca5fcc199c4f307bfcbaa9b2914a838f",
				"text/html", 10 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "/keys/(sha512)")).isEqualTo(1);
		assertThat(registry.get("http.path.Durations", "/keys/(sha512)")).isEqualTo(10 * NS_IN_MS);
	}

	/**
	 * Should identify UUID segment.
	 */
	@Test
	public void shouldIdentifyUuidSegment() {
		request("/applications/e77f8e09-51fc-4d85-8879-82c07d6e7562", "text/html", 10 * NS_IN_MS);
		assertThat(registry.get("http.path.Invocations", "/applications/(uuid)")).isEqualTo(1);
		assertThat(registry.get("http.path.Durations", "/applications/(uuid)")).isEqualTo(10 * NS_IN_MS);
	}
}
package com.tqdev.metrics.spring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class MeasureRequestPathFilterTest extends MeasureRequestPathFilterTestBase {

	static int NS_IN_MS = 1000000;

	@Before
	public void initialize() {
		registry.reset();
	}

	@Test
	public void shouldMeasureRootPath() {
		request("/", "text/html", 10);
		assertThat(registry.get("spring.Path.Invocations", "/")).isEqualTo(1);
		assertThat(registry.get("spring.Path.Durations", "/") / NS_IN_MS).isEqualTo(10);
	}

	@Test
	public void shouldMeasureLongPath() {
		String url = "/blog/2017-How-to-mix-a-good-Mojito-without-using-a-cocktail-shaker";
		request(url, "text/html", 15);
		assertThat(registry.get("spring.Path.Invocations", url)).isEqualTo(1);
		assertThat(registry.get("spring.Path.Durations", url) / NS_IN_MS).isEqualTo(15);
	}

	@Test
	public void shouldUnifyWithAndWithoutTrailingSlash() {
		request("/dir", "text/html", 15);
		request("/dir/", "text/html", 15);
		assertThat(registry.get("spring.Path.Invocations", "/dir")).isEqualTo(2);
		assertThat(registry.get("spring.Path.Durations", "/dir") / NS_IN_MS).isEqualTo(30);
	}

	@Test
	public void shouldAcceptUtf8CharactersInUri() {
		String url = "/submit/name%3A%E7%8E%8B/msg%3AHello%20world!";
		request(url, "text/html", 15);
		assertThat(registry.get("spring.Path.Invocations", url)).isEqualTo(1);
		assertThat(registry.get("spring.Path.Durations", url) / NS_IN_MS).isEqualTo(15);
	}

	@Test
	public void shouldReplaceNumericPathSegmentAtTheEnd() {
		request("/posts/42", "text/html", 15);
		request("/posts/321", "text/html; charset=utf-8", 15);
		assertThat(registry.get("spring.Path.Invocations", "/posts/(number)")).isEqualTo(2);
		assertThat(registry.get("spring.Path.Durations", "/posts/(number)") / NS_IN_MS).isEqualTo(30);
	}

	@Test
	public void shouldReplaceNumericPathSegmentAtTheStart() {
		request("/2017/highscores", "text/html", 15);
		assertThat(registry.get("spring.Path.Invocations", "/(number)/highscores")).isEqualTo(1);
		assertThat(registry.get("spring.Path.Durations", "/(number)/highscores") / NS_IN_MS).isEqualTo(15);
	}

	@Test
	public void shouldIdentifyImagesAsOther() {
		request("/img/logo.svg", "image/svg+xml", 10);
		request("/img/logo2.svg", "image/svg+xml; charset=utf-8", 10);
		request("/img/123/logo.png", "image/png", 10);
		request("/banner_468x60.jpg", "image/jpg", 10);
		request("/spinner.gif", "image/gif", 10);
		assertThat(registry.get("spring.Path.Invocations", "(other)")).isEqualTo(5);
		assertThat(registry.get("spring.Path.Durations", "(other)") / NS_IN_MS).isEqualTo(50);
	}

	@Test
	public void shouldIdentifyJavascriptAsOther() {
		request("/js/jquery.min.js", "application/javascript", 10);
		request("/drupal.js", "application/javascript", 10);
		request("a17c46a74a325d95550017961ce57f40.js", "application/javascript; charset=utf-8", 10);
		assertThat(registry.get("spring.Path.Invocations", "(other)")).isEqualTo(3);
		assertThat(registry.get("spring.Path.Durations", "(other)") / NS_IN_MS).isEqualTo(30);
	}

	@Test
	public void shouldIdentifyCssAsOther() {
		request("/css/aui.min.css", "text/css", 10);
		request("/static/css/styles.js", "text/css", 10);
		request("/min/", "text/css; charset=utf-8", 10);
		assertThat(registry.get("spring.Path.Invocations", "(other)")).isEqualTo(3);
		assertThat(registry.get("spring.Path.Durations", "(other)") / NS_IN_MS).isEqualTo(30);
	}

	@Test
	public void shouldIdentifyNumberSegment() {
		request("/user/1", "text/html", 10);
		request("/user/0123", "text/html", 10);
		request("/user/_12-3(4)5.67", "text/html", 10);
		assertThat(registry.get("spring.Path.Invocations", "/user/(number)")).isEqualTo(3);
		assertThat(registry.get("spring.Path.Durations", "/user/(number)") / NS_IN_MS).isEqualTo(30);
	}

	@Test
	public void shouldIdentifyMd5Segment() {
		request("/images/a17c46a74a325d95550017961ce57f40", "text/html", 10);
		assertThat(registry.get("spring.Path.Invocations", "/images/(md5)")).isEqualTo(1);
		assertThat(registry.get("spring.Path.Durations", "/images/(md5)") / NS_IN_MS).isEqualTo(10);
	}

	@Test
	public void shouldIdentifySha1Segment() {
		request("/keys/ba1a9cac68dd7ae1966884b0af3ad249916aa1c2", "text/html", 10);
		assertThat(registry.get("spring.Path.Invocations", "/keys/(sha1)")).isEqualTo(1);
		assertThat(registry.get("spring.Path.Durations", "/keys/(sha1)") / NS_IN_MS).isEqualTo(10);
	}

	@Test
	public void shouldIdentifySha256Segment() {
		request("/keys/22c867c4ec7d0bc1f360337ef62a32d8ed28c7228c8e3181565ce9ba85defa36", "text/html", 10);
		assertThat(registry.get("spring.Path.Invocations", "/keys/(sha256)")).isEqualTo(1);
		assertThat(registry.get("spring.Path.Durations", "/keys/(sha256)") / NS_IN_MS).isEqualTo(10);
	}

	@Test
	public void shouldIdentifySha512Segment() {
		request("/keys/a0aa121fcd4373a83f60a05b2a42016917b019397bc94825fb09ac2a2a0e25fea147611367badbea781aaf7fd5911a9bca5fcc199c4f307bfcbaa9b2914a838f",
				"text/html", 10);
		assertThat(registry.get("spring.Path.Invocations", "/keys/(sha512)")).isEqualTo(1);
		assertThat(registry.get("spring.Path.Durations", "/keys/(sha512)") / NS_IN_MS).isEqualTo(10);
	}

	@Test
	public void shouldIdentifyUuidSegment() {
		request("/applications/e77f8e09-51fc-4d85-8879-82c07d6e7562", "text/html", 10);
		assertThat(registry.get("spring.Path.Invocations", "/applications/(uuid)")).isEqualTo(1);
		assertThat(registry.get("spring.Path.Durations", "/applications/(uuid)") / NS_IN_MS).isEqualTo(10);
	}
}
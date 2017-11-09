package com.tqdev.metrics.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class MetricRegistryTests {

	final MetricRegistry registry = MetricRegistry.getInstance();

	@Before
	public void initialize() {
		registry.reset();
	}

	@Test
	public void shouldNotHaveTypesWhenCreated() {
		assertThat(registry.getTypes().iterator().hasNext()).isEqualTo(false);
	}

	@Test
	public void shouldHaveTypeAndKeyWhenIncrementing() {
		registry.increment("type", "key");
		assertThat(registry.getTypes().iterator().next()).isEqualTo("type");
		assertThat(registry.getKeys("type").iterator().next()).isEqualTo("key");
	}

	@Test
	public void shouldBeZeroWhenIncrementingAndDecrementing() {
		registry.increment("type", "key");
		assertThat(registry.get("type", "key")).isEqualTo(1);
		registry.decrement("type", "key");
		assertThat(registry.get("type", "key")).isEqualTo(0);
	}

	@Test
	public void shouldBeZeroWhenDecrementingAndIncrementing() {
		registry.decrement("type", "key");
		assertThat(registry.get("type", "key")).isEqualTo(-1);
		registry.increment("type", "key");
		assertThat(registry.get("type", "key")).isEqualTo(0);
	}

	@Test
	public void shouldHaveTypeAndKeyWhenAdding() {
		registry.add("type", "key", 123);
		assertThat(registry.getTypes().iterator().next()).isEqualTo("type");
		assertThat(registry.getKeys("type").iterator().next()).isEqualTo("key");
	}

	@Test
	public void shouldBeZeroWhenAddingNumberPositiveAndNegative() {
		registry.add("type", "key", 123);
		assertThat(registry.get("type", "key")).isEqualTo(123);
		registry.add("type", "key", -123);
		assertThat(registry.get("type", "key")).isEqualTo(0);
	}

	@Test
	public void shouldBeZeroWhenAddingNumberNegativeAndPositive() {
		registry.add("type", "key", -123);
		assertThat(registry.get("type", "key")).isEqualTo(-123);
		registry.add("type", "key", 123);
		assertThat(registry.get("type", "key")).isEqualTo(0);
	}

	@Test
	public void shouldBeMinLongWhenIncrementingMaxLong() {
		registry.set("type", "key", Long.MAX_VALUE);
		registry.increment("type", "key");
		assertThat(registry.get("type", "key")).isEqualTo(Long.MIN_VALUE);
	}

	@Test
	public void shouldBeMaxLongWhenDecrementingMinLong() {
		registry.set("type", "key", Long.MIN_VALUE);
		registry.decrement("type", "key");
		assertThat(registry.get("type", "key")).isEqualTo(Long.MAX_VALUE);
	}

	@Test
	public void shouldBeMinLongWhenAddingOneToMaxLong() {
		registry.add("type", "key", Long.MAX_VALUE);
		registry.add("type", "key", 1);
		assertThat(registry.get("type", "key")).isEqualTo(Long.MIN_VALUE);
	}

	@Test
	public void shouldBeMaxLongWhenAddingMinusOneToMinLong() {
		registry.add("type", "key", Long.MIN_VALUE);
		registry.add("type", "key", -1);
		assertThat(registry.get("type", "key")).isEqualTo(Long.MAX_VALUE);
	}

	@Test
	public void shouldNotHaveTypesWhenReset() {
		registry.increment("type", "key");
		registry.reset();
		assertThat(registry.getTypes().iterator().hasNext()).isEqualTo(false);
	}

	@Test
	public void shouldNotHaveValueWhenReset() {
		registry.increment("type", "key");
		registry.reset();
		Throwable throwable = null;
		try {
			registry.get("type", "key");
		} catch (Exception ex) {
			throwable = ex;
		}
		assertThat(throwable).isNotNull().isInstanceOf(NullPointerException.class);
	}

	@Test
	public void shouldHaveTypeAndKeyWhenSettingLong() {
		registry.set("type", "key", 123);
		assertThat(registry.getTypes().iterator().next()).isEqualTo("type");
		assertThat(registry.getKeys("type").iterator().next()).isEqualTo("key");
	}

	@Test
	public void shouldHaveTypeAndKeyWhenSettingGauge() {
		registry.set("type", "key", () -> 123);
		assertThat(registry.getTypes().iterator().next()).isEqualTo("type");
		assertThat(registry.getKeys("type").iterator().next()).isEqualTo("key");
	}

	@Test
	public void shouldBeNumberWhenSettingLongNumber() {
		registry.set("type", "key", 123);
		assertThat(registry.get("type", "key")).isEqualTo(123);
	}

	@Test
	public void shouldBeNumberWhenSettingGaugeNumber() {
		registry.set("type", "key", () -> 123);
		assertThat(registry.get("type", "key")).isEqualTo(123);
	}

	@Test
	public void shouldBeLastNumberWhenSettingLongNumberTwice() {
		registry.set("type", "key", 123);
		registry.set("type", "key", 1234);
		assertThat(registry.get("type", "key")).isEqualTo(1234);
	}

	@Test
	public void shouldBeLastNumberWhenSettingGaugeNumberTwice() {
		registry.set("type", "key", () -> 123);
		registry.set("type", "key", () -> 1234);
		assertThat(registry.get("type", "key")).isEqualTo(1234);
	}

	@Test
	public void shouldBeLastNumberWhenSettingGaugeNumberAndLongNumber() {
		registry.set("type", "key", () -> 123);
		registry.set("type", "key", 1234);
		assertThat(registry.get("type", "key")).isEqualTo(1234);
	}

	@Test
	public void shouldBeLastNumberWhenSettingLongNumberAndGaugeNumber() {
		registry.set("type", "key", 123);
		registry.set("type", "key", () -> 1234);
		assertThat(registry.get("type", "key")).isEqualTo(1234);
	}

	@Test
	public void shouldBeNextNumberWhenGettingGaugeNumberTwice() {
		registry.set("type", "key", () -> {
			registry.increment("type", "key2");
			return registry.get("type", "key2");
		});
		assertThat(registry.get("type", "key")).isEqualTo(1);
		assertThat(registry.get("type", "key")).isEqualTo(2);
	}

	@Test
	public void shouldThrowExceptionWhenIncrementingGaugeNumber() {
		registry.set("type", "key", () -> 123);
		Throwable throwable = null;
		try {
			registry.increment("type", "key");
		} catch (Exception ex) {
			throwable = ex;
		}
		assertThat(throwable).isNotNull()
				.hasMessageEndingWith("cannot be cast to java.util.concurrent.atomic.LongAdder");
	}

	@Test
	public void shouldThrowExceptionWhenDecrementingGaugeNumber() {
		registry.set("type", "key", () -> 123);
		Throwable throwable = null;
		try {
			registry.decrement("type", "key");
		} catch (Exception ex) {
			throwable = ex;
		}
		assertThat(throwable).isNotNull()
				.hasMessageEndingWith("cannot be cast to java.util.concurrent.atomic.LongAdder");
	}

	@Test
	public void shouldThrowExceptionWhenAddingToGaugeNumber() {
		registry.set("type", "key", () -> 123);
		Throwable throwable = null;
		try {
			registry.add("type", "key", 123);
		} catch (Exception ex) {
			throwable = ex;
		}
		assertThat(throwable).isNotNull()
				.hasMessageEndingWith("cannot be cast to java.util.concurrent.atomic.LongAdder");
	}

	@Test
	public void shouldBeSameReferenceWhenGettingInstance() {
		MetricRegistry metricRegistry = MetricRegistry.getInstance();
		assertThat(metricRegistry == registry).isEqualTo(true);
	}
}

package com.tqdev.metrics.spring.loaders;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.tqdev.metrics.core.MetricRegistry;

@Component
public class MetricRegistryBean {
	@Bean
	MetricRegistry metricRegistry() {
		return MetricRegistry.getInstance();
	}
}

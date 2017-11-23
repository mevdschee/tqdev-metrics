package com.tqdev.metrics.spring.loaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.tqdev.metrics.core.MetricRegistry;
import com.tqdev.metrics.spring.webmvc.MvcDurationInterceptor;

@Configuration
public class HandlerInterceptorLoader extends WebMvcConfigurerAdapter {

	@Autowired
	private MetricRegistry metricRegistry;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new MvcDurationInterceptor(metricRegistry));
	}
}
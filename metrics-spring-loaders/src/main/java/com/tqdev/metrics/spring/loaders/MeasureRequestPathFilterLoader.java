package com.tqdev.metrics.spring.loaders;

import javax.servlet.annotation.WebFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.tqdev.metrics.core.MetricRegistry;
import com.tqdev.metrics.spring.webmvc.MeasureRequestPathFilter;

@Configuration
@WebFilter
public class MeasureRequestPathFilterLoader extends MeasureRequestPathFilter {

	@Autowired
	public MeasureRequestPathFilterLoader(MetricRegistry metricRegistry) {
		super(metricRegistry, "application/json|text/html|text/xml|application/xml");
	}

}

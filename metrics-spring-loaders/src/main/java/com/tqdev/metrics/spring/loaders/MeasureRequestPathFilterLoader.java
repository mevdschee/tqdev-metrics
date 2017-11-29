package com.tqdev.metrics.spring.loaders;

import com.tqdev.metrics.core.MetricRegistry;
import com.tqdev.metrics.http.MeasureRequestPathFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.annotation.WebFilter;

@Configuration
@WebFilter
public class MeasureRequestPathFilterLoader extends MeasureRequestPathFilter {

	@Autowired
	public MeasureRequestPathFilterLoader(MetricRegistry metricRegistry) {
		super(metricRegistry, "application/json|text/html|text/xml|application/xml");
	}

}

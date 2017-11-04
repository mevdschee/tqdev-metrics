package com.tqdev.metrics.spring.loaders;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.tqdev.metrics.core.MetricRegistry;
import com.tqdev.metrics.jdbc.InstrumentedDataSource;

@Component
public class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {

	@Autowired
	private MetricRegistry metricRegistry;

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		if (bean instanceof DataSource) {
			DataSource dataSourceBean = (DataSource) bean;
			InstrumentedDataSource instrumentedDatasource = new InstrumentedDataSource(dataSourceBean, metricRegistry);
			instrumentedDatasource.setMetricsEnabled(true);
			return instrumentedDatasource;
		}
		return bean;
	}
}
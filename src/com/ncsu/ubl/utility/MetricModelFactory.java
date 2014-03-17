package com.ncsu.ubl.utility;

import com.ncsu.jknnl.metrics.CityBlockMetric;
import com.ncsu.jknnl.metrics.EuclidesMetric;
import com.ncsu.jknnl.metrics.MetricModel;


public class MetricModelFactory {
	
	public static MetricModel getMetricModel(String metricModelType)
	{
		MetricModel metricModel = null;
		
		if(metricModelType.equalsIgnoreCase("manhattan"))
		{
			metricModel = new CityBlockMetric();
		}
		else
		{
			metricModel = new EuclidesMetric();
		}
		return metricModel;
	}
}

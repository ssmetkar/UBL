package com.ncsu.ubl.commons;

public enum Metric {
	
	CPU_USAGE("CPU Usage"),
	MEM_USAGE("Memory Usage"),
	NET_IN("Network Input"),
	NET_OUT("Network Output"),
	LOAD_1("Load 1"),
	LOAD_5("Load 5");
	
	private String description;
		
	Metric(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}

package com.ncsu.ubl.models;

import java.util.ArrayList;
import java.util.List;

public class DataFold {
	private ArrayList<String> data = new ArrayList<String>();
	public DataFold(ArrayList<String> data) {
		this.data = data;
	}
	
	public ArrayList<String> getData()
	{
		return this.data;
	}
	
	public void setData(ArrayList<String> data)
	{
		this.data = data;
	}
}

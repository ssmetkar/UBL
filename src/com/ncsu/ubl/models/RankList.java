package com.ncsu.ubl.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RankList {
	
	private int state;
	private ArrayList<Integer> rankList;
	
	public RankList() {
	}
	
	public RankList(int state) {
		this.state = state;
		rankList =new ArrayList<Integer>();
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public ArrayList<Integer> getRankList() {
		return rankList;
	}
	public void setRankList(ArrayList<Integer> rankList) {
		this.rankList = rankList;
	}
}

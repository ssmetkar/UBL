package com.ncsu.ubl.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import com.ncsu.jknnl.network.NetworkModel;
import com.ncsu.jknnl.network.NeuronModel;
import com.ncsu.jknnl.topology.MatrixTopology;
import com.ncsu.jknnl.topology.TopologyModel;
import com.ncsu.ubl.master.Controller;

public class ComputationUtility {

	public static int thresholdValue;
	
	public static int calculateThreshold(NetworkModel networkModel)
	{
		thresholdValue = 0;
		TopologyModel topologyModel = networkModel.getTopology();
		TreeMap<java.lang.Integer, java.lang.Integer> neighbornhood;
		Set<Integer> keySets;
		int neighbourNeuron;
		int manhattanDist[] = new int[networkModel.getNumbersOfNeurons()];
		int tempDistance = 0,neighborDistance = 0;
		
		for(int cnt = 0; cnt < networkModel.getNumbersOfNeurons() ; cnt++)
		{
			keySets = topologyModel.getNeighbourhood(cnt).descendingKeySet();
			Iterator<Integer> setIterator= keySets.iterator();
			neighborDistance = 0;
			while(setIterator.hasNext())
			{
				neighbourNeuron = setIterator.next();
				neighborDistance += calculateDistance(networkModel.getNeuron(cnt),networkModel.getNeuron(neighbourNeuron));
			}
			manhattanDist[cnt] = neighborDistance; 
		}
		
		TreeMap<Integer,Integer> uniqueElementMap = new TreeMap<Integer,Integer>();
		for(int cnt:manhattanDist)
		{
			if(uniqueElementMap.get(cnt)== null)
			{
				uniqueElementMap.put(cnt,1);
			}
		}
		
		ArrayList<Integer> uniqueElements = new ArrayList<Integer>(uniqueElementMap.keySet());
		int percentileIndex = (int)Math.floor(0.85 * uniqueElements.size());
		thresholdValue = uniqueElements.get(percentileIndex);
		return thresholdValue; 
	}
	
	private static int calculateDistance(NeuronModel mainNeuron, NeuronModel neighbourNeuron)
	{
		int sum_of_weights = 0;
		for(int cnt = 0; cnt < mainNeuron.getWeight().length ; cnt++)
		{
			sum_of_weights += Math.abs(mainNeuron.getWeight()[cnt] - neighbourNeuron.getWeight()[cnt]);
		}
		return sum_of_weights;
	}
}
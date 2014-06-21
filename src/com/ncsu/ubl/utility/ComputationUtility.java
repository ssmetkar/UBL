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

/**
 * Helper class to perform computation
 * @author Sarang Metkar
 *
 */
public class ComputationUtility {
	
	/*
	 * Method to calculate threshold value during learning phase
	 */
	public static double calculateThreshold(NetworkModel networkModel)
	{
		double thresholdValue = 0;
		TopologyModel topologyModel = networkModel.getTopology();
		TreeMap<java.lang.Integer, java.lang.Integer> neighbornhood = null;
		Set<Integer> keySets = null;
		int neighbourNeuron;
		double manhattanDist[] = new double[networkModel.getNumbersOfNeurons()];
		double tempDistance = 0,neighborDistance = 0;
		
		for(int cnt = 0; cnt < networkModel.getNumbersOfNeurons() ; cnt++)
		{
			keySets = topologyModel.getNeighbours(cnt).descendingKeySet();
			
			if(keySets != null)
			{
				Iterator<Integer> setIterator= keySets.iterator();
				neighborDistance = 0;
				while(setIterator.hasNext())
				{
					neighbourNeuron = setIterator.next();
					neighborDistance += calculateDistance(networkModel.getNeuron(cnt),networkModel.getNeuron(neighbourNeuron));
				}
				manhattanDist[cnt] = neighborDistance;
			}
			 
		}
		
		TreeMap<Double,Integer> uniqueElementMap = new TreeMap<Double,Integer>();
		
		for(double cnt:manhattanDist)
		{
			if(uniqueElementMap.get(cnt)== null)
			{
				uniqueElementMap.put(cnt,1);
			}
		}
		
		ArrayList<Double> uniqueElements = new ArrayList<Double>(uniqueElementMap.keySet());
		int percentileIndex = (int)Math.floor(0.85 * uniqueElements.size());
		thresholdValue = uniqueElements.get(percentileIndex);
		return thresholdValue; 
	}
	
	/*
	 * Implementation of Manhattan distance calculation
	 */
	public static double calculateDistance(NeuronModel mainNeuron, NeuronModel neighbourNeuron)
	{
		double sum_of_weights = 0;
		for(int cnt = 0; cnt < mainNeuron.getWeight().length ; cnt++)
		{
			sum_of_weights += Math.abs(mainNeuron.getWeight()[cnt] - neighbourNeuron.getWeight()[cnt]);
		}
		return sum_of_weights;
	}
}
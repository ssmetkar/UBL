package com.ncsu.ubl.master;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ncsu.jknnl.metrics.MetricModel;
import com.ncsu.jknnl.network.DefaultNetwork;
import com.ncsu.jknnl.network.NetworkModel;
import com.ncsu.jknnl.topology.MatrixTopology;
import com.ncsu.jknnl.topology.TopologyModel;
import com.ncsu.ubl.configuration.VMConfiguration;
import com.ncsu.ubl.models.LearnThread;
import com.ncsu.ubl.utility.TopologyModelFactory;

/**
 * This is the main controller class which initiates the learning model and resultant cause inference
 * @author Sarang Metkar
 *
 */

public class Controller {
	
	private static VMConfiguration config; 
	private Map<String,NetworkModel> VM_model_map;
	
	public void initialize()
	{
		config = VMConfiguration.getInstance();
		for(int i = 1; i <= config.getNumberOfVM(); i++)
		{
			TopologyModel topologyModel = TopologyModelFactory
					.getTopologyModel(config.getTopologyModelType(),
							config.getRows(), config.getCols(),
							config.getRadius());

			double maxWeights[] = new double[config.getNumberOfWeights()];
			for (int j = 0; j < config.getNumberOfWeights(); j++) {
				maxWeights[j] = config.getMaxWeight();
			}
			NetworkModel networkModel = new DefaultNetwork(
					config.getNumberOfWeights(), maxWeights, topologyModel);
			VM_model_map.put(config.getIpAddressList().get(i), networkModel);
		}
	}
	
	public void learn()
	{
		List<LearnThread> learningModelThread = new ArrayList<LearnThread>();
		
		for(int cnt = 1; cnt <= config.getNumberOfVM(); cnt++)
		{
			LearnThread learnThread = new LearnThread(VM_model_map.get(cnt),config.getIpAddressList().get(cnt));
			learnThread.start();
		}
	}
	
	public void launch()
	{
		while(true)
		{
			
		}
	}
	
	public static VMConfiguration getConfig()
	{
		return config;
	}

	
}

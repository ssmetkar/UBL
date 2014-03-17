package com.ncsu.ubl.models;

import com.ncsu.jknnl.kohonen.LearningData;
import com.ncsu.jknnl.kohonen.WTMLearningFunction;
import com.ncsu.jknnl.learningFactorFunctional.ConstantFunctionalFactor;
import com.ncsu.jknnl.learningFactorFunctional.LearningFactorFunctionalModel;
import com.ncsu.jknnl.metrics.MetricModel;
import com.ncsu.jknnl.network.NetworkModel;
import com.ncsu.jknnl.topology.GaussNeighbourhoodFunction;
import com.ncsu.jknnl.topology.NeighbourhoodFunctionModel;
import com.ncsu.ubl.master.Controller;
import com.ncsu.ubl.utility.ComputationUtility;
import com.ncsu.ubl.utility.MetricModelFactory;

public class LearnThread extends Thread {
	
	private String ip_address;
	private NetworkModel networkModel;
	//private LinkedHashMap rankList;
	
	public LearnThread(NetworkModel networkModel,String ip_address) {
		this.networkModel = networkModel;
		this.ip_address = ip_address;
	}

	@Override
	public void run() {
		int thresholdValue = 0;
		String fileLocation = Controller.getConfig().getLearnFileLocation();
		String filename = Controller.getConfig().getLearnFileName();
		String finalFileName =  fileLocation + ip_address + filename;
		
		LearningData learningData = new LearningData(finalFileName);
		MetricModel metricModel = MetricModelFactory.getMetricModel(Controller
				.getConfig().getTrainingMetricType());
		LearningFactorFunctionalModel learnFunctionModel = new ConstantFunctionalFactor(
				Controller.getConfig().getLearningFactor());
		NeighbourhoodFunctionModel neighbourModel = new GaussNeighbourhoodFunction(
				Controller.getConfig().getRadius());
		
		WTMLearningFunction learningFunction = new WTMLearningFunction(
				networkModel, Controller.getConfig().getMaxIteration(),
				metricModel, learningData,learnFunctionModel,
				neighbourModel);
		learningFunction.learn();
		thresholdValue = ComputationUtility.calculateThreshold(networkModel);
	}
	
}

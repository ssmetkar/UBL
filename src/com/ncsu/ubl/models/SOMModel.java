package com.ncsu.ubl.models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ncsu.jknnl.kohonen.LearningData;
import com.ncsu.jknnl.kohonen.WTMLearningFunction;
import com.ncsu.jknnl.learningFactorFunctional.ConstantFunctionalFactor;
import com.ncsu.jknnl.learningFactorFunctional.LearningFactorFunctionalModel;
import com.ncsu.jknnl.metrics.MetricModel;
import com.ncsu.jknnl.network.DefaultNetwork;
import com.ncsu.jknnl.network.NetworkModel;
import com.ncsu.jknnl.topology.GaussNeighbourhoodFunction;
import com.ncsu.jknnl.topology.NeighbourhoodFunctionModel;
import com.ncsu.ubl.master.Controller;
import com.ncsu.ubl.utility.ComputationUtility;
import com.ncsu.ubl.utility.MetricModelFactory;

public class SOMModel {
	
	private static Logger logger = Logger.getLogger(Controller.class);
	private DefaultNetwork networkModel;
	private static double thresholdValue;
	
	public SOMModel(NetworkModel networkModel) {
		this.networkModel = (DefaultNetwork)networkModel;
	}

	public void learnModel() {
		DataFold[] kFoldedData = initForCrossValidation(Controller.getConfig().getKFoldValue());		
		thresholdValue = getBestNetworkModel(kFoldedData);
	}
	
	public void learn()
	{
		MetricModel metricModel = MetricModelFactory.getMetricModel(Controller
				.getConfig().getTrainingMetricType());
		LearningFactorFunctionalModel learnFunctionModel = new ConstantFunctionalFactor(
				Controller.getConfig().getLearningFactor());
		NeighbourhoodFunctionModel neighbourModel = new GaussNeighbourhoodFunction(
				Controller.getConfig().getRadius(),1);
		
		LearningData data = new LearningData("D:\\Study Stuff\\AdvDS\\Project\\ADS_Project_Snippets\\src\\SampleTrain.txt");
		
		WTMLearningFunction learningFunction = new WTMLearningFunction(
				networkModel, Controller.getConfig().getMaxIteration(),
				metricModel, data ,learnFunctionModel,
				neighbourModel);
		learningFunction.setShowComments(true);
		learningFunction.learn();
	}
	
	public RankList predictState(double metricVector[]){
		
		RankList currentState = null;
		//Get data from somewhere and use it for predicting the state
		
		MetricModel metricModel = MetricModelFactory.getMetricModel(Controller
				.getConfig().getTrainingMetricType());
		LearningFactorFunctionalModel learnFunctionModel = new ConstantFunctionalFactor(
				Controller.getConfig().getLearningFactor());
		NeighbourhoodFunctionModel neighbourModel = new GaussNeighbourhoodFunction(
				Controller.getConfig().getRadius(),10);
		
		WTMLearningFunction learningFunction = new WTMLearningFunction(
				networkModel, Controller.getConfig().getMaxIteration(),
				metricModel, null,learnFunctionModel,
				neighbourModel);
		currentState = learningFunction.predict(metricVector,thresholdValue);
		return currentState;
	}
	
	private DataFold[] initForCrossValidation(int kFoldValue)
	{
		logger.info(kFoldValue + "-fold cross-validation");
		DataFold[] data = new DataFold[kFoldValue];
		String finalFileName = Controller.getConfig().getLearnFileName();
		
		try {
			FileReader fr = new FileReader(finalFileName);
			LineNumberReader lnr = new LineNumberReader(fr);
			
			int dataCount = 0,listSize = 0,lastRunSize = 0, foldCount = 0;
			ArrayList<String> tempList = new ArrayList<String>();
			
			//Count number of lines
			String lineRead = "";
			while( (lineRead = lnr.readLine()) != null) {}
			dataCount = lnr.getLineNumber();
			fr.close();
			
			//Get the actual content 
			fr = new FileReader(finalFileName);
			BufferedReader reader = new BufferedReader(fr);
			
			listSize = dataCount/kFoldValue;
			
			if( dataCount % kFoldValue == 0)
				lastRunSize = dataCount / kFoldValue;
			else
				lastRunSize = listSize + dataCount % kFoldValue;
			
			dataCount = 0;
			foldCount = 0;

			while( (lineRead = reader.readLine()) != null )
			{
				if(dataCount < listSize)
				{
					tempList.add(lineRead);
					dataCount++;
				}
				else
				{
					data[foldCount++] = new DataFold(tempList);
					dataCount = 0;
					if(foldCount == kFoldValue - 1)
						listSize = lastRunSize; 
					tempList = new ArrayList<String>();
					tempList.add(lineRead);
					dataCount++;
				}
			}
			data[foldCount] = new DataFold(tempList);
						
		} catch (FileNotFoundException e) {
			logger.error("Unable to locate the input file");
		} catch (IOException e) {
			logger.error("Error while calculating line number");
		}
		return data;
	}

	/**
	 * 
	 * @param data :- length equal to value of K
	 * @return
	 */
	private double getBestNetworkModel(DataFold[] data)
	{
		logger.info("Getting best network model");
		double thresholdValue[] = new double[data.length];
		double maxAccuracy = -1,currentAccuracy = 0;
		int maxAccuracyIndex = 0;
		
		DefaultNetwork[] networkModelList = new DefaultNetwork[data.length];
		
		MetricModel metricModel = MetricModelFactory.getMetricModel(Controller
				.getConfig().getTrainingMetricType());
		LearningFactorFunctionalModel learnFunctionModel = new ConstantFunctionalFactor(
				Controller.getConfig().getLearningFactor());
		NeighbourhoodFunctionModel neighbourModel = new GaussNeighbourhoodFunction(
				Controller.getConfig().getRadius(),Controller.getConfig().getGaussianHeight());
		
		for(int itr = 0 ; itr < data.length ; itr++ )
		{
			networkModelList[itr] = DefaultNetwork.newInstance(this.networkModel); 
			
			LearningData learningData = new LearningData(getTrainData(data, itr));
			
			WTMLearningFunction learningFunction = new WTMLearningFunction(
					networkModelList[itr], Controller.getConfig().getMaxIteration(),
					metricModel, learningData,learnFunctionModel,
					neighbourModel);
			
			learningFunction.learn();
			
			thresholdValue[itr] = ComputationUtility.calculateThreshold(networkModelList[itr]);
			ArrayList <double[]> testData = getTestData(data[itr]); 
			currentAccuracy = learningFunction.findModelAccuracy(testData,thresholdValue[itr]);
			
			if(currentAccuracy > maxAccuracy)
			{
				maxAccuracyIndex = itr;
				maxAccuracy = currentAccuracy;
			}
		}
		
		this.networkModel = networkModelList[maxAccuracyIndex];
		logger.info("Max accuracy model threshold value : " + thresholdValue[maxAccuracyIndex]);
		return thresholdValue[maxAccuracyIndex];
	}
	
	/*
	 * Original data is in the form of array of string, we need to  
	 * data into double format
	 */
	private ArrayList<double[]> getTestData(DataFold testData)
	{
		ArrayList<double[]> test = new ArrayList<double[]>();
		ArrayList<String> tempStrData = null;
		String tempTable[] = null;
		double[] tempList = null;
		tempStrData = testData.getData();
		if(tempStrData != null){
			for(String dataStr : tempStrData)
			{
				tempTable = dataStr.split(Controller.getConfig().getDelimiter());
				int tableLength = tempTable.length;
				tempList = new double[tableLength];
				for(int i=0; i < tableLength ; i++)
				{
					tempList[i] = Double.valueOf(tempTable[i]);
				}
				test.add(tempList);
			}
		}
		return test;
	}
	
	private DataFold[] getTrainData(DataFold[] data, int test_index)
	{
		DataFold[] trainingData = new DataFold[data.length - 1];
		int trainItr = 0;
		for(int itr = 0 ; itr < data.length ; itr++ )
		{
			if(itr != test_index)
			{
				trainingData[trainItr++] = data[itr];
			}
		}
		return trainingData;
	}
}

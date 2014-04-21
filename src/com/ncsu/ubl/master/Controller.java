package com.ncsu.ubl.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ncsu.jknnl.metrics.MetricModel;
import com.ncsu.jknnl.network.DefaultNetwork;
import com.ncsu.jknnl.network.NetworkModel;
import com.ncsu.jknnl.network.NeuronModel;
import com.ncsu.jknnl.topology.MatrixTopology;
import com.ncsu.jknnl.topology.TopologyModel;
import com.ncsu.press.MarkovChainModel;
import com.ncsu.press.SignaturePredictionModel;
import com.ncsu.ubl.commons.Constants;
import com.ncsu.ubl.configuration.VMConfiguration;
import com.ncsu.ubl.models.RankList;
import com.ncsu.ubl.models.SOMModel;
import com.ncsu.ubl.utility.TopologyModelFactory;
import com.ncsu.ubl.utility.ReadFile;

/**
 * This is the main controller class which initiates the learning model 
 * @author Sarang Metkar
 *
 */

public class Controller {
	
	private int alarmCount;
	private static VMConfiguration config; 
	private static double[][] MinMaxMetricVal;
	//private Map<String,NetworkModel> VM_model_map;
	private SOMModel somModel;
	
	public double[] readNormalizedLastLine()
	{
		double[] newRow = new double[7];
		double[] normalizedNewRow = new double[7];
		
		try{
			File file = new File("C:\\Users\\amitskatti\\Documents\\GitHub\\UBL\\src\\scripts\\ubuntupara2_mem.log");
			String metric = ReadFile.readLast_N_Lines(file, 1);
			String[] splited = metric.split("\\s+");
			newRow[Constants.METRIC.MEM.getValue()] = Double.parseDouble(splited[5]);
			
			file = new File("C:\\Users\\amitskatti\\Documents\\GitHub\\UBL\\src\\scripts\\ubuntuPara.log");
			metric = ReadFile.readLast_N_Lines(file, 1);
			splited = metric.split("\\s+");
			newRow[Constants.METRIC.CPU.getValue()] = Double.parseDouble(splited[3]);
			newRow[Constants.METRIC.NETTX.getValue()] = Double.parseDouble(splited[9]);
			newRow[Constants.METRIC.NETRX.getValue()] = Double.parseDouble(splited[11]);
			newRow[Constants.METRIC.VBD_OO.getValue()] = Double.parseDouble(splited[13]);
			newRow[Constants.METRIC.VBD_RD.getValue()] = Double.parseDouble(splited[15]);
			newRow[Constants.METRIC.VBD_WR.getValue()] = Double.parseDouble(splited[17]);
			
			/* SCALING LOGIC
			 * X_std = (X - X.min(axis=0)) / (X.max(axis=0) - X.min(axis=0))
			 * X_scaled = X_std * (max - min) + min 
			 */
//			System.out.println("The normalized data is:");
			for(int i=0;i<7;i++)
			{
				double std;
				double denominator = MinMaxMetricVal[1][i] - MinMaxMetricVal[0][i];
				if(denominator > 0)
					std = (newRow[i] - MinMaxMetricVal[0][i]) / denominator;
				else
					std=0;
				
				normalizedNewRow[i] = std * (100 - 0) + 0;
//				System.out.print(normalizedNewRow[i] + "  ");
			}
		} catch(NullPointerException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return normalizedNewRow;
	}
	
	public void initialize()
	{
		config = VMConfiguration.getInstance();
		alarmCount=0;
		
		//Call the TrainDataPreprocess python file to get the normalized Training data
		MinMaxMetricVal = new double[2][7];
		ProcessBuilder p = new ProcessBuilder("python","C:\\Users\\amitskatti\\Documents\\GitHub\\UBL\\src\\scripts\\TrainDataPreprocess.py");
		Process proc;
		try {
			proc = p.start();
			
			BufferedReader output = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			
			String ligne = "";
			int counter = 0;
			
			while ((ligne = output.readLine()) != null) {
				if(counter>13)
					break;
			    Double d = Double.parseDouble(ligne);
			    MinMaxMetricVal[(int)(counter/7)][(int)(counter%7)] = d;
			    counter++;
			}		
//			TESTING THE PYTHON'S OUTPUT
//			while ((ligne = error.readLine()) != null) {
//			 System.out.println(ligne);
//			}
//			System.out.println(output);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*for(int i = 1; i <= config.getNumberOfVM(); i++)
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
		}*/
	}
	
	public void execute()
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
		
		somModel = new SOMModel(networkModel);
		somModel.learnModel();
		//somModel.learn();
	
		/*
		while(true)
		{
			try
			{
				currentState = somModel.predictState();
				if(currentState == 1)
				{
					//get rank list and inform master node about the alarm and submit the rank list
				}
			}
			catch(Exception e)
			{
				break;
			}
		}*/
	}
	
	public void launch()
	{
		FileReader input;
		String tempTable[] = null;
		double[] tempList = null;
		BufferedReader br = null;
		try {
			input = new FileReader(config.getPredictDataFile());
			br = new BufferedReader(input);
			String line = "";
			while( (line = br.readLine()) != null )
			{
				tempTable = line.split(Controller.getConfig().getDelimiter());
				int tableLength = tempTable.length;
				tempList = new double[tableLength];
				for(int i=0; i < tableLength ; i++)
				{
					tempList[i] = Double.valueOf(tempTable[i]);
				}
				RankList rankList = somModel.predictState(tempList);

				if(rankList.getState() == 0)
				{
					alarmCount = 0;
				}
				else if(rankList.getState() == 1)
				{
					if(++alarmCount >=2 )
					{
						alarmCount++;
						Scanner scan = null;
						double metricInput [] = new double [100];
						int scaleTo = -1;

						switch (rankList.getRankList().get(0))
						{
						case 0: //It is CPU, get CPU data into metricInput
							break;
						case 1: //It is Memory, get Memory data into metricInput
							break;
						case 2: //It is Something else, Print appropriate data
							System.out.println("ALARM: THIS METRIC is going to cause ANOMALY.");
							break;
						}

						//point to a file with the last 100 metric data of the particular metric
						File metricSource = new File("D:\\NCSU\\Android_Workspace\\FFT_sample\\src\\sineWaveDummyData.txt");
						try {
							int i = 0;
							scan = new Scanner(metricSource);
							while(scan.hasNextDouble())
							{
								metricInput[i]=scan.nextDouble();
								i++;
							}
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} finally{
							scan.close();
						}

						scaleTo = SignaturePredictionModel.SignatureDrivenPrediction(metricInput);
						if (scaleTo == -1)
						{
							/*MarkovChainModel Testing Code*/
							MarkovChainModel MyModel = new MarkovChainModel(40);
							MyModel.trainMarkovChainModel(metricInput);
							//						MyModel.printTransition_matrix();
							scaleTo = MyModel.predictState((int)metricInput[metricInput.length-1], 10);
						}

						//code for actually scaling the metric
					}
				}
			}
			input.close();
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("Wrong input file");
		} catch (IOException e) {
			System.err.println("IO exception");
		}
	}
	
	public static VMConfiguration getConfig()
	{
		return config;
	}
	
	public static void main(String[] args) {
		Controller controller = new Controller();
		controller.initialize();
		//Learn Model
		
		long start = System.currentTimeMillis();
		controller.execute();
		long end = System.currentTimeMillis();
		System.out.println("Time difference :" + (end - start));
		System.out.println("Training Complete");
			
		while(true)
		{
			start = System.currentTimeMillis();
		
			controller.launch();
			
			end = System.currentTimeMillis();
			
			try {
				Thread.sleep(1000 - (end-start));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Exception thrown by Sleep at while loop of Launch");
				e.printStackTrace();
			}
		}
	}

	
}

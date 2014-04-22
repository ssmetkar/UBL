package com.ncsu.ubl.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.ncsu.jknnl.network.DefaultNetwork;
import com.ncsu.jknnl.network.NetworkModel;
import com.ncsu.jknnl.topology.TopologyModel;
import com.ncsu.press.MarkovChainModel;
import com.ncsu.press.SignaturePredictionModel;
import com.ncsu.ubl.commons.Constants;
import com.ncsu.ubl.configuration.VMConfiguration;
import com.ncsu.ubl.models.RankList;
import com.ncsu.ubl.models.SOMModel;
import com.ncsu.ubl.utility.ReadFile;
import com.ncsu.ubl.utility.TopologyModelFactory;

/**
 * This is the main controller class which initiates the learning model 
 * @author Sarang Metkar
 *
 */

public class Controller {
	
	private static Logger logger = Logger.getLogger(Controller.class);
	private int alarmCount;
	private static VMConfiguration config; 
	private static double[][] MinMaxMetricVal;
	//private Map<String,NetworkModel> VM_model_map;
	private SOMModel somModel;
	
	public double[] readNormalizedLastLine()
	{
		double[] newRow = new double[2];
		double[] normalizedNewRow = new double[2];
		
		try{
			File file = new File(Controller.getConfig().getTrainMemLogFile());
			String metric = ReadFile.readLast_N_Lines(file, 1);
			String[] lastline = metric.split("\\n");
			metric = lastline[lastline.length-1];
			String[] splited = metric.split("\\s+");
			newRow[Constants.METRIC.MEM.getValue()] = Double.parseDouble(splited[4]);
			
			file = new File(Controller.getConfig().getTrainMetricLogFile());
			metric = ReadFile.readLast_N_Lines(file, 1);
			lastline = metric.split("\\n");
			metric = lastline[lastline.length-1];
			splited = metric.split("\\s+");
			newRow[Constants.METRIC.CPU.getValue()] = Double.parseDouble(splited[4]);
			/*newRow[Constants.METRIC.NETTX.getValue()] = Double.parseDouble(splited[8]);
			newRow[Constants.METRIC.NETRX.getValue()] = Double.parseDouble(splited[10]);
			newRow[Constants.METRIC.VBD_OO.getValue()] = Double.parseDouble(splited[12]);
			newRow[Constants.METRIC.VBD_RD.getValue()] = Double.parseDouble(splited[14]);
			newRow[Constants.METRIC.VBD_WR.getValue()] = Double.parseDouble(splited[16]);*/
			/*logger.info("Metric read : " + newRow[0] + " " + newRow[1] + " " + newRow[2] + " " + newRow[3] + " " + newRow[4] + " " + newRow[5] + " "
										+  newRow[6] );*/
			logger.info("Metric read : " + newRow[0] + " " + newRow[1]);
			/* SCALING LOGIC
			 * X_std = (X - X.min(axis=0)) / (X.max(axis=0) - X.min(axis=0))
			 * X_scaled = X_std * (max - min) + min 
			 */
			for(int i=0;i<2;i++)
			{
				double std;
				double denominator = MinMaxMetricVal[1][i] - MinMaxMetricVal[0][i];
				if(denominator > 0)
					std = (newRow[i] - MinMaxMetricVal[0][i]) / denominator;
				else
					std=0;
				
				normalizedNewRow[i] = std * (100 - 0) + 0;
			}
			logger.info("Normalized : " + normalizedNewRow[0] + " " + normalizedNewRow[1] );
		} catch(NullPointerException e){
			logger.info(e.getMessage());
			e.printStackTrace();
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		return normalizedNewRow;
	}
	
	public void initialize()
	{
		config = VMConfiguration.getInstance();
		alarmCount=0;

		logger.info("Executing python script");
		
		if(Controller.getConfig().getdoTest() != 1)
		{
			//Call the TrainDataPreprocess python file to get the normalized Training data
			MinMaxMetricVal = new double[2][2];
			ProcessBuilder p = new ProcessBuilder("python","./resources/TrainDataPreprocess.py");
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
				    MinMaxMetricVal[(int)(counter/2)][(int)(counter%2)] = d;
				    counter++;
				}		
//				TESTING THE PYTHON'S OUTPUT
//				while ((ligne = error.readLine()) != null) {
//				 System.out.println(ligne);
//				}
//				System.out.println(output);		
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public void execute()
	{
		logger.info("Learning SOM Model");
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
		logger.info("SOM training completed");
	}
	
	public void launch() {
		try {
			logger.info("Predicting state");
			RankList rankList = somModel.predictState(readNormalizedLastLine());
			logger.info("State predicted : " + rankList.getState());
			
			if (rankList.getState() == 0) {
				alarmCount = 0;
			} else if (rankList.getState() == 1) {
				if (++alarmCount >= 2) {
					logger.info("Alarm raised , count :" + alarmCount);
					alarmCount++;
					double metricInput[] = new double[1000];
					int scaleTo = -1;
					File metricSource = null;
					String metric;
					String[] line;
					int i = 0;
					//New Change - Amit
					int anomalyMetric = rankList.getRankList().get(0);
					logger.info("Anamoly metric : " + anomalyMetric);
					// 0 = Memory, 1 = CPU
					switch (anomalyMetric) {
						case 0: // It is Memory, get Memory data into metricInput
								metricSource = new File(Controller.getConfig().getMemLogFile());
								metric = ReadFile.readLast_N_Lines(metricSource, 1000);
								line = metric.split("\\n");
								i = 0;
								for(String m : line){
									if(m.length()<40)
										continue;
									String[] parts = m.split("\\s+");		
									metricInput[i]=Double.parseDouble(parts[4]);
//									System.out.println(metricInput[i]);
									i++;
									if(i>999)
										break;
								}
								break;
						case 1: // It is CPU, get CPU data into metricInput
								metricSource = new File(Controller.getConfig().getMetricLogFile());
								metric = ReadFile.readLast_N_Lines(metricSource, 1000);
								line = metric.split("\\n");
								i = 0;
								for(String m : line){
									if(m.length()<40)
										continue;
									String[] parts = m.split("\\s+");		
									metricInput[i]=Double.parseDouble(parts[4]);
//									System.out.println(metricInput[i]);
									i++;
									if(i>999)
										break;
								}
								break;
						//New Change - Amit
//						case 2: // NETTX causing issue
//								logger.info(System.currentTimeMillis() + " :Abnormal state cause  : NETTX");
//								break;
//						case 3: // NETRX causing issue
//								logger.info(System.currentTimeMillis() + " :Abnormal state cause  : NETRX");
//								break;
//						case 4: // VBD_OO causing issue
//								logger.info(System.currentTimeMillis() + " :Abnormal state cause  : VBD_OO");
//								break;
//						case 5: // VBD_RD causing issue
//								logger.info(System.currentTimeMillis() + " :Abnormal state cause : VBD_RD");
//								break;
//						case 6: // VBD_WR causing issue
//								logger.info(System.currentTimeMillis() + " :Abnormal state cause  : VBD_WR");
//								break;
					}
					
					// If it is not CPU or MEMORY, Exit!
					if(metricSource == null)
						return;

					scaleTo = SignaturePredictionModel.SignatureDrivenPrediction(metricInput);
					logger.info("Calling PRESS");
					logger.info("PRESS scaleTo value : "+ scaleTo);
					
					if (scaleTo < 0) {
						logger.info("Calling Markov");
						/* MarkovChainModel Testing Code */
						MarkovChainModel MyModel = new MarkovChainModel(40);
						MyModel.trainMarkovChainModel(Arrays.copyOfRange(metricInput, metricInput.length-41, metricInput.length));
						// MyModel.printTransition_matrix();
						scaleTo = MyModel.predictState(
								(int) metricInput[metricInput.length - 1],
								Controller.getConfig().getPredictAheadStep());
						logger.info("Markov scaleTo value : "+scaleTo);
					}

					//New Change - Amit
					if (config.getdoTest() == 1)
					{
						if (anomalyMetric == 0)
						{
							scaleMemory(scaleTo);
						}
						else if(anomalyMetric == 1)
						{
							scaleCPU(scaleTo);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	//New Change - Amit
		public void scaleMemory(int scaleToVal)
		{
			String vmname = config.getvm_name();
			ProcessBuilder p = new ProcessBuilder("/bin/bash","-c","xm mem-set "+vmname+ " "+scaleToVal);
			Process proc;
			try {
				proc = p.start();
				
				BufferedReader output = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				
				String line = "";
				while ((line = error.readLine()) != null) {
					String parts[] = line.split(":");
					if ("Error".equals(parts[0]))
					{
						logger.error("Command didn't execute successfully: xm mem-set "+vmname+" "+scaleToVal);
						break;
					}
				}			
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		
		
		//New Change - Amit	
		public void scaleCPU(int scaleToVal)
		{
			String vmname = config.getvm_name();
			int currentCPUCap, scaleToCPUCap;
			ProcessBuilder p = new ProcessBuilder("/bin/bash","-c","xm sched-credit");
			Process proc;
			try {
				proc = p.start();
				
				BufferedReader output = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				
				String line = "";
				
				while ((line = output.readLine()) != null) {
					String parts[] = line.split("\\s+");
					if (vmname.equals(parts[0]))
					{
						currentCPUCap=Integer.parseInt(parts[2]);
						if(scaleToVal > 0 && scaleToVal <= 10)
							scaleToCPUCap = currentCPUCap - 64;
						else if(scaleToVal > 85)
							scaleToCPUCap = currentCPUCap + 256;
						else
							scaleToCPUCap = currentCPUCap + 64;
						
						p = new ProcessBuilder("/bin/bash","-c","xm sched-credit -d "+vmname+" -w "+scaleToCPUCap);
						proc = p.start();
						BufferedReader innerOutput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
						BufferedReader innerError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
						while ((line = innerError.readLine()) != null) {
							parts = line.split(":");
							if ("Error".equals(parts[0]))
							{
								logger.error("Command didn't execute successfully: xm sched-credit -d "+vmname+" -w "+scaleToCPUCap);
								break;
							}
						}	
						break;
					}
				}			
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	
	
	public void test()
	{
		FileReader input;
		int cnt = 0,temp = 0;
		String tempTable[] = null;
		double[] tempList = null;
		BufferedReader br = null;
		Map<Integer,Integer> actual = new HashMap<Integer,Integer>();
		Map<Integer,Integer> out = new HashMap<Integer, Integer>();
		double true_positive = 0, false_positive = 0, accuracy = 0;
		try
		{
			input = new FileReader(config.getPredictDataFile());
			br = new BufferedReader(input);
			String line = "";
			cnt = 0;			
			while( (line = br.readLine()) != null)
			{
				tempTable = line.split(Controller.getConfig().getDelimiter());
				int tableLength = tempTable.length;
				tempList = new double[tableLength];
				out.put(cnt, Character.getNumericValue((tempTable[tableLength-1].charAt(0))));
				cnt++;
			}
			input.close();
			br.close();
			
			input = new FileReader(config.getPredictDataFile());
			br = new BufferedReader(input);
			line = "";
			
			cnt = 0;
			while( (line = br.readLine()) != null )
			{
				tempTable = line.split(Controller.getConfig().getDelimiter());
				int tableLength = tempTable.length;
				temp = 0;
				tempList = new double[tableLength-2];
				for(int i=1; i < tableLength-1 ; i++)
				{
					tempList[temp++] = Double.valueOf(tempTable[i]);
				}
				logger.info("Predicting State");
				RankList rankList = somModel.predictState(tempList);
				actual.put(cnt, 0);
				if(rankList.getState() == 1)
				{
					
					for(int start = cnt + 1; (start < out.size()) && (start < cnt + Controller.getConfig().getLookAheadSize()) ; start++)
					{
						if(out.get(start) == 2)
						{
							logger.info("Abnormal state predicted");
							logger.info("Rank List :\t");
							actual.put(cnt, 2);
							Iterator<Integer> it = rankList.getRankList().iterator();
							while(it.hasNext())
							{
								logger.info(it.next() + "\t");
							}
							break;
						}
					}
				}
				cnt++; 
			}
			input.close();
			br.close();
			
			double tp = 0, tn = 0, fp = 0, fn = 0;
			
			for(int i = 0; i < actual.size() ; i++)
			{
				if(actual.get(i)== 0 && out.get(i) == 0)
				{
					tn++;
				}
				else if(out.get(i) == 0 && actual.get(i) == 2)
				{
					fp++;
				}
				else if(out.get(i) == 2 && actual.get(i) == 0 )
				{
					fn++;
				}
				else if(out.get(i) == 2 && actual.get(i) == 2)
				{
					tp++;
				}
			}
			
			true_positive = tp / (tp + fn);
			false_positive = fp / (fp + tn);
			accuracy = (tp + tn) / (tp + tn + fp + fn);
			logger.info("True positive : " + true_positive);
			logger.info("Accuracy : " + accuracy);
			logger.info("False positive :" + false_positive);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
	}
	
	public static VMConfiguration getConfig()
	{
		return config;
	}
	
	public static void main(String[] args) {
		Controller controller = new Controller();
		String s = new String();
		controller.initialize();
		logger.info("Configuration file initialized");
		
		long start = System.currentTimeMillis();
		controller.execute();
		long end = System.currentTimeMillis();
		
		logger.info("Training time  : " + (end - start));

		System.out.println("\nPress enter to start prediction");

		Scanner in = new Scanner(System.in);
		s = in.nextLine();
		
		//Finding TP, FP, Accuracy
		if(Controller.getConfig().getdoTest() == 1)
		{
			logger.info("Calling test method");
			controller.test();
		}
		else
		{
			while(true)
			{
				start = System.currentTimeMillis();
				controller.launch();
				end = System.currentTimeMillis();
				
				try {
					Thread.sleep(1000 - (end-start));
				} catch (InterruptedException e) {
					logger.error("Exception thrown by Sleep at while loop of Launch");
				}
			}
		}
	}
}

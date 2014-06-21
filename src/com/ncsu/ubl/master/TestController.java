package com.ncsu.ubl.master;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

public class TestController {
	
	private static Logger logger = Logger.getLogger(TestController.class);
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
		logger.info("Configuration initialized");
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
		
		int alarmcnt = 0;
		boolean faultInduced = false;
		long timer = 0;
		long curr_timestamp = 0;
		int faultInterval = TestController.getConfig().getFaultInt();
		FileWriter annotateFile;
		FileWriter predictFile;
		BufferedWriter br1;
		BufferedWriter br2;
		ProcessBuilder p = new ProcessBuilder();
		Process proc;
		int count = 0;
		long start = 0,end =0;
		try
		{
			annotateFile = new FileWriter(TestController.config.getAnnotateFile());
			predictFile = new FileWriter(TestController.config.getPredictFile());
			br1 = new BufferedWriter(annotateFile);
			br2 = new BufferedWriter(predictFile);
		}
		catch(FileNotFoundException e)
		{
			logger.error(e.getMessage());
			return;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return;
		}
		
		try {
			while(true)
			{
				RankList rankList = somModel.predictState(readNormalizedLastLine());
				timer++;
				curr_timestamp = System.currentTimeMillis();
				
				if(timer == 100)
				{
					p = new ProcessBuilder("/bin/bash","-c","stress -v -m 1 --vm-bytes 50M -t 3");
					proc = p.start();
					br1.write(curr_timestamp +"\t"+0);
					br1.newLine();
				}
				else if(timer > 100 && timer <= 103)
				{
					br1.write(curr_timestamp +"\t"+1);
					br1.newLine();
				}
				else if(timer == 104)
				{
					timer = 0;
					br1.write(curr_timestamp +"\t"+0);
					br1.newLine();
				}
				else
				{
					logger.info("Writing to file");
					
					br1.write(curr_timestamp +"\t"+0);
					br1.newLine();
					logger.info("Written to file");
				}
				
				
				if(rankList.getState() == 0) 
				{	
					br2.write(curr_timestamp + "\t"+ 0);
					br2.newLine();
					alarmcnt = 0;
				}
				else
				{
					alarmcnt++;
					if(alarmcnt > 3)
					{
						br2.write(curr_timestamp + "\t"+ 1);
						br2.newLine();
					}
					else
					{
						br2.write(curr_timestamp + "\t"+ 0);
						br2.newLine();
					}
				}
				
				end = System.currentTimeMillis();
				
				if((1000 - (end- curr_timestamp)) > 0)
				{
					try {
						Thread.sleep(1000 - (end-curr_timestamp));
					} catch (InterruptedException e) {
						logger.error("Exception thrown by Sleep at while loop of Launch");
					}	
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		finally
		{
			try {
				br1.close();
				br2.close();
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
		}
	}
	
	//New Change - Amit
		public void scaleMemory(int scaleToVal)
		{
			logger.info("Elastically Scaling Memory Value : " + scaleToVal);
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
						
						logger.info("Elastically Scaling CPU value : " + scaleToCPUCap);
						
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
				RankList rankList = somModel.predictState(tempList);
				actual.put(cnt, 0);
				if(rankList.getState() == 1)
				{
					
					for(int start = cnt + 1; (start < out.size()) && (start < cnt + Controller.getConfig().getLookAheadSize()) ; start++)
					{
						if(out.get(start) == 2)
						{
							logger.info("Abnormal state predicted");
							String tempStr = "Rank List :\t";
							actual.put(cnt, 2);
							Iterator<Integer> it = rankList.getRankList().iterator();
							while(it.hasNext())
							{
								tempStr += it.next() + "\t";
							}
							logger.info(tempStr);
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
		Controller cnt = new Controller();
		cnt.initialize();
		TestController controller = new TestController();
		String s = new String();
		controller.initialize();
		logger.info("Configuration file initialized");
		
		long start = System.currentTimeMillis();
		controller.execute();
		long end = System.currentTimeMillis();
		
		logger.info("Training time  : " + (end - start));
		System.out.println("SOM model learned");
		System.out.println("Press enter to start prediction");

		Scanner in = new Scanner(System.in);
		s = in.nextLine();
		
		//Finding TP, FP, Accuracy
		controller.launch();
	}


}

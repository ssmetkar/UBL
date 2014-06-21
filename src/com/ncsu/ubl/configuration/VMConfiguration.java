package com.ncsu.ubl.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



import org.apache.log4j.Logger;

import com.ncsu.ubl.commons.*;
import com.ncsu.ubl.master.Controller;

public class VMConfiguration {
	
	private static Logger logger = Logger.getLogger(VMConfiguration.class);
	private static final String CONF_FILE = "./resources/ubl.properties";
	private static VMConfiguration config;
	
	private String topologyModelType;
	private int weightNumber; 
	private String learnFileName;
	private int rows;
	private int cols;
	private int radius;
	private int numberofWeights;
	private int maxWeight;
	private int maxIteration;
	private String trainMetricType;
	private String predictMetricType;
	private int learningFactor;
	private double neighbourFactor;
	private int kFoldValue;
	private String delimiter;
	private String pythonFileLocation;
	private String normalizedFileName;
	private int normalNeurons;
	private String predictDataFile;
	private int lookAheadSize;
	private int predictAheadStep;
	private int gaussianHeight;
	private String mem_log_file;
	private String metric_log_file;
	private String train_mem_log_file;
	private String train_metric_log_file;
	private int dotest;
	private String vm_name;
	private String annotateFile;
	private String predictFile;
	private int faultInterval;
	
	private static void loadConfig()
	{
	
		VMConfiguration.config = new VMConfiguration();
		InputStream conf_file;
		try {
			conf_file = new FileInputStream(new File(CONF_FILE));
			Properties props = new Properties();
			props.load(conf_file);
			config.weightNumber = Integer.parseInt(props.getProperty(Constants.WEIGHT_NUMBER));
			config.learnFileName = props.getProperty(Constants.LEARN_FILE_NAME);
			config.topologyModelType = props.getProperty(Constants.TOPOLOGY_MODEL);
			config.rows = Integer.parseInt(props.getProperty(Constants.ROWS));
			config.cols = Integer.parseInt(props.getProperty(Constants.COLS));
			config.radius = Integer.parseInt(props.getProperty(Constants.RADIUS));
			config.numberofWeights = Integer.parseInt(props.getProperty(Constants.NUMBER_OF_WEIGHTS));
			config.maxWeight = Integer.parseInt(props.getProperty(Constants.MAX_WEIGHT));
			config.maxIteration = Integer.parseInt(props.getProperty(Constants.MAX_ITERATION));
			config.trainMetricType = props.getProperty(Constants.TRAIN_METRIC_TYPE);
			config.predictMetricType = props.getProperty(Constants.PREDICT_METRIC_TYPE);
			config.learningFactor = Integer.parseInt(props.getProperty(Constants.LEARN_FACTOR));
			config.neighbourFactor = Double.parseDouble(props.getProperty(Constants.NEIGHBOUR_FACTOR));
			config.kFoldValue = Integer.parseInt(props.getProperty(Constants.K_FOLD_VALUE));
			config.delimiter = props.getProperty(Constants.DELIMITER);
			config.pythonFileLocation = props.getProperty(Constants.PYTHON_FILE);
			config.normalizedFileName = props.getProperty(Constants.NORMALIZED_FILE);
			config.normalNeurons = Integer.parseInt(props.getProperty(Constants.NORMAL_NEURONS));
			config.predictDataFile = props.getProperty(Constants.PREDICT_DATA_FILE);
			config.lookAheadSize = Integer.parseInt(props.getProperty(Constants.LOOK_AHEAD_SIZE));
			config.predictAheadStep = Integer.parseInt(props.getProperty(Constants.PREDICT_AHEAD_STEP));
			config.gaussianHeight = Integer.parseInt(props.getProperty(Constants.GAUSSIAN_HEIGHT));
			config.mem_log_file  = props.getProperty(Constants.MEM_LOG_FILE);
			config.metric_log_file = props.getProperty(Constants.METRIC_LOG_FILE);
			config.train_mem_log_file  = props.getProperty(Constants.TRAIN_MEM_LOG_FILE);
			config.train_metric_log_file = props.getProperty(Constants.TRAIN_METRIC_LOG_FILE);
			config.dotest = Integer.parseInt(props.getProperty(Constants.DO_TEST));
			config.vm_name = props.getProperty(Constants.VM_NAME);
			config.predictFile = props.getProperty(Constants.PREDICT_FILE);
			config.annotateFile = props.getProperty(Constants.ANN_FILE);
			config.faultInterval = Integer.parseInt(props.getProperty(Constants.FAULT));
			
		} catch (FileNotFoundException e) {
			logger.error("Error while opening configuration file");
		} catch (IOException e) {
			logger.error("Error while loading configuration file");
		}
	}
	
	public static VMConfiguration getInstance()
	{
		if(config == null){
			loadConfig();
		}
		return VMConfiguration.config;
	}
	
	public String getTopologyModelType()
	{
		return config.topologyModelType;
	}
	
	public int getWeightNumber()
	{
		return config.weightNumber;
	}
	
	public String getLearnFileName()
	{
		return learnFileName;
	}
	
	public int getRows()
	{
		return rows;
	}
	
	public int getCols()
	{
		return cols;
	}
	
	public int getRadius()
	{
		return radius;
	}
	
	public int getNumberOfWeights()
	{
		return numberofWeights;
	}
	
	public int getMaxWeight()
	{
		return maxWeight;
	}
	
	public int getMaxIteration()
	{
		return maxIteration;
	}
	
	public String getTrainingMetricType()
	{
		return trainMetricType;
	}
	
	public String getPredictMetricType()
	{
		return predictMetricType;
	}
	
	public int getLearningFactor()
	{
		return learningFactor;
	}
	
	public double getNeighbourFactor()
	{
		return neighbourFactor;
	}
	
	public int getKFoldValue()
	{
		return kFoldValue;
	}
	
	public String getDelimiter()
	{
		return delimiter;
	}
	
	public String getPythonLocation()
	{
		return pythonFileLocation;
	}
	
	public String getNormalizedFileName()
	{
		return normalizedFileName; 
	}
	
	public int getNumberofNormalNeurons()
	{
		return normalNeurons;
	}
	
	public String getPredictDataFile()
	{
		return predictDataFile;
	}
	
	public int getLookAheadSize()
	{
		return lookAheadSize;
	}
	
	public int getPredictAheadStep() {
		return predictAheadStep;
	}
	
	public int getGaussianHeight()
	{
		return gaussianHeight;
	}
	
	public String getMemLogFile()
	{
		return mem_log_file;
	}
	
	public String getMetricLogFile()
	{
		return metric_log_file;
	}
	
	public String getTrainMemLogFile()
	{
		return mem_log_file;
	}
	
	public String getTrainMetricLogFile()
	{
		return metric_log_file;
	}
	
	public int getdoTest()
	{
		return dotest;
	}
	public String getvm_name()
	{
		return vm_name;
	}
	
	public String getAnnotateFile()
	{
		return annotateFile;
	}
	
	public String getPredictFile()
	{
		return predictFile;
	}
	
	public int getFaultInt()
	{
		return faultInterval;
	}
}



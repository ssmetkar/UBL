package com.ncsu.ubl.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.omg.PortableInterceptor.INACTIVE;

import com.ncsu.ubl.commons.*;

public class VMConfiguration {
	
	private static final String CONF_FILE = "ubl.properties";
	private static VMConfiguration config;
	
	//private int number_of_VM;
	//private String ip_address;
	private String topologyModelType;
	private int weightNumber; 
	
	//private long timeNextIteration;
	//private String webServicePath;
	//private String webServicePort;
	//private String outputFileName;
	//private String outputFilePath;
	//private String learnFileLocation;
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
			
	private static void loadConfig()
	{
		VMConfiguration.config = new VMConfiguration();
		InputStream conf_file;
		//String ip_address;
		StringTokenizer strToken;
		try {
			conf_file = new FileInputStream(new File(CONF_FILE));
			Properties props = new Properties();
			props.load(conf_file);
	//		config.number_of_VM = Integer.parseInt(props.getProperty(Constants.NUMBER_OF_VM));
		//	ip_address = props.getProperty(Constants.IP_ADDRESS);
			config.weightNumber = Integer.parseInt(props.getProperty(Constants.WEIGHT_NUMBER));
		//	config.timeNextIteration = Long.parseLong(props.getProperty(Constants.NEXT_ITERATION_TIME));
		//	config.webServicePath = props.getProperty(Constants.WEB_SERVICE_PATH);
		//	config.webServicePort = props.getProperty(Constants.WEB_SERVICE_PORT);
		//	config.outputFileName = props.getProperty(Constants.OUTPUT_FILENAME);
		//	config.outputFilePath = props.getProperty(Constants.OUTPUT_FILEPATH);
		//	config.learnFileLocation = props.getProperty(Constants.LEARN_FILE_LOCATION);
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
			
			/*strToken = new StringTokenizer(ip_address,",");
			if(strToken !=null)
			{
				while(strToken.hasMoreTokens())
				{
					config.ip_address_list.add(strToken.nextToken());
				}
			}*/
		} catch (FileNotFoundException e) {
			System.err.println("Error while opening configuration file.");
		} catch (IOException e) {
			System.err.println("Error while loading configuration file.");
		}
	}
	
	public static VMConfiguration getInstance()
	{
		if(config == null){
			loadConfig();
		}
		return VMConfiguration.config;
	}
	
	/*public int getNumberOfVM()
	{
		return config.number_of_VM; 
	}
	
	public String getIPAddress()
	{
		return config.ip_address;
	}*/
	
	public String getTopologyModelType()
	{
		return config.topologyModelType;
	}
	
	public int getWeightNumber()
	{
		return config.weightNumber;
	}
	
	/*public long getTimeNextIteration()
	{
		return timeNextIteration;
	}
	
	public String getWebServicePath()
	{
		return webServicePath;
	}
	
	public String getWebServicePort()
	{
		return webServicePort;
	}
	
	public String getOutputFileName()
	{
		return outputFileName;
	} 
	
	public String getOutputFilePath()
	{
		return outputFilePath;
	}
	
	public String getLearnFileLocation()
	{
		return learnFileLocation;
	}*/
	
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
}



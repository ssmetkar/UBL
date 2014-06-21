package com.ncsu.ubl.utility;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.ncsu.ubl.configuration.VMConfiguration;
import com.ncsu.ubl.master.Controller;

public class ScalingClient {
	
	private static Logger logger = Logger.getLogger(VMConfiguration.class);
	
	/*public static void sendMetric(String vmName,String metricName,double scaleToValue)
	{
		Socket socket = null;
		try {
			socket = new Socket(Controller.getConfig().getDOM0Address(),
					Controller.getConfig().getPortNumber());
			PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
			out.append(vmName+"::"+metricName+"::"+scaleToValue);
			socket.close();
		} catch (UnknownHostException e) {
			logger.error("Check host name");
		} catch (IOException e) {
			logger.error("Error while sending data");
		}
	}*/
}

package com.ncsu.ubl.utility;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.ncsu.ubl.commons.Constants;
import com.ncsu.ubl.master.Controller;

public class ReadMetricsRESTClient {
	
	public static ArrayList<double[]> pullData(String ip_address)
	{
		ArrayList<double[]> learningData = null;
		String tempContent[];
		double tempdata[];
		String web_service_path = Controller.getConfig().getWebServicePath();
		String web_service_port = Controller.getConfig().getWebServicePort();
		String output_file_path = Controller.getConfig().getOutputFilePath();
		
		String output_file_name = Controller.getConfig().getOutputFileName();
		output_file_name = ip_address + "_" + output_file_name;
		
		
		try {
			
			URL url = new URL("http://" + ip_address + ":" + web_service_port + web_service_path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(Constants.HTTP_GET);
			connection.setRequestProperty("Accept","text/plain");
			
			if(connection.getResponseCode() != 200)
			{
				System.err.println("Web service of "+ip_address+ " not accessible.");
			}
			else
			{
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String content;
				while( (content = responseReader.readLine()) != null ){
					tempContent = content.split(Constants.SPLIT_CHAR);
					tempdata = new double[tempContent.length];
					for(int i = 0 ; i < tempContent.length ; i++)
					{
						tempdata[i] = Double.valueOf(tempContent[i]);
					}
					learningData.add(tempdata);
				}
				
				//Code to write to file
			/*	OutputStream outStream = new FileOutputStream(output_file_name);
				byte[] buffer = new byte[1024];
				int bytesRead;
				
				while((bytesRead = inputstream.read(buffer)) != -1)
				{
						outStream.write(buffer);
				}
				outStream.close();*/
			}
			connection.disconnect();
			
		} catch (MalformedURLException e) {
			System.err.println("\nURL not correct.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error while reading from InputStream or writing to OutputStream");
			e.printStackTrace();
		}
		return learningData;
	}
}

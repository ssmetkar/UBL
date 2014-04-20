package com.ncsu.press;

import java.util.ArrayList;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.distance.dtw.DTWSimilarity;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class SignaturePredictionModel {
	
	public static double mean(double[] m) {
	    double sum = 0;
	    for (int i = 0; i < m.length; i++) {
	        sum += m[i];
	    }
	    return (sum / m.length);
	}
	
	public static int SignatureDrivenPrediction(double[] metricInput)
	{
		int WINDOWSIZE = 100;
		int SAMPLERATE = 1;
		double[] fftoutput = new double [WINDOWSIZE*2];
		Double re,im;
		ArrayList<Double> magnitude = new ArrayList<Double>();
		int i=0;
		
		 
		//Initialize the extra slots in fftoutput to -1
		for (i = 0;i<WINDOWSIZE*2;i++)
		{
			if(i<WINDOWSIZE)
				fftoutput[i] = metricInput[i];
			else
				fftoutput[i] = -1;
		}
		 
//		 try
//			{
//				PythonInterpreter.initialize(System.getProperties(), System.getProperties(), new String[0]);
//				PythonInterpreter interp = new PythonInterpreter();
//				interp.execfile("foo.py");
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}

		//Compute DFT on the metric data
		DoubleFFT_1D fft = new DoubleFFT_1D(WINDOWSIZE);
		fft.realForwardFull(fftoutput);
		
		//Calculate the magnitude by combining the real & imaginary parts
		for (i = 0;i<=WINDOWSIZE - 1;i++)
		{
			re = fftoutput[2*i];
			im = fftoutput[2*i+1];
			magnitude.add((double) Math.sqrt(re*re+im*im));
		}
		
		Double maxVal = magnitude.get(1);
		double dominatingFreqIndex = 1;
		for(i=2;i<magnitude.size()/2 - 1;i++)
		{
			if (magnitude.get(i) >= maxVal)
			{
				maxVal = magnitude.get(i);
				dominatingFreqIndex =  i;
			}
		}
		double dominatingFreq = dominatingFreqIndex / (double)WINDOWSIZE;
		System.out.println("########### MaxVal = "+maxVal+"Fd Index = "+dominatingFreqIndex+"Fd = "+dominatingFreq+" ###############");

		//Set the Pattern Window Size and divide original timeseries into multiple Pattern Windows
//		int patternWindowSize = 20;
		int patternWindowSize = (int) Math.ceil(SAMPLERATE/dominatingFreq);
		System.out.println("PatternWindowSize = "+patternWindowSize);
		double[][] windows = new double[(int) Math.ceil(metricInput.length/patternWindowSize)][patternWindowSize];
		int window_count=0;
		
		for(i = 0; i<metricInput.length;i+=patternWindowSize)
		{
			if((i+patternWindowSize-1) >= (metricInput.length-1))
			{
				int temp = 0;
				for(int j=i;j<metricInput.length;j++)
					windows[window_count][temp++] = metricInput[j];
			}
			else
			{
				int temp = 0;
				for(int j=i;j<i+patternWindowSize;j++)
					windows[window_count][temp++] = metricInput[j];
			}
			window_count++;
		}
		
		
		/* Find if the Windows are similar
		 * For each pair of windows
		 * 1. Compute Pearson Correlation between them
		 * 2. Compute the Ratio of mean value of both windows
		 * 3. Pearson correlation should be > 0.85
		 * 4. Ratio of Means should be between (0.95 - 1.05)
		 * 5. If both the above conditions are satisfied, then these 2 are similar windows
		 */
		boolean isRepeatingPattern = true;
		boolean[][] SimilarWindows = new boolean[windows.length][windows.length];
		
		for(i=0;i<windows.length;i++)
		{
			for(int j=i;j<windows.length;j++)
			{
				PearsonsCorrelation correlationValues = new PearsonsCorrelation();
				double correlationResultValue = 0;
				boolean correlationResult;
				double MeanRatioValue = 0;
				boolean MeanRatio;
				
				correlationResultValue = correlationValues.correlation(windows[i], windows[j]);
				if(correlationResultValue > 0.85)
					correlationResult = true;
				else
					correlationResult = false;
				
				MeanRatioValue = (mean(windows[i])) / (mean(windows[j]));
				if(MeanRatioValue >= 0.95 && MeanRatioValue <= 1.05 )
					MeanRatio = true;
				else
					MeanRatio = false;
				
				SimilarWindows[i][j] = SimilarWindows[j][i] = correlationResult && MeanRatio;
				isRepeatingPattern = isRepeatingPattern && SimilarWindows[i][j];
			}
		}
		
		System.out.println("Signature-Based Result "+isRepeatingPattern);
		//If the windows are not similar, exit saying not repeatable patterns
		if (!isRepeatingPattern)
			return -1;
		else
		{
			//Calculate the Signature from the average of that position from each window
			double[] Signature = new double[windows.length];
			for(i=0;i<windows.length;i++)
				for(int j=0;j<windows[i].length;j++)
					Signature[j] += (windows[i][j]/windows.length);
			
			//Code to find the Dynamic Time Wrapping distance between 2 time series
			DenseInstance window1 = new DenseInstance(windows[0]);
			DenseInstance window2 = new DenseInstance(windows[1]);
			DTWSimilarity similarity = new DTWSimilarity();
			System.out.println(similarity.measure(window1, window2));
		}
		
		return 0;
	}

}


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import edu.emory.mathcs.jtransforms.dst.DoubleDST_1D;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.distance.dtw.DTWSimilarity;
import org.python.util.PythonInterpreter;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class FFTTrial {

	public static double mean(double[] m) {
	    double sum = 0;
	    for (int i = 0; i < m.length; i++) {
	        sum += m[i];
	    }
	    return sum / m.length;
	}
	
	public static int SignatureDrivenPrediction(File sourcefile)
	{
		int WINDOWSIZE = 100;
		Double cpuinput [] = new Double [WINDOWSIZE];
		double[] fftoutput = new double [WINDOWSIZE*2];
		Double re,im;
		ArrayList<Double> magnitude = new ArrayList<Double>();
		int i=0;
		Scanner scan = null;
		
		//Read metric entries from the file
		 try {
		        scan = new Scanner(sourcefile);
		        while(scan.hasNextDouble())
		        {
		        	cpuinput[i]=scan.nextDouble();
		        	fftoutput[i]=cpuinput[i];
		            i++;
		        }

		    } catch (FileNotFoundException e1) {
		            e1.printStackTrace();
		    }
		finally{
			 scan.close();
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

		 
		//Initialize the extra slots in fftoutput to -1
		for (;i<WINDOWSIZE*2;i++)
		{
		    	fftoutput[i] = -1;
		}
		
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
		
		
		
		//Set the Pattern Window Size and divide original timeseries into multiple Pattern Windows
		int patternWindowSize = 20;
		double[][] windows = new double[(int) Math.ceil(cpuinput.length/patternWindowSize)][patternWindowSize];
		int window_count=0;
		
		for(i = 0; i<cpuinput.length;i+=patternWindowSize)
		{
			if((i+patternWindowSize-1) >= (cpuinput.length-1))
			{
				int temp = 0;
				for(int j=i;j<cpuinput.length;j++)
					windows[window_count][temp++] = cpuinput[j].doubleValue();
			}
			else
			{
				int temp = 0;
				for(int j=i;j<i+patternWindowSize;j++)
					windows[window_count][temp++] = cpuinput[j].doubleValue();
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
		
		//Code to find the Dynamic Time Wrapping distance between 2 time series
		DenseInstance window1 = new DenseInstance(windows[0]);
		DenseInstance window2 = new DenseInstance(windows[1]);
		DTWSimilarity similarity = new DTWSimilarity();
		System.out.println(similarity.measure(window1, window2));
		
		
		
		return 0;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int WINDOWSIZE = 100;
		int SAMPLERATE = 1;
		Double cpuinput [] = new Double [WINDOWSIZE];
		double[] fftoutput = new double [WINDOWSIZE*2];
		int i=0;
		
		Double re,im;
		ArrayList<Double> magnitude = new ArrayList<Double>();
		
		Scanner scan;
	    File file = new File("D:\\NCSU\\Android_Workspace\\FFT_sample\\src\\sineWaveDummyData.txt");
	    try {
	        scan = new Scanner(file);

	        while(scan.hasNextDouble())
	        {
	        	cpuinput[i]=scan.nextDouble();
//	        	cpuinput[i] += (Math.random()*10 -5);
	        	fftoutput[i]=cpuinput[i];
	            System.out.println( cpuinput[i]);
	            i++;
	        }

	    } catch (FileNotFoundException e1) {
	            e1.printStackTrace();
	    }
//	    for (i=0;i<WINDOWSIZE;i++)
//		{
//	    	fftoutput[2 * i] = cpuinput[i];
//	    	fftoutput[2 * i + 1] = 0;
//		}
		
		
		DoubleFFT_1D fft = new DoubleFFT_1D(WINDOWSIZE);
		fft.realForwardFull(fftoutput);
//	    DoubleDST_1D fft = new DoubleDST_1D(WINDOWSIZE);
//		fft.forward(fftoutput, true);
	    
		
//		DoubleFFT_1D fft = new DoubleFFT_1D(WINDOWSIZE);
//		fft.complexForward(fftoutput);
		
		System.out.println("Input   Output");
		for (;i<WINDOWSIZE*2;i++)
		{
			if (i<100)
				System.out.print(cpuinput[i] + "   ");
			System.out.print( fftoutput[i] + "\n");
		}
		
		
		for (i = 0;i<=WINDOWSIZE - 1;i++)
		{
			re = fftoutput[2*i];
			im = fftoutput[2*i+1];
			magnitude.add((double) Math.sqrt(re*re+im*im));
		}

//		magnitude.subList(fromIndex, toIndex);
//		magnitude.toArray(window);
//		Collections.sort(magnitude);
//		System.out.println("Sorted Magnitude List");
//		for(i=0;i<magnitude.size();i++)
//			System.out.println(magnitude.get(i));
		
		
		System.out.println("Unorted Magnitude List");
		for(i=0;i<magnitude.size();i++)
		{
			System.out.println(magnitude.get(i));
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
//		Double dominatingFreq = -1.0;
//		Double maxVal = (double) -1;
//		int max_i=-1;
//		System.out.println("Real and Complex value");
//		for (i = 0; i < fftoutput.length; i += 2) { // we are only looking at the half of the spectrum
////            double hz = ((i / 2.0) / fftoutput.length) * 1;
//            // complex numbers -> vectors, so we compute the length of the vector, which is sqrt(realpart^2+imaginarypart^2)
//            double vlen = Math.sqrt(fftoutput[i] * fftoutput[i] + fftoutput[i + 1] * fftoutput[i + 1]);
//            System.out.println("Real = "+fftoutput[i]+" Complex = "+fftoutput[i+1]);
//            
//            if (maxVal < vlen) {
//                // if this length is bigger than our stored biggest length
//            	maxVal = vlen;
//                max_i = i;
//            }
//        }
//
//        dominatingFreq = ((max_i / 2.0) / fftoutput.length) * 1;
//		
//		System.out.println("MaxVal = "+maxVal+" Dominating Frequency = "+dominatingFreq);
//		
		
		
//		int patternWindowSize = 20;
		int patternWindowSize = (int) Math.ceil(SAMPLERATE/dominatingFreq);
		System.out.println("PatternWindowSize = "+patternWindowSize);
//		Double[][] windows = new Double[(int) Math.ceil(cpuinput.size()/patternWindowSize)][patternWindowSize];
		double[][] windows = new double[(int) Math.ceil(cpuinput.length/patternWindowSize)][patternWindowSize];
		int window_count=0;
		
		for(i = 0; i<cpuinput.length;i+=patternWindowSize)
		{
			if((i+patternWindowSize-1) >= (cpuinput.length-1))
			{
//				cpuinput.subList(i, cpuinput.size()).toArray(windows[window_count]);
				int temp = 0;
				for(int j=i;j<cpuinput.length;j++)
					windows[window_count][temp++] = cpuinput[j].doubleValue();
			}
			else
			{
//				cpuinput.subList(i, i+patternWindowSize).toArray(windows[window_count]);
				int temp = 0;
				for(int j=i;j<i+patternWindowSize;j++)
					windows[window_count][temp++] = cpuinput[j].doubleValue();
			}
			window_count++;
		}

		for(i=0;i<windows.length;i++)
		{
			for(int j=0;j<windows[i].length;j++)
				System.out.print(windows[i][j]+"  ");
			System.out.println();
		}
		
		PearsonsCorrelation correlationValues = new PearsonsCorrelation();
		double[][] correlationResultVaule = new double[windows.length][windows.length]; //REMOVE
		boolean[][] correlationResult = new boolean[windows.length][windows.length];
		double[][] MeanRatioVaule = new double[windows.length][windows.length];			//REMOVE
		boolean[][] MeanRatio = new boolean[windows.length][windows.length];
		double[] Signature = new double[windows.length];
		for(i=0;i<window_count;i++)
		{
			Signature[i] = 0;
		}
		for(i=0;i<windows.length;i++)
		{
			for(int j=0;j<windows[i].length;j++)
				Signature[j] += windows[i][j];
		}
		for(i=0;i<window_count;i++)
		{
			Signature[i] /= windows.length;
			System.out.print(Signature[i] + "  ");
		}
		
		for(i=0;i<windows.length;i++)
		{
			for(int j=i;j<windows.length;j++)
			{
				correlationResultVaule[i][j] = correlationResultVaule[j][i] = correlationValues.correlation(windows[i], windows[j]);
				if(correlationResultVaule[i][j] > 0.85)
					correlationResult[i][j] = correlationResult[j][i] = true;
				else
					correlationResult[i][j] = correlationResult[j][i] = false;
//				System.out.println("Correlation of Window "+i+" and "+j+" is "+correlationResultVaule[i][j]+" "+correlationResult[i][j]);
				
				MeanRatioVaule[i][j] = (mean(windows[i])) / (mean(windows[j]));
				if(MeanRatioVaule[i][j] >= 0.95 && MeanRatioVaule[i][j] <= 1.05 )
					MeanRatio[i][j] = MeanRatio[j][i] = true;
				else
					MeanRatio[i][j] = MeanRatio[j][i] = false;
//				System.out.println("Ratio of Means of Window "+i+" and "+j+" is "+MeanRatioVaule[i][j]+" "+MeanRatio[i][j]);
			}
		}
		System.out.println("Correlation Value Table");
		for(i=0;i<windows.length;i++)
		{
			for(int j=0;j<=i;j++)
				System.out.print(correlationResult[i][j] + "   ");
			System.out.println();
		}
		System.out.println("Ratio of Mean Value Table");
		for(i=0;i<windows.length;i++)
		{
			for(int j=0;j<=i;j++)
				System.out.print(MeanRatio[i][j] + "   ");
			System.out.println();
		}
		
		System.out.println("Similar Windows are:");
		boolean isRepeatingPattern = true;
		boolean[][] SimilarWindows = new boolean[windows.length][windows.length];
		for(i=0;i<windows.length;i++)
		{
			for(int j=0;j<windows.length;j++){
				SimilarWindows[i][j] = correlationResult[i][j] && MeanRatio[i][j];
				System.out.print(SimilarWindows[i][j] + "   ");
				isRepeatingPattern = isRepeatingPattern && SimilarWindows[i][j];
			}
			System.out.println();
		}
		System.out.println("Signature-Based Result "+isRepeatingPattern);
		
		
		
		DenseInstance window1 = new DenseInstance(windows[window_count-1]);
		DenseInstance window2 = new DenseInstance(windows[1]);
		
		DTWSimilarity similarity = new DTWSimilarity();
		System.out.println(similarity.measure(window1, window2));
		
		
		
	
		
		
		
		
		
		
		
		
		
		


		
//		/*MarkovChainModel Testing Code*/
//		MarkovChainModel MyModel = new MarkovChainModel(40);
//		MyModel.trainMarkovChainModel(cpuinput);
//		MyModel.printTransition_matrix();
//		MyModel.predictState(48, 10);
		
		
	}

}

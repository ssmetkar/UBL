package com.ncsu.press;

import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import com.ncsu.ubl.configuration.VMConfiguration;

public class MarkovChainModel{
		private static Logger logger = Logger.getLogger(MarkovChainModel.class); 
		private Integer num_of_states;
		private SimpleMatrix transition_matrix;
		
		public MarkovChainModel()
		{
			num_of_states = 0;
			transition_matrix = null;
		}
		
		public MarkovChainModel(Integer stateNum)
		{
			num_of_states = stateNum;
			transition_matrix = new SimpleMatrix(stateNum, stateNum);
			//Transition Matrix elements are initialized to 0
		}
		
		public int getBinNumber (double org_val)
		{
			if (org_val >100 || org_val<0)
			{
				logger.info("Invalid Number passed to MarkovChainModel.getBinNumber");
				return -1;
			}
			return (int) Math.floor(org_val*num_of_states/100);
		}
		
		public int getOrgValFromBinNumber(int bin_number)
		{
			return  (int) Math.ceil((bin_number+1)*100/num_of_states);
		}
		
		public void trainMarkovChainModel (double[] input)
		{
			int input_size = input.length;
			int from_state, to_state;
			Integer[] count = new Integer[num_of_states];
			for(int i=0;i<num_of_states;i++)
				count[i] = 0;
			
			//Loop through the input and record the number of transitions
			for (int i = 0; i<input_size-2;i++)
			{
				from_state = getBinNumber(input[i]);
				to_state = getBinNumber(input[i+1]);
//				Increment entry by 1
//				transition_matrix[from_state][to_state]++;
				transition_matrix.set(from_state, to_state, transition_matrix.get(from_state, to_state)+1);

				count[from_state]++;
				logger.info("FromVal:"+input[i]+" FromBin: "+from_state+
						" ToVal:"+input[i+1]+" ToBin: "+to_state+
						" TransitionCount: "+transition_matrix.get(from_state, to_state)+
						" TotalCount: "+count[from_state]);
			}
			
			//Calculate the transition probability matrix 
			for(int i = 0; i<num_of_states;i++)
			{
				for (int j=0; j<num_of_states;j++)
				{
					if (count[i] == 0)
					{
//						transition_matrix[i][j]
						transition_matrix.set(i, j, 0);
					}
					else
					{
//						transition_matrix[i][j]/=count[i];
						transition_matrix.set(i, j, transition_matrix.get(i,j)/count[i]);
					}
				}
			}
		}
		
		
		public int predictState(int from_state, int number_of_steps)
		{
			long lStartTime = System.currentTimeMillis();
			int from_bin = getBinNumber((float)from_state);
			SimpleMatrix result = new SimpleMatrix(transition_matrix);
			for(int i = 1; i<number_of_steps;i++)
				result = result.mult(transition_matrix);
				
			SimpleMatrix row_result = result.extractVector(true, from_bin);
			row_result.print();
			
			double maxVal = row_result.get(0, 0);
			int maxValIndex = 0;
			for(int i = 1;i<row_result.numCols();i++)
			{
				if (row_result.get(0,i)>=maxVal)
				{
					maxVal = row_result.get(0,i);
					maxValIndex = i;
				}
			}
			long lEndTime = System.currentTimeMillis();
			logger.info(("From: "+from_state+" In NumberOfSteps: "+number_of_steps+" MaxProbabilityBin: "+maxValIndex+"  MostProbablyTo: "+getOrgValFromBinNumber(maxValIndex)));
			logger.info(("Time taken for prediction = "+(lEndTime-lStartTime)));
			return getOrgValFromBinNumber(maxValIndex);
		}
		
		/**
		 * @return the num_of_states
		 */
		public Integer getNum_of_states() {
			return num_of_states;
		}

		/**
		 * @return the transition_matrix
		 */
		public SimpleMatrix getTransition_matrix() {
			return transition_matrix;
		}
		
		/**
		 * @print the transition_matrix
		 */
		public int printTransition_matrix() {
			if (transition_matrix == null)
			{
				logger.info(("The model has not been initialized yet"));
				return -1;
			}
			transition_matrix.print();
//			System.out.println("The Transition Probability Matrix is:");
//			for(int i = 0; i< num_of_states; i++)
//			{
//				for(int j = 0; j<num_of_states;j++)
//					System.out.print(transition_matrix[i][j] + "   ");
//				System.out.println();
//			}
			return 0;
		}
		
	}
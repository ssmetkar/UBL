package com.ncsu.ubl.utility;

import com.ncsu.jknnl.topology.HexagonalTopology;
import com.ncsu.jknnl.topology.MatrixTopology;
import com.ncsu.jknnl.topology.TopologyModel;

/**
 * Factory class to create TopologyModel
 * @author siddhivinayak
 *
 */
public class TopologyModelFactory {
	
	public static TopologyModel getTopologyModel(String topologyModelType, int numberOfRows,int numberOfCols,int radius)
	{
		TopologyModel topologyModel = null;
		if(topologyModelType.equalsIgnoreCase("hexagonal"))
		{
			topologyModel = new HexagonalTopology(numberOfRows,numberOfCols);
		}
		else
		{
			topologyModel = new MatrixTopology(numberOfRows, numberOfCols, radius);
		}
		return topologyModel;
	}

}

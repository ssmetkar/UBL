import os
import numpy
import timeit
from sklearn import preprocessing
from numpy.testing.utils import assert_array_equal

start = timeit.time.time()

metric = numpy.array([[0.0,0.0,0.0,0.0,0.0]])

#. Read the log file and get only relevent metrics
# metric = numpy.array([['CPU_USAGE','MEM_USAGE','NET_IN','NET_OUT','LOAD1','LOAD5']])
with open('D:\\Study Stuff\\AdvDS\\Project\\ADS_Project_Snippets\\src\\testing.log', mode='r') as logfile:
    for line in logfile:
		data = line.split(' ')
		data = [data[0],data[4],data[8],data[14],data[27]]
		data = [float(s) for s in data]
		metric = numpy.append (metric,[data],0)

#. Create a Standardizer
min_max_scaler = preprocessing.MinMaxScaler(feature_range=(0,100))
ScaledMetricData = min_max_scaler.fit_transform(metric)
#assert_array_equal(ScaledMetricData.min(axis=0),0)
#assert_array_equal(ScaledMetricData.max(axis=0),100)
# print(ScaledMetricData)
for i in range(0,len(ScaledMetricData)):
	metric[i,1] = ScaledMetricData[i,1]
	metric[i,2] = ScaledMetricData[i,2]
	metric[i,3] = ScaledMetricData[i,3]
#. Code to save the preprocessed data in a file in 2 formats
numpy.savetxt('D:\\Study Stuff\\AdvDS\\Project\\ADS_Project_Snippets\\src\\testNormalized.txt', metric, fmt='%.4f',delimiter ='	')

end = timeit.time.time()

print ("Time taken to read and store is {0}".format(end-start))

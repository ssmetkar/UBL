import numpy
import timeit
from sklearn import preprocessing
from scipy import ndimage

start = timeit.time.time()

metric1 = numpy.array([[0.0]])
#. Read the log file and get only relevent metrics
# metric = numpy.array([['MEM_USAGE']])
# with open('D:\\NCSU\\Android_Workspace\\ADS_Project_Snippets\\src\\newMEM.log', mode='r') as logfile:
with open('/var/nfs/ubuntupara3_mem_temp.log', mode='r') as logfile:
    for line in logfile:
        data = line.split(' ')
        data = [data[4]]
        data = [float(s) for s in data]
        metric1 = numpy.append (metric1,[data],0)

#Remove 1st row which was a dummy row
metric1 = metric1[1:] 
metric1_num = metric1.shape[0]
# print("MEM METRIC")
# print(metric1_num)


metric2 = numpy.array([[0.0]])
#. Read the log file and get only relevent metrics
# metric = numpy.array([['CPU %']])
# with open('D:\\NCSU\\Android_Workspace\\ADS_Project_Snippets\\src\\newCPU.log', mode='r') as logfile:
with open('/var/nfs/Ctemp.log', mode='r') as logfile:
    for line in logfile:
        data = line.split(' ')
        data = [data[4]]
        data = [float(s) for s in data]
        metric2 = numpy.append (metric2,[data],0)

#Remove 1st row which was a dummy row
metric2 = metric2[1:]
metric2_num = metric2.shape[0] 
# print("OTHER METRIC")
# print(metric2_num)
# print(metric2)

# Ensure same lines of code in both files
if metric1_num > metric2_num:
    metric1 = metric1[0:metric2_num]
elif metric2_num > metric1_num :
    metric2 = metric2[0:metric1_num]

metric = numpy.append(metric1,metric2,1)
# print("COMBINED METRIC")
# print(metric.shape)
# print(metric)
 
# print(metric)
print(min(a for (a,b) in metric))
print(min(b for (a,b) in metric))
print(max(a for (a,b) in metric))
print(max(b for (a,b) in metric))
 
 
#. Create a Standardizer
min_max_scaler = preprocessing.MinMaxScaler(feature_range=(0,100))
ScaledMetricData = min_max_scaler.fit_transform(metric)
# print(ScaledMetricData)
 
 
# Save the Model onto a file
# scale = [min_max_scaler.scale_]
# min = [min_max_scaler.min_]
# data_range = [min_max_scaler.data_range]
# model = numpy.array([[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]])
# model = numpy.append(model,min,0)
# model = numpy.append(model,scale,0)
# model = numpy.append(model,data_range,0)
# model = model[1:] 
# numpy.savetxt('D:\\NCSU\\Android_Workspace\\ADS_Project_Snippets\\src\\normalizeMODEL.txt', model)
 
 
 
#. Data Smoothing using 1D Guassian Filtering with Variance = 1
for i in range(len(ScaledMetricData[1])):
    temp_row = [row[i] for row in ScaledMetricData]
    smoothened_row = ndimage.filters.gaussian_filter1d(temp_row, 1, -1, 0, None, 'reflect', 0.0)
    ScaledMetricData[:,i] = smoothened_row;
 
#. Code to save the preprocessed data in a file in 2 formats
# numpy.savetxt('D:\\NCSU\\Android_Workspace\\ADS_Project_Snippets\\src\\newNORMALIZEDDATA.txt', ScaledMetricData,fmt='%.4f',delimiter ='\t')
numpy.savetxt('/var/nfs/trainingNormalized.txt', ScaledMetricData, fmt='%.4f',delimiter ='\t')

end = timeit.time.time()
# print ("Time taken to read and store is {0}".format(end-start))

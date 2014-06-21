CSC 724 : Advanced Distributed System
Project Name : Unsupervised Behavior Learning for Prediction and Prevention of Performance anomalies in Virtualized Cloud System
Group Members :  Amit Katti ||| Bhargav Pejakala ||| Sarang Metkar

-------README-----------

1. Copy the jar 'ubl.jar' in build folder to the location where you want to run the application
2. Also copy the 'lib' and 'resource' folder present in the build folder to the location where you have copied the 'ubl.jar' file
3. The 'resource' folder consists of 'ubl.properties' file in which you can specify the location for training file and file using which
   online prediction is to be made. Write appropriate values for different properties in 'ubl.properties' file.
4. To start the application, open command prompt at the location where you have copied the 'ubl.jar' file and type following command

For Windows

c:>  java -cp ./*;lib/* com.ncsu.ubl.master.Controller

For Linux

root@root> java -cp ./*:lib/* com.ncsu.ubl.master.Controller
   
   A message will be displayed ' Press enter to start prediction ', press enter key.
   
   A log file will be created in 'logs' folder at the same location. You can see the result of running application in testlog.log file
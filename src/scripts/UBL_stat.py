import subprocess
from subprocess import Popen, PIPE
import os
#pipe = os.popen('/usr/sbin/xentop -b -i2 -d.1')
#a = pipe.readline()
#for line in pipe.readline():
 
#subprocess.call(["/usr/sbin/xentop","-b"])
ls_proc = Popen(['/usr/sbin/xentop','-b','-i100','-d.1'], stdout=open('outfile.txt', 'w'))
out, err = ls_proc.communicate()

#import commands
#import re
#a = commands.getstatusoutput('/usr/sbin/xentop -b -i 2 -d.1')
#print len(a)
#print a[0]
#b =str(a)
#for i in a:
 #   print type(i)
#print "This is a tuple: %s" % str(t)
#print b.split("     ")
#print b.split("\n")
#print b.strip()
#b = b.sub('[]', '', line)
	
#print format(*c)	
#b = ' '.join(a);
#c=b.split(sep="\n", maxsplit=1);
#print b[5]
#!/usr/bin/perl -w
 
use strict;
use Sys::Hostname;
sub trim($);
print "Pid of process is: $$\n";
run_sleep();

sub run_sleep { 
	 #my $pid = fork;
     #if($pid != 0) {
	  #exit;
	 #}
	#return if $pid;     # in the parent process
	#print "Pid of process is: $pid\n";
	while(1){
		getStat();
		sleep(1);
	}
	exit;
 }

sub getStat
{
my @result = split(/\n/, `/usr/sbin/xentop -b -i 2 d.1`);
 
# remove the first linexen top
shift(@result);
#print @result;
#shift(@result) while @result && $result[0] !~ /^xentop - /;
 
# the next 3 lines are headings..
shift(@result);
shift(@result);
shift(@result);
#shift(@result);
# print @result;
 
foreach my $line (@result)
{
  #print($ARGV[0]);
  my $find = "no limit";
  my $replace = "nolimit";
  $line =~ s/$find/$replace/g;
  #print($line);
  my @xenInfo = split(/[\t ]+/, trim($line));
  my $timestamp = getLoggingTime();
  my $filename = '>>' . $ARGV[0] .'.log';
  #print($filename);
  open (MYFILE, $filename);
  $timestamp =~ tr/ //ds;
  #my $vari = `hostname`;
  #print($vari);
  if ($xenInfo[0] eq $ARGV[0]) {
  #print MYFILE "Time";
  #print MYFILE " ";
  print MYFILE $timestamp;
  #print MYFILE " ";
  #print MYFILE "DomainName";
  #print MYFILE " ";
  #print MYFILE $xenInfo[0];
  print MYFILE " ";
  print MYFILE "CPU_USAGE";
  print MYFILE " ";
  print MYFILE $xenInfo[2];
  print MYFILE " ";
  print MYFILE "CPU(%)";
  print MYFILE " ";  
  print MYFILE $xenInfo[3];
  #print MYFILE " ";
  #print MYFILE "MEM(k)";
  #print MYFILE " ";  
  #print MYFILE $xenInfo[4];
  #print MYFILE " ";
  #print MYFILE "MEM(%)";
  #print MYFILE " ";
  #print MYFILE $xenInfo[5];
  #print MYFILE " ";
  #print MYFILE "MAXMEM(k)";  
  #print MYFILE " ";
  #print MYFILE $xenInfo[6];
  #print MYFILE " ";
  #print MYFILE "MAXMEM(%)"; 
  #print MYFILE " ";  
  #print MYFILE $xenInfo[7];
  print MYFILE " ";
  print MYFILE "VCPUS";
  print MYFILE " ";
  print MYFILE $xenInfo[8];
  #print MYFILE " ";
  #print MYFILE "NETS";  
  #print MYFILE " ";
  #print MYFILE $xenInfo[9];
  print MYFILE " ";
  print MYFILE "NETTX(k)";  
  print MYFILE " ";
  print MYFILE $xenInfo[10];
  print MYFILE " ";
  print MYFILE "NETRX(k)";
  print MYFILE " ";
  print MYFILE $xenInfo[11];
  #print MYFILE " ";
  #print MYFILE "VBDS";  
  #print MYFILE " ";
  print MYFILE $xenInfo[12];
  print MYFILE " ";
  print MYFILE "VBD_OO";
  print MYFILE " ";
  print MYFILE $xenInfo[13];
  print MYFILE " ";
  print MYFILE "VBD_RD";
  print MYFILE " ";
  print MYFILE $xenInfo[14];
  print MYFILE " ";
  print MYFILE "VBD_WR";
  print MYFILE " ";
  print MYFILE $xenInfo[15];
  #print MYFILE " ";
  #print MYFILE "SSID";
  #print MYFILE " ";
  #print MYFILE $xenInfo[16];
  print MYFILE "\n";
  close MYFILE;
 }
 }
 }
# trims leading and trailing whitespace
sub trim($)
{
  my $string = shift;
  $string =~ s/^\s+//;
  $string =~ s/\s+$//;
  return $string;
}
sub getLoggingTime {

    my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst)=localtime(time);
    my $nice_timestamp = sprintf ( "%04d%02d%02d %02d:%02d:%02d",
                                   $year+1900,$mon+1,$mday,$hour,$min,$sec);
    return $nice_timestamp;
}

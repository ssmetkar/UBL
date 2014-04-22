#!/usr/bin/perl -w
 
use strict;
use Sys::Hostname;
sub trim($);
print "Pid of process is: $$\n";
run_sleep();

sub run_sleep { 
	 while(1){
		getStat();
		sleep(1);
	}
	exit;
 }

sub getStat
{
no warnings 'uninitialized';
#my $memorytotal = split(/\n/, `free -m | grep Mem | awk '{print $2}'`);
#my $memoryused = split(/\n/, `free -m | grep Mem | awk '{print $3}'`);
my $result = `free -m | grep Mem`;
#print $result;
my @xenInfo = split(/[\t ]+/, trim($result));
my $memoryused = $xenInfo[2];
my $memorytotal = $xenInfo[1]; 
my $timestamp = getLoggingTime();
 my $host = hostname();
my $filename = '>>' . $host .'_mem.log';
open (MYFILE, $filename);
$timestamp =~ tr/ //ds;
print MYFILE $timestamp;
print MYFILE " ";
print MYFILE "MEM_CAP";
print MYFILE " ";
print MYFILE $memorytotal;
print MYFILE " ";
print MYFILE "MEM_USAGE";
print MYFILE " ";
print MYFILE $memoryused;
print MYFILE "\n";
close MYFILE;
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

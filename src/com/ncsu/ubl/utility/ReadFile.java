package com.ncsu.ubl.utility;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class ReadFile {

	public static String readLast_N_Lines(File file, int numberOfLines)
	{
		RandomAccessFile randomAccessFile=null;
		StringBuilder result = new StringBuilder();
		
		try{
			randomAccessFile = new RandomAccessFile(file, "r");
			int linesRead = 0;
			StringBuilder builder = new StringBuilder();
			long length = file.length();
			boolean firstRead = true;
			length--;
			randomAccessFile.seek(length);
			
			for(long seek = length; seek >= 0; --seek)
			{
				randomAccessFile.seek(seek);
				char c = (char)randomAccessFile.read();
				
				if (firstRead && c == '\n')
                	continue;
                else
                	firstRead = false;
				
				builder.append(c);
				if(c == '\n'){
					builder = builder.reverse();
					result.append(builder.toString());
					linesRead++;
					builder = null;
					builder = new StringBuilder();
					if (linesRead == numberOfLines){
						break;
					}
				}
			}
		}catch( java.io.FileNotFoundException e ) {
			e.printStackTrace();
			return null;
		} catch( java.io.IOException e ) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (randomAccessFile != null )
				try {
					randomAccessFile.close();
				} catch (IOException e) {
				}
		}
		return result.toString();
	}

	
	//End of Class
}

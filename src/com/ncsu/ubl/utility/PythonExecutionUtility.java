package com.ncsu.ubl.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import com.ncsu.ubl.master.Controller;

public class PythonExecutionUtility {
	
	public static void main(String[] args) {

		ProcessBuilder p = new ProcessBuilder("python",Controller.getConfig().getPythonLocation());
		Process proc = null;
		try {
			proc = p.start();
			BufferedReader output = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			String readLine = "";

			while ((readLine = output.readLine()) != null) {
			    System.out.println(readLine);
			}

			while ((readLine = error.readLine()) != null) {
			 System.out.println(readLine);
			}
		} catch (IOException e) {
			System.err.println("");
		}
	}
}

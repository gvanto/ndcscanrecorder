package com.plaslantic.common;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.Map.Entry;

import ndc.scanner.io.*;


/**
 * This class handles all socket interaction to NDC 8000 scanner
 * Basically runs a loop on a X-minute cycle which runs the NDC 8000 command 1009? (last 100 scans data)
 * See NDC 8000 scanner [8000 Open Access.pdf] documentation (page 53, 116) for more info.
 * 
 * @author Gert (gvanto@gmail.com)
 *
 */
public class NdcScanRecorder {
	
	private static String app_name = "NdcScanRecorder v2";
	private static String host;
	private static int port;
	private static String scannerName; // Welex1 / Welex2
	private static int cycleSecondsOverride;
	private static String dataOutFolder;
	
	private static NdcScanFileWriter fileWriter = null; // the class doing all the file writing work
	
	// Socket I/O
	private static Socket socket = null;  
	private static PrintWriter printWriter = null;       
	private static BufferedReader bufferedReader = null;
	
	// Ndc command IO
	private static AppInitIO appInitIO;
	private static RollNumberIO rollNumberIO;
	private static Last100ScansIO last100ScansIO;
	private static RollProfileNoScansIO rollProfileNoScansIO;
	private static RollProfileDataIO rollProfileDataIO;
	private static HistogramDataIO histogramDataIO;
	private static HistogramStatsIO histogramStatsIO;
	
	
	public static void main(String[] args) throws InterruptedException
	{
		if(!loadConfig()) {
			System.err.println("Error loading config. Aborting...");
			System.exit(0);
		}
		
        try {
        	fileWriter = new NdcScanFileWriter(dataOutFolder, scannerName, "rollNo,time,max,ave,min,rollProfileNo&Data_histStats&histData");
        	
        	// to open printWriter + bufferedReader
            socket = new Socket(host, port);
            // writes input commands
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            // reads responses
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));       
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host + ":" + port);
            System.exit(0);
        }
        
        
		// If all went well, let's try and write to socket
        if (socket != null && printWriter != null && bufferedReader != null) {
    		appInitIO = new AppInitIO(printWriter, bufferedReader, host, port);
        	rollNumberIO = new RollNumberIO(printWriter, bufferedReader);
        	last100ScansIO = new Last100ScansIO(printWriter, bufferedReader);
        	rollProfileNoScansIO = new RollProfileNoScansIO(printWriter, bufferedReader);
        	rollProfileDataIO = new RollProfileDataIO(printWriter, bufferedReader);
        	histogramDataIO = new HistogramDataIO(printWriter, bufferedReader);
        	histogramStatsIO = new HistogramStatsIO(printWriter, bufferedReader);
        	
        	//always run first
    		appInitIO.initHostInterface(); // send a command to init host interface
    		
    		
    		/*** MAIN LOOP ***/
    		int cycleSeconds = -1; // poll interval
    		int cycleMin;
    		int rollNo = -1, prevRollNo;
    		Last100ScansResponse last100ScansResponse; // loop delay calc'ed from response.cycleSeconds
    		NdcTime lastWrittenTime = null; // avoid time overlaps
    		boolean isDataAvailable = true; // if roll(ing) data available
    		String now;
    		
    		// store these on every loop. on roll transition, write prev loop's stats values 
    		int rollProfileNoScans = -1;
    		String rollProfileData = "";
    		String histData = "";
    		String histStats = "";
    		
    		/**
    		 * CSV line to write. Format:
    		 * rollNo, last100={time, max, ave, min}, 
    		 * "rollNo,time,max,ave,min,rollProfileNo&Data_histData&histStats"
    		 * When roll has transitioned, write rollProfileNo, Data \n histData, histStats
    		 */
    		String line;
    		ArrayList<String> lines = new ArrayList<String>(); // lines to write to csv
    		
    		while (true) {
    			line = ""; // clear
    			lines.clear();
    			
    			prevRollNo = rollNo;
    			rollNo = rollNumberIO.getRollNumber();
    			
    			// todo: take out rollNo dev override
    			echo("rollNo=" + rollNo + " prevRollNo=" + prevRollNo);
    			
    			// Check for roll transition
    			if (prevRollNo != -1 && rollNo == prevRollNo + 1) {
    				echo("NEW ROLL");
    				
    				if (isDataAvailable) {
    					line = ",,,,rollProfileNoScans," + rollProfileNoScans + ",rollProfileData," + rollProfileData;
        				lines.add(line);
        				
        				line = ",,,,histStats," + histStats + ",histData," + histData;
        				lines.add(line);
    				}
    				
    				cycleSeconds = 10;
    			} else {
    				last100ScansResponse = last100ScansIO.getResponse();
    				
        			for (Entry<NdcTime, String> scanRecord : last100ScansResponse.getTimeValuesMap().entrySet()) {
        				NdcTime time = scanRecord.getKey();
        				String  values = scanRecord.getValue();
        				//echo("time=" + time.toString() + " values=" + values);
        				
        				// to avoid overlap check we havent written this time already
//        				if (lastWrittenTime != null) {
//        					echo("time=" + time.toString() + " lastWritten=" + lastWrittenTime.toString());
//        				}
        				
        				if (lastWrittenTime == null || (lastWrittenTime != null && time.isAfter(lastWrittenTime))) {
        					line = rollNo + "," + time.toString() + "," + values;
            				lines.add(line);
            				lastWrittenTime = time.clone();
        				}
        			}
        			
//        			NdcTime[] times = last100ScansResponse.getTimeValuesMap().keySet().toArray(new NdcTime[0]);
//        			lastWrittenTime = times[times.length - 1];
//        			echo("lastWrittenTime=" + lastWrittenTime.toString());
        			
        			if (lines.size() > 0) {
        				rollProfileNoScans = rollProfileNoScansIO.getNumberOfScansAveraged();
        	    		//echo("rollProfileNoScans=" + rollProfileNoScans);				
        	    		rollProfileData = rollProfileDataIO.getRollProfileData();
        	    		//echo("rollProfileData=" + rollProfileData);  	    		
        	    		histData = histogramDataIO.getHistogramData();
        	    		//echo("histData=" + histData);   	    		
        	    		histStats = histogramStatsIO.getHistogramStats();
        	    		//echo("histStats=" + histStats);
        	    		isDataAvailable = true;
        			} else {
        				// no scannin goin on
        				lines.add(NdcUtils.getSqlDateTime() + ": No scan data received.");
        				isDataAvailable = false;
        			}
        			
        			cycleSeconds = last100ScansResponse.getCycleSeconds();
    			}
    			
    			if (lines.size() > 0) {
    				fileWriter.writeScanData(lines.toArray(new String[0]));
    			}
    			
    			// Loop waiting logic
        		if (cycleSeconds == -1) {
	    			echo("WARNING: cycleSeconds did not calculate properly. Resetting to 1740 secs (= 29 min)");
	    			cycleSeconds = 1740;
        		}
	    		cycleMin = (cycleSeconds - (cycleSeconds%60)) / 60;
	    		
	    		// If for some reason cycleSeconds > 29minutes, we cap it at 29min
	    		if (cycleSeconds > 1740) {
	    			echo("WARNING: cycleSeconds=" + cycleSeconds + "(= " + cycleMin + " min) is more than 29 min. Resetting cycleSeconds to 1740 secs (=29 min)");
	    			cycleSeconds = 1740;
	    		}    		
	    		
	    		// see config/app.properties > cyclesecondsoverride
	    		if (cycleSecondsOverride != -1) {
	    			cycleSeconds = cycleSecondsOverride; // dev testing only
		    		cycleMin = (cycleSeconds - (cycleSeconds%60)) / 60;
	    		}
	    		
	    		echo("Waiting " + cycleSeconds + " seconds (= " + cycleMin + " min) before next scan data request...");
	    		Thread.sleep(cycleSeconds * 1000);
	    		
    		} // end: while(true)
    		
        } // end: if (socket != null && printWriter != null && bufferedReader != null)
       
	}
	
	private static boolean loadConfig()
	{
		Properties properties = new Properties();

		try {
			// load a properties file:
			// https://www.programcreek.com/2009/06/a-simple-example-to-show-how-to-use-java-properties-file/
			properties.load(NdcScanRecorder.class.getClassLoader().getResourceAsStream("config/app.properties"));
			
			
			if ((host = (String)System.getProperty("ipaddress")) == null) {
				if ((host = (String)properties.getProperty("ipaddress")) == null) {
					System.err.println("Error loading config: host");
					return false;
				}
			}
			echo("loadConfig: Loaded IP address (host): " + host);
			
			echo("loadConfig: Loading port");
			port = Integer.parseInt(properties.getProperty("port"));
			echo("loadConfig: Loaded port: " + port);
			
			// try and get from system property first:
			// https://stackoverflow.com/questions/21322384/java-command-line-arguments-in-key-value-format
			if ((scannerName = (String)System.getProperty("scanner_name")) == null) {
				if ((scannerName = (String)properties.getProperty("scanner_name")) == null) {
					System.err.println("Error loading config: scanner_name");
					return false;
				}
			}
			echo("loadConfig: Loaded scanner name: " + scannerName);
			
			echo("loadConfig: Loading cycle_seconds_override");
			cycleSecondsOverride = Integer.parseInt(properties.getProperty("cycle_seconds_override"));
			echo("loadConfig: Loaded cycleSecondsOverride: " + cycleSecondsOverride);
			
			if ((dataOutFolder = (String)System.getProperty("data_out_folder")) == null) {
				if ((dataOutFolder = (String)properties.getProperty("data_out_folder")) == null) {
					System.err.println("Error loading config: data_out_folder");
					return false;
				}
			}
			echo("loadConfig: Loaded dataOutFolder: " + dataOutFolder);
			
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public static void closeApplicationNicely()
	{
		echo ("closeApplicationNicely: Closing " + app_name);
		try {
    		socket.close();
    		printWriter.close();
    		bufferedReader.close();		
		} catch (IOException e) {
	        System.err.println("Error (while attempting to close socket / printwriter / br): " + e.getMessage());	        
	    }		
		System.exit(0);		
	}
	
	public static void echo(String s)
	{
		NdcUtils.echo(s);
	}
	
}

package com.plaslantic.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


public class NdcScanFileWriter {
	
	private String dataOutFolder;
	private String scannerName, csvColHeadings;
	private BufferedWriter csvWriter;
	
	
	public NdcScanFileWriter(String dataOutFolder, String scannerName, String csvColHeadings) 
	{
		this.dataOutFolder = dataOutFolder;
		this.scannerName = scannerName;
		this.csvColHeadings = csvColHeadings;
	}
	
	
	public void writeScanData(String[] lines) 
	{
		//echo("writeScanData(): Writing data. lines.count=" + lines.length);
		this.csvWriter = this.getCsvBufferedWriter();
		
		try {
			for (String line : lines) {
				this.csvWriter.write(line);
				this.csvWriter.newLine();
			}
			this.csvWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Attempts to open file data/{scannerName}-{yyyy-mm-dd}.csv (or creates if not exist).
	 * In case of new (create), column headings will be written to first line.
	 * 
	 * @return bw
	 */
	private BufferedWriter getCsvBufferedWriter()
	{
		File file = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		boolean isFileNew = false;
		
		//String dataDir = System.getProperty("user.dir") // gives Java project root eg C:\workspace\NdcScanRecorder
		//		+ "\\scan_data";
		
		String filePath = this.dataOutFolder + "\\" 
			+ this.scannerName
			+ "-"
			+	NdcUtils.getTodayYearMonthDay() // yyyy-mm-dd
			+ ".csv";
		
		echo("Opening file: " + filePath);		
		file = new File(filePath);
		
		// If new file, create it & write column headings (else just append to it)
		if (!file.exists()) {
			try {
				file.createNewFile();
				isFileNew = true;
			} catch (IOException e) {				
				e.printStackTrace();
				echo("Error trying to create file: " + filePath);
				NdcScanRecorder.closeApplicationNicely();				
			}
		}
		
		try {
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			
			// if new, write column headings
			if (isFileNew) {
				bw.write(this.csvColHeadings);
				bw.newLine();
			}
		} catch (IOException e) {
			echo("getCsvBufferedWriter: bufferedWriter opening error.");
			e.printStackTrace();
			NdcScanRecorder.closeApplicationNicely();
		}
			
		return bw;			
	}
	
//	private static DateTime getDateTimeFromString(String dtStr) 
//	{
//		DateTime dt = null;
//	}
	
		
	private static void echo(String s)
	{
		NdcUtils.echo(s);
	}
}

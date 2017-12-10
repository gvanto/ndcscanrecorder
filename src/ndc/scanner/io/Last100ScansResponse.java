package ndc.scanner.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.plaslantic.common.NdcTime;
import com.plaslantic.common.NdcUtils;

/**
 * Response object for storing:
 * - cycleSeconds
 * - map<time, values> where values=0.395,0.265,0.197
 *
 */
public class Last100ScansResponse
{
	private  int cycleSeconds;
	private  LinkedHashMap<NdcTime, String> timeValuesMap;
	private  NdcTime lastPollEndTime; // to avoid getting overlaps - poll = 100scans (1 scan request)
	
	public int getCycleSeconds()
	{
		return this.cycleSeconds;
	}
	
	public LinkedHashMap<NdcTime, String> getTimeValuesMap()
	{
		return this.timeValuesMap;
	}
	
	public Last100ScansResponse(String line) // line = response line from Last100ScansIO
	{
		this.cycleSeconds = -1;
		// poll = 100scans
		NdcTime scanTime, pollStartTime, pollEndTime;
		pollStartTime = new NdcTime(0, 0, 0);
		pollEndTime = new NdcTime(0, 0, 0);
		
		//echo(this.getClass() + ": Processing last100scan data line: " + line);
		
		// Now all one big fat line. Split on the commas
		String[] lineArr = line.split("\\,");
		//echo("lineArr=" + java.util.Arrays.toString(lineArr));
		
		// Need to get all values into a matrix-type structure
		int numRows = lineArr.length / 4; // Should be 100
		if (numRows != 100) {
			echo("WARNING: Number of rows from scan is not 100.");
		}
		
		this.timeValuesMap = new LinkedHashMap<NdcTime, String>();		
					
		int k = 0; // array index
		for (int i = 0; i < numRows; i++) {		
			k = i * 4;
			
			scanTime = new NdcTime(lineArr[k]);
			
			// Get start and end scan times
			if (i == 0) { // scan start == first row					
				pollStartTime = scanTime;
				
			} else if(i == numRows - 1) {
				pollEndTime = scanTime;
			}	
			
			if (this.lastPollEndTime == null || (this.lastPollEndTime != null && scanTime.isAfter(lastPollEndTime))) {
				this.timeValuesMap.put(scanTime, lineArr[k + 1] + "," + lineArr[k + 2] + "," + lineArr[k + 3]);
			} else {
				echo(this.getClass() + "Omitting record from scanTime (previously recorded): " + scanTime.toString());
			}
									
		}
		
		//echo(this.getClass() + ": last100TimeValuesMap.size()=" + this.timeValuesMap.size());
	
		echo(this.getClass() + " [start, end] = " + pollStartTime.toString() + ", " + pollEndTime.toString());
		this.lastPollEndTime = pollEndTime;		
		
		this.cycleSeconds = NdcTime.getDifferenceInSeconds(pollStartTime, pollEndTime);	
	}
	
	public static void echo(String s)
	{
		NdcUtils.echo(s);
	}
}

package ndc.scanner.io;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Returns No of scans averaged in current roll profile
 * eg. 25  (single int)
 */
public class RollProfileNoScansIO extends NdcDataIO
{

	public RollProfileNoScansIO(PrintWriter printWriter, BufferedReader bufferedReader)
	{
		super(printWriter, bufferedReader);
		this.command = "1017";
		this.description = this.getClass().toString();
	}
	
	public int getNumberOfScansAveraged()
	{
		int noScans = -1;

		String line = this.sendCommandAndWaitForLineResponse();
		line = this.cleanLine(line);
		try {
			noScans = Integer.parseInt(line);
		} catch (NumberFormatException e) {
			echo("Failed to get number of scans averaged :-(");
		}
		return noScans;
	}
}

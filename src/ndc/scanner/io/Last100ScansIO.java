package ndc.scanner.io;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Gets last 100 scan data: (1009)
 * 
 * Response: time, max, ave, min
 * eg:
 * 23:36:16,	0.251,	0.243,	0.231
 *
 */
public class Last100ScansIO extends NdcDataIO
{

	public Last100ScansIO(PrintWriter printWriter, BufferedReader bufferedReader)
	{
		super(printWriter, bufferedReader);
		this.command = "1009"; // Last100ScanData
		this.description = this.getClass().toString();
	}
	
	/**
	 * Returns 100 lines of [time, max, ave, min]
	 */
	public Last100ScansResponse getResponse()
	{
		String line = this.sendCommandAndWaitForLineResponse();
		line = this.cleanLine(line);
		return new Last100ScansResponse(line);
	}
}

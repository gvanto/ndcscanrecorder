package ndc.scanner.io;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class HistogramStatsIO extends NdcDataIO
{

	public HistogramStatsIO(PrintWriter printWriter, BufferedReader bufferedReader)
	{
		super(printWriter, bufferedReader);
		this.command = "1036";
		this.description = this.getClass().toString();
	}
	
	public String getHistogramStats()
	{
		String line = this.sendCommandAndWaitForLineResponse();
		line = this.cleanLine(line);
		
		return line;
	}
}

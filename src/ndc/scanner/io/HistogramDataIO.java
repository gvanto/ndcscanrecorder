package ndc.scanner.io;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class HistogramDataIO extends NdcDataIO
{

	public HistogramDataIO(PrintWriter printWriter, BufferedReader bufferedReader)
	{
		super(printWriter, bufferedReader);
		this.command = "1035";
		this.description = this.getClass().toString();
	}
	
	public String getHistogramData()
	{
		String line = this.sendCommandAndWaitForLineResponse();
		line = this.cleanLine(line);
		
		return line;
	}
}

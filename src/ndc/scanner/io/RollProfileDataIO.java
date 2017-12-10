package ndc.scanner.io;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class RollProfileDataIO extends NdcDataIO
{

	public RollProfileDataIO(PrintWriter printWriter, BufferedReader bufferedReader)
	{
		super(printWriter, bufferedReader);
		this.command = "1013";
		this.description = this.getClass().toString();
	}
	
	public String getRollProfileData()
	{
		String line = this.sendCommandAndWaitForLineResponse();
		line = this.cleanLine(line);
		
		return line;
	}
}

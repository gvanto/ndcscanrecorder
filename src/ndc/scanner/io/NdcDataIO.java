package ndc.scanner.io;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.plaslantic.common.NdcScanRecorder;
import com.plaslantic.common.NdcUtils;

/**
 * Abstract class from which all IO command classes inherit from
 */

public abstract class NdcDataIO
{
	protected String command;
	protected String description = "";
	protected int waitForResponseTries = 0;
	
	protected PrintWriter printWriter; 
	protected BufferedReader bufferedReader;
	
	
	public NdcDataIO(PrintWriter printWriter, BufferedReader bufferedReader)
	{
		this.printWriter = printWriter;
		this.bufferedReader = bufferedReader;
	}
	
	private void sendCommand()
	{
		echo("Sending command: " + this.command + "? " + " " + this.description);
		printWriter.println(this.command + "?\n");
	}
	
	protected String sendCommandAndWaitForLineResponse()
	{
		String line = "";
		int inChar = 0;
		
		try {
			// Loop-read response infinitely
			while (true /*&& count < 7*/) {
				this.sendCommand();
				
	    		while (!this.bufferedReader.ready()) {
	    			echo("Waiting for reponse ...");
	    			this.waitForResponseTries++;
	    			
	    			if (this.waitForResponseTries >= 20) {
	    				echo("More than 20 'wait for response' tries, waiting 20min then restarting ...");
	    				NdcScanRecorder.closeApplicationNicelyAndRestart(20 * 60); // 45 * 60
	    			}
	    			
	    			Thread.sleep(1000);
	    		}
	    		
	    		// Once ready, we are at start of a line of input
	    		if (bufferedReader.ready()) {
	    			this.waitForResponseTries = 0;
	    			
	    			line = ""; //clear line 
	    			
	    			while (bufferedReader.ready()) {
	    				inChar = bufferedReader.read();
	    				line += Character.toString((char) inChar);
	    			}
	    			//echo(line);
	    			// make sure our response is the type in question
	    			if (line.contains(this.command.replaceAll("BASE:", "") + " = ")) {
	    				return line;
	    			}
	    		} // end if (br.ready()) 
			} // end: while(true)
		
	    } catch (Exception e) {
			e.printStackTrace();
			NdcScanRecorder.closeApplicationNicelyAndRestart(10);
		}
		
		return line;
	}
	
	
	
	/**
	 * Cleans and returns a response line. 
	 * 
	 * @param line - Response line (dirty)
	 * @return
	 */
	protected String cleanLine(String line)
	{
		//Remove crap and make entire thing into one big line
		
		// tokenize by " = " (sometimes contains multiple of these so always use last arr element)
		String[] arr = line.split("=");
		line = arr[arr.length - 1];
		
		//line = line.replace(firstCrap, "");
		
		line = line.replace("Ok", "");
		line = line.replace(" ", "");
		line = line.replace("\n", "");	
		line = line.replace("\r", "");			
		line = line.replace("\\", "");
		
		return line;
	}
	
	public static void echo(String s)
	{
		NdcUtils.echo(s);
	}
}









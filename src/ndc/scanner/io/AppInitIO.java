package ndc.scanner.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import com.plaslantic.common.NdcScanRecorder;

/**
 * Initialize the host interface
 */
public class AppInitIO extends NdcDataIO
{
	// for debug/display/exception purposes only
	protected String host;
	protected int port; 
	
	
	public AppInitIO(PrintWriter printWriter, BufferedReader bufferedReader, String host, int port)
	{
		super(printWriter, bufferedReader);
		this.command = "1032"; // RollNo - anything here is fine
		this.description = this.getClass().toString();
		
		this.host = host;
		this.port = port;
	}
	
	public boolean initHostInterface()
	{
		boolean isInitComplete = false;
		
		if (this.printWriter == null || this.bufferedReader == null) {
			return isInitComplete;
		}
		
		int inChar = 0;
		boolean isDoneInit = false; // check for correct initialization
		String line = "";
		
		try {
		
			// Loop-read response infinitely
			while (true /*&& count < 7*/) {
				if (!isDoneInit) {
					echo("Sending command: " + this.command + " " + this.description);
	        		printWriter.println(this.command);
				} else {
					isInitComplete = true;
					return isInitComplete;
				}
				        			
	    		while (!this.bufferedReader.ready()) {
	    			echo("Waiting for reponse ...");
	    			Thread.sleep(1000);
	    		}
	    		
	    		// Once ready, we are at start of a line of input
	    		if (bufferedReader.ready()) {
	    			line = ""; //clear line 
	    			
	    			while (bufferedReader.ready()) {
	    				inChar = bufferedReader.read();
	    				line += Character.toString((char) inChar);
	    			}
	    			
	    			// If response contains this needle, init is OK
					String initSuccessNeedle = "Initialization complete";
					
					if (line.toLowerCase().contains(initSuccessNeedle.toLowerCase())) {
						isDoneInit = true;
						echo("Init completed: " + line);
					} else {
						echo("Error: Initialization failed. Aborting ...");
						NdcScanRecorder.closeApplicationNicely();
					}
	    			
	    		} // end if (br.ready()) 
	        	
			} // end: while(true)
			
		} catch (UnknownHostException e) {
	        System.err.println("Unknown host: " + host);
	        System.exit(0);
	    } catch (IOException e) {
	        System.err.println("Couldn't get I/O for the connection to: " + host + ":" + port);
	        System.exit(0);
	    } catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		return isInitComplete;
	}
}












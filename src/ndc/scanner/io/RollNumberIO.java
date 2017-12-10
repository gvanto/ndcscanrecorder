package ndc.scanner.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import com.plaslantic.common.NdcScanRecorder;

/**
 * Gets current roll number (1032) 
 * Response:
 * 1032 = 2
 *
 */
public class RollNumberIO extends NdcDataIO
{
	// dev testing only: transition
	private int rollNo = 50;
	private int count = 0;
	private boolean isDev = false;
	
	
	public RollNumberIO(PrintWriter printWriter, BufferedReader bufferedReader)
	{
		super(printWriter, bufferedReader);
		this.command = "1032"; // RollNo
		this.description = this.getClass().toString();
	}
	
	/**
	 * 
	 * @return Current roll number (or -1 if not found)
	 */
	public int getRollNumber()
	{
		int rollNumber = -1;
		
		if (isDev) {
			if (this.count >= 2) {
				this.rollNo++;
				this.count = 0;
			}
			rollNumber = this.rollNo;
			this.count++;
		} else {
			String line = this.sendCommandAndWaitForLineResponse();
			line = this.cleanLine(line);
			try {
				rollNumber = Integer.parseInt(line);
			} catch (NumberFormatException e) {
				echo("WARNING: Failed to get roll number :-(");
			}
		}
		
		return rollNumber;
	}
	
	/**
	 * overrides because getting result twice!
	 * 1032 = 2\r\nOk 1032 = 2\r\nOk 
	 */
//	protected String cleanLine(String line, String firstCrap)
//	{
//		// 1032 = 2\r\nOk 1032 = 2\r\nOk 
//		String[] arr = line.split("\\r\\n");
//		
//		return super.cleanLine(arr[0], firstCrap);
//	}
	
}

package com.plaslantic.common;

/**
 * Simple structure representing a 24hr-format time HH:mm:ss (eg 17:34:15)
 * 
 * @author gert (gvanto@gmail.com)
 */
public class NdcTime {
	
	public int hour = -1, min = -1, sec = -1;
	
	public NdcTime(int hour, int min, int sec) 
	{
		this.hour = hour;
		this.min = min;
		this.sec = sec;
	}
	
	// Accepts 'HH:mm:ss' 
	public NdcTime(String timeStampStr)
	{
		String[] strArr = timeStampStr.split(":");
				
		if (strArr.length != 3) { // Should never happen
			NdcUtils.echo("NdcTime(): Error: strArr.length != 3 Error in start/end time format:" + timeStampStr);			
		} else {
			this.hour = Integer.parseInt(strArr[0]);
			this.min = Integer.parseInt(strArr[1]);
			this.sec = Integer.parseInt(strArr[2]);
		}		
	}
	
	public int toSeconds()
	{
		return (this.hour * 3600) + (this.min * 60) + this.sec;
	}
	
	public String toString()
	{
		return NdcUtils.prettifyNumber(this.hour) + ":" 
					+ NdcUtils.prettifyNumber(this.min) + ":" 
					+ NdcUtils.prettifyNumber(this.sec);
	}
	
	/**
	 * Checks if after - accounts for overnight time checking.
	 * 
	 *  NdcTime justBeforeLastWritten = new NdcTime(23, 59, 49);
        NdcTime lastWritten = new NdcTime(23, 59, 51);
        NdcTime justBeforeMidnight = new NdcTime(23, 59, 55);
        NdcTime justAfterMidnight = new NdcTime(0, 0, 53);
        if (justBeforeLastWritten.isAfter(lastWritten)) {
        	echo("is after");
        } else {
        	echo("not after");
        }
	 * 
	 * @param time
	 * @return True if 
	 */
	public boolean isAfter(NdcTime time)
	{		
		if (this.toSeconds() > time.toSeconds()) {	
			return true;	
		} else { 
			// we're dealing with an overnight e.g 23:50:00 -> 00:15:00
			if ((this.hour - time.hour) < -20) {
				return true;
			} else {
				return false;
			}	
		}
	}
	
	// to work with Hashtable, must implement hashCode and equals
	public int hashCode()
	{
		return this.toSeconds();
	}
	
	public boolean equals(Object obj)
	{
		NdcTime ot = (NdcTime) obj;
		return (this.hour == ot.hour && this.min == ot.min && this.sec == ot.sec);
	}
	
	public NdcTime clone() 
	{
		return new NdcTime(this.hour, this.min, this.sec);
	}
	
	public static int getDifferenceInSeconds(NdcTime startTime, NdcTime endTime)
	{
		// If end.hour >= start.hour, we can safely subtract start from end
		if (endTime.hour >= startTime.hour) {
			return (endTime.toSeconds() - startTime.toSeconds());
		}
		// it's gone overnight:
		//(eg 23:50:00 -> 00:20:00) OR (11:50:00 -> 00:20:00)
		// Do it in 2 periods:  + midnight -> end
		else {
			NdcTime midnight = new NdcTime((startTime.hour >= 13) ? 24 : 12, 0, 0);
			return 
					midnight.toSeconds() - startTime.toSeconds() // start -> midnight
				+   endTime.toSeconds(); 						 // midnight -> endTime
		}
	}	
}

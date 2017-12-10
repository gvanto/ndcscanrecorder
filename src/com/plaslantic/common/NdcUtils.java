package com.plaslantic.common;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class NdcUtils {
	
	private static Calendar calendar = null;
	private static String sqlDateTime = null;
	
	
	public static String getTodayYearMonthDay()
	{
		// Get today's date as string
		calendar = Calendar.getInstance();		
		int month = calendar.get(Calendar.MONTH) + 1;
		String str_month = prettifyNumber(month);
				
		String yearMonthDay = Integer.toString(calendar.get(Calendar.YEAR)) 
				+ "-" + str_month + "-" + prettifyNumber(calendar.get(Calendar.DAY_OF_MONTH));
		
		return yearMonthDay;
	}	
	
	/**
	 * Prefixes a single-digit number with "0". Returns number as a string
	 * 
	 * @param uglyNum 
	 * @return prettyNum 
	 */
	public static String prettifyNumber(int uglyNum)
	{
		String prettyNum = "";		
		prettyNum = (uglyNum < 10) ? "0" + Integer.toString(uglyNum) : Integer.toString(uglyNum);
		
		return prettyNum;
	}	
			
	public static int getSecondsFromTime(int[] timeArr)
	{
		int secs = 0;
		
		secs = (timeArr[0] * 60 * 60) // hours
				+ (timeArr[1] * 60)   // minutes
				+ timeArr[2];  		  // seconds
				
		return secs;
	}
	
	public static String getSqlDateTime()
	{
		calendar = Calendar.getInstance();
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}
	
	public static void echo(String s)
	{	
		System.out.println("[" + getSqlDateTime() + "] " + s);
	}
	
}

package com.plaslantic.common;

import java.io.*;


/**
 * This class handles all socket interaction to NDC 8000 scanner
 * See NDC 8000 scanner [8000 Open Access.pdf] documentation (page 53, 116) for more info.
 * 
 * @author Gert (gvanto@gmail.com)
 *
 */
public class NdcScanRecorder {
	private static NdcScanApp app;
	
	public static void main(String[] args)
	{
		startApp();
	}
	
	private static void startApp()
	{
		app = new NdcScanApp();
		try {
			app.run();
		} catch (InterruptedException e) {
			e.printStackTrace();
			closeApplicationNicelyAndRestart(5);
		}
	}
	
	public static void closeApplicationNicelyAndRestart(int waitSeconds)
	{
		echo ("closeApplicationNicelyAndRestart()");
		app.closeIoConnections();
		
		echo("Restarting in " + waitSeconds +  " seconds ... (= " + NdcTime.secondsToMinutes(waitSeconds) + " min)");
		try {
			Thread.sleep(waitSeconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		startApp();
	}
	
	public static void echo(String s)
	{
		NdcUtils.echo(s);
	}
	
}

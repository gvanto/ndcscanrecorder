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
		}
	}
	
	public static void closeApplicationNicely()
	{
		echo ("closeApplicationNicely: Closing ");
			
		try {
    		app.getSocket().close();
    		app.getPrintWriter().close();
    		app.getBufferedReader().close();		
		} catch (IOException e) {
	        System.err.println("Error (while attempting to close socket / printwriter / br): " + e.getMessage());	        
	    }
		
		echo("waiting ... todo: restart");	
	}
	
	public static void echo(String s)
	{
		NdcUtils.echo(s);
	}
	
}

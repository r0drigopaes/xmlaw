/******************************************************************
 * @(#) Trace.java	1.3
 * 
 * Copyright (c) 2005, John Miller
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   
 * 1. Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer. 
 * 2. Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials
 *    provided with the distribution. 
 * 3. Neither the name of the University of Georgia nor the names
 *    of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @version     1.3, 28 Jan 2005
 * @author      John Miller, Greg Silver
 */


package jsim.util;

import java.io.*;
import java.util.logging.*;


/*********************************************************************
 * A convenience class for controlling the display of logger messages,
 */

public class Trace implements Serializable
{
    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\

	/**
	 * Logging level
	 */
	private static Level  logLevel;

	/**
	 * Logging file handler
	 */
	private static FileHandler file;

	/**
	 * Logging console handler
	 */
	private static ConsoleHandler console;

	/**
	 * Log handler creation flag
	 */
	private static boolean handlersCreated = false;

	/**
	 * Unique identifier for serialized object
	 */
	private static final long serialVersionUID = 5437584723472341584L;

    /**************************************************************
     * Turn trace on/off.
     */
    public static void toggle ()
    {
        
		if ( console.getLevel () == Level.OFF ) 
		{
			logLevel = Level.INFO;
			System.out.println ("Log level is INFO");
		} 
		else 
		{
			logLevel = Level.OFF;
			System.out.println ("Log level is OFF");

		} // if

        console.setLevel ( logLevel );
		file.setLevel ( logLevel );

    }; // toggle

	/**************************************************************
	 * Set the log configuration for a logger.
	 * 
	 */	
	public static void setLogConfig ( Logger lg ) 
	{
		if (! handlersCreated ) 
		{
			createLogHandlers ();
		} // if

		lg.setUseParentHandlers ( false );
		boolean handlerFound = false;
		int i = 0;
		Handler [] handlers = lg.getHandlers ();

		while ( !handlerFound && i < handlers.length) 
		{
			if ( handlers [i] instanceof FileHandler) 
			{
				handlerFound = true;
			} // if
			i++;

		} // while

		if ( !handlerFound )  
		{
			lg.addHandler ( file );
			lg.addHandler ( console );

		} // if
		
	} // setLogConfig

	/**************************************************************
	 * Create the log handlers of the simulation and animation
	 */
	public static void createLogHandlers () 
	{
		try 
		{
			file = new FileHandler ( "trace.log" );
			console = new ConsoleHandler ();
			handlersCreated = true;

		} 
		catch ( IOException e ) 
		{
			System.out.println ( "Log Handler cannot open trace.xml." );

		} // try
		
	}

}; // class


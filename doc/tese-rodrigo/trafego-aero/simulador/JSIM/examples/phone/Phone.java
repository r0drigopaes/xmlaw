
/******************************************************************
 * Phone.java
 * JSIM Phone simulation model
 */

import java.awt.*;
import java.util.*;
import jsim.process.*;
import jsim.queue.*;
import jsim.util.*;
import jsim.variate.*;
import jsim.animator.*;
import jsim.coroutine.*;

public class Phone extends Model
{
	/**
	 * Declare nodes.
	 */
	Source 	Caller;
	Server 	Phone;
	Sink 	Hangup;

	/**
	 * Define default properties for each node.
	 */
	static Prop [] defaults = {
		new Prop (Node.SOURCE, "Caller", 50, new Point (74, 146), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.SERVER, "Phone", 1, new Point (195, 151), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.SINK, "Hangup", 0, new Point (330, 146), new Bernoulli (0.1, 0), new Uniform (0.0, 0.0, 0))};

	/**
	 * Define edges leaving each node.
	 */
	static Transport    CallerE0 = new Transport (114, 166, 195, 166, new Uniform (1000.0, 100.0, 0));
	static Transport [] CallerEs = {CallerE0};

	static Transport    PhoneE0 = new Transport (235, 166, 340, 166, new Uniform (1000.0, 100.0, 0));
	static Transport [] PhoneEs = {PhoneE0};

	static Transport [] trans = {CallerE0, PhoneE0};


	/**
	 * Define a shared data structure for communication between simulator and animator
	 */
	static AnimationQueue sharedQueue = new AnimationQueue ();


	/*************************************************************
	 * Construct Phone model.
	 */
	public Phone ()
	{
		super ("Phone", defaults, trans, sharedQueue);

		Caller = new Source (0, defaults [0], "Caller", CallerEs, sharedQueue);
		Phone = new Server (1, defaults [1], PhoneEs, sharedQueue);
		Hangup = new Sink (2, defaults [2], null, sharedQueue);
		initModel (new DynamicNode [] { Caller, Phone, Hangup });

	}; // Phone


	/*************************************************************
	 * Main method for running as application.
	 */
	public static void main (String [] args)
	{
		Phone model = new Phone ();
		model.start ();

	}; // main

}; // class


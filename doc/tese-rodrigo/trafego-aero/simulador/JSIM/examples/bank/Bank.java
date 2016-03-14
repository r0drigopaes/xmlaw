
/******************************************************************
 * Bank.java
 * JSIM Bank simulation model
 */

import java.awt.*;
import java.util.*;
import jsim.process.*;
import jsim.queue.*;
import jsim.util.*;
import jsim.variate.*;
import jsim.animator.*;
import jsim.coroutine.*;

public class Bank extends Model
{
	/**
	 * Declare nodes.
	 */
	Source 	Customer;
	Facility 	Teller;
	Sink 	Exit;

	/**
	 * Define default properties for each node.
	 */
	static Prop [] defaults = {
		new Prop (Node.SOURCE, "Customer", 30, new Point (53, 157), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.FACILITY, "Teller", 1, new Point (165, 162), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.SINK, "Exit", 0, new Point (365, 157), new Bernoulli (0.1, 0), new Uniform (0.0, 0.0, 0))};

	/**
	 * Define edges leaving each node.
	 */
	static Transport    CustomerE0 = new Transport (93, 177, 165, 177, new Uniform (1000.0, 100.0, 0));
	static Transport [] CustomerEs = {CustomerE0};

	static Transport    TellerE0 = new Transport (275, 177, 375, 177, new Uniform (1000.0, 100.0, 0));
	static Transport [] TellerEs = {TellerE0};

	static Transport [] trans = {CustomerE0, TellerE0};


	/**
	 * Define a shared data structure for communication between simulator and animator
	 */
	static AnimationQueue sharedQueue = new AnimationQueue ();


	/*************************************************************
	 * Construct Bank model.
	 */
	public Bank ()
	{
		super ("Bank", defaults, trans, sharedQueue);

		Customer = new Source (0, defaults [0], "Customer", CustomerEs, sharedQueue);
		Teller = new Facility (1, defaults [1], new LinkedList<Coroutine> (), TellerEs, sharedQueue);
		Exit = new Sink (2, defaults [2], null, sharedQueue);
		initModel (new DynamicNode [] { Customer, Teller, Exit });

	}; // Bank


	/*************************************************************
	 * Main method for running as application.
	 */
	public static void main (String [] args)
	{
		Bank model = new Bank ();
		model.start ();

	}; // main

}; // class


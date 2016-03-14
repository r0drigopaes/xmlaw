
/******************************************************************
 * FoodCourt.java
 * JSIM FoodCourt simulation model
 */

import java.awt.*;
import java.util.*;
import jsim.process.*;
import jsim.queue.*;
import jsim.util.*;
import jsim.variate.*;
import jsim.animator.*;
import jsim.coroutine.*;

public class FoodCourt extends Model
{
	/**
	 * Declare nodes.
	 */
	Source 	McDonalds;
	Facility 	MRegister1;
	Facility 	MRegister2;
	Sink 	MExit;
	Source 	Wendys;
	Facility 	WRegister;
	Sink 	WExit;

	/**
	 * Define default properties for each node.
	 */
	static Prop [] defaults = {
		new Prop (Node.SOURCE, "McDonalds", 50, new Point (73, 118), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.FACILITY, "MRegister1", 1, new Point (246, 64), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.FACILITY, "MRegister2", 1, new Point (246, 185), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.SINK, "MExit", 0, new Point (481, 117), new Bernoulli (0.1, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.SOURCE, "Wendys", 50, new Point (78, 336), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.FACILITY, "WRegister", 2, new Point (243, 341), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.SINK, "WExit", 0, new Point (478, 336), new Bernoulli (0.1, 0), new Uniform (0.0, 0.0, 0))};

	/**
	 * Define edges leaving each node.
	 */
	static Transport    McDonaldsE0 = new Transport (113, 138, 246, 200, new Uniform (1000.0, 100.0, 0));
	static Transport    McDonaldsE1 = new Transport (113, 138, 246, 79, new Uniform (1000.0, 100.0, 0));
	static Transport [] McDonaldsEs = {McDonaldsE0, McDonaldsE1};

	static Transport    MRegister1E0 = new Transport (356, 79, 491, 137, new Uniform (1000.0, 100.0, 0));
	static Transport [] MRegister1Es = {MRegister1E0};

	static Transport    MRegister2E0 = new Transport (356, 200, 491, 137, new Uniform (1000.0, 100.0, 0));
	static Transport [] MRegister2Es = {MRegister2E0};

	static Transport    WendysE0 = new Transport (118, 356, 243, 356, new Uniform (1000.0, 100.0, 0));
	static Transport [] WendysEs = {WendysE0};

	static Transport    WRegisterE0 = new Transport (353, 356, 488, 356, new Uniform (1000.0, 100.0, 0));
	static Transport [] WRegisterEs = {WRegisterE0};

	static Transport [] trans = {McDonaldsE0, McDonaldsE1, MRegister1E0, MRegister2E0, WendysE0, WRegisterE0};


	/**
	 * Define a shared data structure for communication between simulator and animator
	 */
	static AnimationQueue sharedQueue = new AnimationQueue ();


	/*************************************************************
	 * Construct FoodCourt model.
	 */
	public FoodCourt ()
	{
		super ("FoodCourt", defaults, trans, sharedQueue);

		McDonalds = new Source (0, defaults [0], "McDonalds", McDonaldsEs, sharedQueue);
		MRegister1 = new Facility (1, defaults [1], new LinkedList<Coroutine> (), MRegister1Es, sharedQueue);
		MRegister2 = new Facility (2, defaults [2], new LinkedList<Coroutine> (), MRegister2Es, sharedQueue);
		MExit = new Sink (3, defaults [3], null, sharedQueue);
		Wendys = new Source (4, defaults [4], "Wendys", WendysEs, sharedQueue);
		WRegister = new Facility (5, defaults [5], new LinkedList<Coroutine> (), WRegisterEs, sharedQueue);
		WExit = new Sink (6, defaults [6], null, sharedQueue);
		initModel (new DynamicNode [] { McDonalds, MRegister1, MRegister2, MExit, Wendys, WRegister, WExit });

	}; // FoodCourt


	/*************************************************************
	 * Main method for running as application.
	 */
	public static void main (String [] args)
	{
		FoodCourt model = new FoodCourt ();
		model.start ();

	}; // main

}; // class


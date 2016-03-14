
/******************************************************************
 * Reservations.java
 * JSIM Reservations simulation model
 */

import java.awt.*;
import java.util.*;
import jsim.process.*;
import jsim.queue.*;
import jsim.util.*;
import jsim.variate.*;
import jsim.animator.*;
import jsim.coroutine.*;

public class Reservations extends Model
{
	/**
	 * Declare nodes.
	 */
	Source 	Customer;
	Facility 	TravelAgent;
	Split 	Split0;
	Facility 	BookTicket;
	Facility 	BookRoom;
	AndJoin 	Join0;
	Facility 	SendConfirmation;
	Sink 	Exit;

	/**
	 * Define default properties for each node.
	 */
	static Prop [] defaults = {
		new Prop (Node.SOURCE, "Customer", 30, new Point (61, 195), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.FACILITY, "TravelAgent", 1, new Point (181, 203), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.SPLIT, "Split0", 0, new Point (369, 211), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.FACILITY, "BookTicket", 1, new Point (479, 147), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.FACILITY, "BookRoom", 1, new Point (482, 253), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.ANDJOIN, "Join0", 0, new Point (660, 209), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.FACILITY, "SendConfirmation", 1, new Point (722, 201), new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
		new Prop (Node.SINK, "Exit", 0, new Point (897, 198), new Bernoulli (0.1, 0), new Uniform (0.0, 0.0, 0))};

	/**
	 * Define edges leaving each node.
	 */
	static Transport    CustomerE0 = new Transport (101, 215, 181, 218, new Uniform (1000.0, 100.0, 0));
	static Transport [] CustomerEs = {CustomerE0};

	static Transport    TravelAgentE0 = new Transport (291, 218, 373, 218, new Uniform (1000.0, 100.0, 0));
	static Transport [] TravelAgentEs = {TravelAgentE0};

	static Transport    Split0E0 = new Transport (384, 218, 479, 162, new Uniform (1000.0, 100.0, 0));
	static Transport    Split0E1 = new Transport (386, 217, 482, 268, new Uniform (1000.0, 100.0, 0));
	static Transport [] Split0Es = {Split0E0, Split0E1};

	static Transport    BookTicketE0 = new Transport (589, 162, 663, 216, new Uniform (1000.0, 100.0, 0));
	static Transport [] BookTicketEs = {BookTicketE0};

	static Transport    BookRoomE0 = new Transport (592, 268, 670, 214, new Uniform (1000.0, 100.0, 0));
	static Transport [] BookRoomEs = {BookRoomE0};

	static Transport    Join0E0 = new Transport (678, 214, 722, 216, new Uniform (1000.0, 100.0, 0));
	static Transport [] Join0Es = {Join0E0};

	static Transport    SendConfirmationE0 = new Transport (832, 216, 907, 218, new Uniform (1000.0, 100.0, 0));
	static Transport [] SendConfirmationEs = {SendConfirmationE0};

	static Transport [] trans = {CustomerE0, TravelAgentE0, Split0E0, Split0E1, BookTicketE0, BookRoomE0, Join0E0, SendConfirmationE0, CustomerE0, TravelAgentE0, Split0E0, Split0E1, BookTicketE0, BookRoomE0, Join0E0, SendConfirmationE0, CustomerE0, TravelAgentE0, Split0E0, Split0E1, BookTicketE0, BookRoomE0, Join0E0, SendConfirmationE0};


	/**
	 * Define a shared data structure for communication between simulator and animator
	 */
	static AnimationQueue sharedQueue = new AnimationQueue ();


	/*************************************************************
	 * Construct Reservations model.
	 */
	public Reservations ()
	{
		super ("Reservations", defaults, trans, sharedQueue);

		Customer = new Source (0, defaults [0], "Customer", CustomerEs, sharedQueue);
		TravelAgent = new Facility (1, defaults [1], new LinkedList<Coroutine> (), TravelAgentEs, sharedQueue);
		Split0 = new Split (2, defaults [2], "Split0", Split0Es, sharedQueue);
		BookTicket = new Facility (3, defaults [3], new LinkedList<Coroutine> (), BookTicketEs, sharedQueue);
		BookRoom = new Facility (4, defaults [4], new LinkedList<Coroutine> (), BookRoomEs, sharedQueue);
		Join0 = new AndJoin (5, defaults [5], Join0Es, sharedQueue);
		SendConfirmation = new Facility (6, defaults [6], new LinkedList<Coroutine> (), SendConfirmationEs, sharedQueue);
		Exit = new Sink (7, defaults [7], null, sharedQueue);
		initModel (new DynamicNode [] { Customer, TravelAgent, Split0, BookTicket, BookRoom, Join0, SendConfirmation, Exit });

	}; // Reservations


	/*************************************************************
	 * Main method for running as application.
	 */
	public static void main (String [] args)
	{
		Reservations model = new Reservations ();
		model.start ();

	}; // main

}; // class


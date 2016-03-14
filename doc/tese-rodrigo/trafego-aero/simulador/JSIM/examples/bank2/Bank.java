
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
    /** Define nodes.
     */
    static Source    customer;
    static Facility  teller;
    static Sink      exit;

    /** Define default properties for each node.
     */
    static Prop [] defaults = {
        new Prop (Node.SOURCE, "customer", 30, new Point (53, 157),
                  new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
        new Prop (Node.FACILITY, "teller", 1, new Point (165, 162),
                  new Uniform (2000.0, 1000.0, 0), new Uniform (0.0, 0.0, 0)),
        new Prop (Node.SINK, "exit", 0, new Point (365, 157),
                  new Bernoulli (0.1, 0), new Uniform (0.0, 0.0, 0)) };

    /** Define edges leaving each node.
     */
    static Transport    customer_e0 = new Transport (93, 177, 165, 177, new Uniform (1000.0, 100.0, 0));
    static Transport [] customer_es = { customer_e0 };

    static Transport    teller_e0 = new Transport (275, 177, 375, 177, new Uniform (1000.0, 100.0, 0));
    static Transport [] teller_es = { teller_e0 };

    static Transport [] trans = { customer_e0, teller_e0 };

    /** Define shared queue for communication between simulator and animator.
     */
    static AnimationQueue sharedQueue = new AnimationQueue ();
    

    /*************************************************************
     * Nested class giving behavior of customer entity.
     */
    public static class Customer extends SimObject
    {

	public void run ()
	{
            try {

		for (customer_e0.join (this); customer_e0.move (this); );
		teller.request (this);
		teller.use (this);
		for (teller_e0.join (this); teller_e0.move (this); );
		exit.use (this);

            } catch (EntityException ee) { _end (); };

	} // run

    } // Customer nested class


    /*************************************************************
     * Construct Bank model.
     */
    public Bank ()
    {
        super ("Bank", defaults, trans, sharedQueue);
        customer = new Source (0, defaults [0], "Bank$Customer", customer_es, sharedQueue);
        teller = new Facility (1, defaults [1], new LinkedList<Coroutine> (), teller_es, sharedQueue);
        exit = new Sink (2, defaults [2], null, sharedQueue);
        initModel (new DynamicNode [] { customer, teller, exit });
    
    } // Bank


    /*************************************************************
     * Main method for running as application.
     */
    public static void main (String [] args)
    {
        Bank model = new Bank ();
        model.start ();
    
    } // main


} // Bank class


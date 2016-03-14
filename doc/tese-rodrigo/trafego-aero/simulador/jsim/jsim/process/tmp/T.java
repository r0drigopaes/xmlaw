import jsim.process.*;

class T {

    public static void main (String [] args)
    {
	try {
	    Class entityType = Class.forName ("Customer");
	    SimObject entity = (SimObject) entityType.newInstance ();
	    entity.init (null, 0, null);
	    System.out.println ("start entity success!");
	} catch (Exception e) {
	    e.printStackTrace ();
	}
    }; // main
}; // test class

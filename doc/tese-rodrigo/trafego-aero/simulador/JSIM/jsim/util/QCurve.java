/******************************************************************
 * @(#) QCurve.java     1.3     97/12/14
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
 * @author      John Miller
 */

package jsim.util;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.logging.Logger;


/******************************************************************
 * The QCurve class enhances the QuadCurve.Double class (from the
 * java.awt.geom package) by allowing entities to move along such
 * quadratic curves.
 */

public class QCurve extends     QuadCurve2D.Double
                    implements  Serializable
{

    /*************************************************************
     * Inner class to hold coefficients of quadratic equation.
     */
    private class Coefficients 
					implements  Serializable
    {
        private double a;
        private double b;
        private double c;
        private double alpha;
        private double cosa;
        private double sina;

        /*********************************************************
         * Contructs zeored coordinates.
         */
        Coefficients ()
        {
            a = b = c = 0.0;

        }; // Coefficients

        /*********************************************************
         * Contructs coordinates from parameters a, b and c for a
         * quad curve.
         */
        Coefficients (double a, double b, double c)
        {
            this.a = a;
            this.b = b;
            this.c = c;

        }; // Coefficients

        /*********************************************************
         * Contructs coordinates from the start and end points for
         * a straight line (degenerate quad curve).
         */
        Coefficients (double x1, double y1,       // start point
                      double x2, double y2)       // end point
        {
             double m = (y2 - y1) / (x2 - x1);    // slope of line

             alpha = 0.0;
             a =  0.0;
             b =  m;
             c =  y1 - b * x1;
 
        }; // Coefficients

        /*********************************************************
         * Contructs coordinates from the start, control and end
         * points for a quad curve.
         */
        Coefficients (double x1,  double y1,       // start point
                      double xc1, double yc1,      // control point
                      double x2,  double y2)       // end point
        {
            double  xm, ym, xc, yc, k, kc;
            double  X1, Y1, X2, Y2, Xc, Yc, Xc1, Yc1, Xm, Ym;
            double  Xt1, Yt1, Xt2, Yt2, Xtc, Ytc, Xtc1, Ytc1;
            double  Xa, Ya, xa,ya;
            double  m1, m2;

            alpha = cosa = sina = 0.0;

            xm = (x1 + x2) / 2;   ym = (y1 + y2) / 2;
            xc = (xm + xc1) / 2;  yc = (ym + yc1) / 2;

            //if ( (xm - xc) == 0 ) {
            if ( (xm - xc) <= 0.5 ) {         // FIX - jam 1/31/99

                m1 = (yc - y1) / (xc - x1);   // slope of line 1
                m2 = (y2 - yc) / (x2 - xc);   // slope of line 2
   
                a = (m2 - m1) / (1 * (x2 - x1)); 
                b =  m1 -  a * (x1 + xc);
                c =  y1 - (a * x1 + b) * x1;
          
            } else {

                kc    = (ym - yc) / (xm - xc);
                alpha = Math.atan (1.0 / kc);
                //alpha = Math.atan2 (xm - xc, ym - yc);
                cosa = Math.cos (alpha);
                sina = Math.sin (alpha);        

 
                X1 = xtoX (x1, y1);           // x1*sina + y1*cosa;
                Y1 = ytoY (x1, y1);           // y1*sina - x1*cosa;
                X2 = xtoX (x2, y2);           // x2*sina + y2cosa;
                Y2 = ytoY (x2, y2);           // y2*sina - x2*cosa;
                Xc = xtoX (xc, yc);           // xc*sina + yc*cosa;
                Yc = ytoY (xc, yc);           // yc*sina - xc*cosa;
                Xm = xtoX (xm, ym);    
                Ym = ytoY (xm, ym);

                Xc1 = xtoX (xc1, yc1);        // xc1*sina + yc1*cosa;
                Yc1 = ytoY (xc1, yc1);        // yc1*sina - xc1*cosa;

                m1 = (Yc - Y1) / (Xc - X1);   // slope of line 1
                m2 = (Y2 - Yc) / (X2 - Xc);   // slope of line 2
 
                a = (m2 - m1) / (1 * (X2 - X1));
                b =  m1 -  a * (X1 + Xc);
                c =  Y1 - (a * X1 + b) * X1;

                Xt1 = Xtox (X1, Y1);          // X1*sina - Y1*cosa;
                Yt1 = Ytoy (X1, Y1);          // Y1*sina + X1*cosa;
                Xt2 = Xtox (X2, Y2);          // X2*sina - Y2*cosa;
                Yt2 = Ytoy (X2, Y2);          // Y2*sina + X2*cosa;
                Xtc = Xtox (Xc, Yc);          // Xc*sina - Yc*cosa;
                Ytc = Ytoy (Xc, Yc);          // Yc*sina + Xc*cosa;

                Xa = X1;
                Ya = Xa * (Xa * a + b) + c;
                xa = Xtox (Xa, Ya);           // Xa*sina - Ya*cosa;
                ya = Ytoy (Xa, Ya);           // Ya*sina + Xa*cosa;
         
                Xa = -b / (2 * a);
                Ya = Xa * (Xa * a + b) + c;
                xa = Xtox (Xa, Ya);           // Xa*sina - Ya*cosa;
                ya = Ytoy (Xa, Ya);           // Ya*sina + Xa*cosa;
                Xa = -b / (2 * a) + 20;
                Ya = Xa * (Xa * a + b) + c;
                xa = Xtox (Xa, Ya);           // Xa*sina - Ya*cosa;
                ya = Ytoy (Xa, Ya);           // Ya*sina + Xa*cosa;
                Xa = X2;
                Ya = Xa * (Xa * a + b) + c;
                xa = Xtox (Xa, Ya);           // Xa*sina - Ya*cosa;
                ya = Ytoy (Xa, Ya);           // Ya*sina + Xa*cosa;

            }; // if

        }; // Coefficients

        public Point2D.Double eval (Point2D.Double p, double step)
        {
            double    x, y; 
            double    X, Y;

            if (alpha == 0.0) {
                x = p.x + step;
                y = ((a * x + b) * x + c );    
            } else {
                X = xtoX (p.x, p.y) + step;
                Y = (a * X + b) * X + c;
                x = Xtox (X, Y);
                y = Ytoy (X, Y);
            }; // if    

            return new Point2D.Double (x, y);

        }; // eval

        private double xtoX (double x, double y)
        {
            return x * cosa - y * sina;

        }; // xtoX

        private double ytoY (double x, double y)
        {
            return y * cosa + x * sina;

        }; // ytoY

        private double Xtox (double X, double Y)
        {
            return X * cosa + Y * sina;

        }; // Xtox

        private double Ytoy (double X, double Y)
        {
            return -X * sina + Y * cosa; 

        }; // Ytoy

    }; // inner class


    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Flag indicating whether curve is actaully a straight line.
     */
    public  final boolean       straight;
 
    /**
     * Values of coefficients of quadratic equation.
     */
    private Coefficients  val;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Revised coordinates of start point.
     */
    private double  rX1, rY1;

    /**
     * Revised coordinates of end point.
     */
    private double  rX2, rY2;

	/**
	 * Probability distribution
	 */
	public  String     distribution;

	/**
	 * Scale parameter (e.g., mean)
	 */
	public  String     alpha;

	/**
	 * Shape parameter (e.g., variance)
	 */
	public  String     beta;

	/**
	 * Random number stream
	 */
	public  String     stream;

	/**
	 * Position of QCurve in outedge array
	 */
	private int edgeIndex;

	/**
	 * Node in which the QCurve is an outedge
	 */
	private Node startNode;

    /**
	 * Type of output condition
	 */
	private int outCondType;

	/////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
	/**
	 * Tracing Messages
	 */
	protected static Logger trc = Logger.getLogger(Node.class.getName() );

	/**
	 * Unique identifier of serialized class
	 */
	private static final long serialVersionUID = 4376890213965839091L;

    /*************************************************************
     * Construct an empty quad curve.
     */
    public QCurve ()
    {
        super (0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

        straight = false;

        val = new Coefficients (); 
        rX1 = rY1 = rX2 = rY2 = 0.0;

		/******************************************************************
		 * Assign default values to adjustable parameters.
		 */
		distribution = "Uniform";
		alpha = "1000.0";
		beta = "100.0";
		stream = "0";
		Trace.setLogConfig ( trc );

    }; // QCurve


    /*************************************************************
     * Construct a straight line (degenerate quad curve).
     * @param  x1     x coordinate of start point
     * @param  y1     y coordinate of start point
     * @param  x2     x coordinate of end point
     * @param  y2     y coordinate of end point
     */
    public QCurve (double x1, double y1,         // start point
                   double x2, double y2)         // end point
    {
        super (x1, y1, (x1 + x2) / 2.0, (y1 + y2) / 2.0, x2, y2);

        straight = true;

        val = new Coefficients (x1, y1, x2, y2);

        if (x2 >= x1) { rX1 = x1;  rX2 = x2; }
        else          { rX1 = x2;  rX2 = x1; };

        if (y2 >= y1) { rY1 = y1;  rY2 = y2; }
        else          { rY1 = y2;  rY2 = y1; };

        rX1 -= 10.0;  rY1 -= 10.0;
        rX2 += 10.0;  rY2 += 10.0;

		/******************************************************************
		 * Assign default values to adjustable parameters.
		 */
		distribution = "Uniform";
		alpha = "1000.0";
		beta = "100.0";
		stream = "0";
		Trace.setLogConfig ( trc );
 
    }; // QCurve


    /*************************************************************
     * Construct a quad curve.
     * @param  x1     x coordinate of start point
     * @param  y1     y coordinate of start point
     * @param  ctrlx  x coordinate of control point
     * @param  ctrly  y coordinate of control point
     * @param  x2     x coordinate of end point
     * @param  y2     y coordinate of end point
     */
    public QCurve (double x1,    double y1,         // start point
                   double ctrlx, double ctrly,      // control point
                   double x2,    double y2)         // end point
    {
        super (x1, y1, ctrlx, ctrly, x2, y2);

        straight = false; 

        val = new Coefficients (x1, y1, ctrlx, ctrly, x2, y2);

        if (x2 >= x1) { rX1 = x1;  rX2 = x2; }
        else          { rX1 = x2;  rX2 = x1; };

        if (y2 >= y1) { rY1 = y1;  rY2 = y2; }
        else          { rY1 = y2;  rY2 = y1; };

        if      (ctrlx < rX1) { rX1 = ctrlx; }
        else if (ctrlx > rX2) { rX2 = ctrlx; };

        if      (ctrly < rY1) { rY1 = ctrly; }
        else if (ctrly > rY2) { rX2 = ctrly; };

        rX1 -= 20.0;  rY1 -= 20.0;
        rX2 += 20.0;  rY2 += 20.0;

        Point2D.Double tmp = new Point2D.Double (90, 70);   // FIX

        for( int i = 0; i < 15; i++) {
            tmp = val.eval (tmp, 10.0);
            //System.out.println ("[" + i +"] " + tmp.x + ", "+ tmp.y);
        }; // for

		/******************************************************************
		 * Assign default values to adjustable parameters.
		 */
		distribution = "Uniform";
		alpha = "1000.0";
		beta = "100.0";
		stream = "0";
		Trace.setLogConfig ( trc );

    }; // QCurve

	/*************************************************************
	 * ReConstruct a quad curve.
	 * @param  x1     x coordinate of start point
	 * @param  y1     y coordinate of start point
	 * @param  ctrlx  x coordinate of control point
	 * @param  ctrly  y coordinate of control point
	 * @param  x2     x coordinate of end point
	 * @param  y2     y coordinate of end point
	 */
	public void ReconstructQCurve (double x1,    double y1,         // start point
							       double ctrlx, double ctrly,      // control point
							       double x2,    double y2)         // end point
	{
		this.x1 = x1;
		this.y1 = y1;
		this.ctrlx = ctrlx;
		this.ctrly = ctrly;
		this.x2 = x2;
		this.y2 = y2;

		val = new Coefficients (x1, y1, ctrlx, ctrly, x2, y2);

		if (x2 >= x1) { rX1 = x1;  rX2 = x2; }
		else          { rX1 = x2;  rX2 = x1; };

		if (y2 >= y1) { rY1 = y1;  rY2 = y2; }
		else          { rY1 = y2;  rY2 = y1; };

		if      (ctrlx < rX1) { rX1 = ctrlx; }
		else if (ctrlx > rX2) { rX2 = ctrlx; };

		if      (ctrly < rY1) { rY1 = ctrly; }
		else if (ctrly > rY2) { rX2 = ctrly; };

		rX1 -= 20.0;  rY1 -= 20.0;
		rX2 += 20.0;  rY2 += 20.0;

		Point2D.Double tmp = new Point2D.Double (90, 70);   // FIX

		for( int i = 0; i < 15; i++) 
		{
			tmp = val.eval (tmp, 10.0);
			//System.out.println ("[" + i +"] " + tmp.x + ", "+ tmp.y);
		}; // for

	}; // QCurve

    /*************************************************************
     * Return the first/start point of the quad curve.
     * @return  Point2D.Double  the first point
     */
    public Point2D.Double getFirst ()
    {
        return new Point2D.Double (x1, y1);

    }; // first


    /*************************************************************
     * Return the control point of the quad curve.
     * @return  Point2D.Double  the control point
     */
    public Point2D.Double getControl ()
    {
        return new Point2D.Double (ctrlx, ctrly);
 
    }; // getControl


    /*************************************************************
     * Return the last/end point of the quad curve.
     * @return  Point2D.Double  the last point
     */  
    public Point2D.Double getLast ()
    {
        return new Point2D.Double (x2, y2);

    }; // last


    /*************************************************************
     * Reset first point in curve.
     * @param  delta  translation vector
     */
    public void setFirst (Point2D.Float delta) {

      float dx = (float) delta.getX ();
      float dy = (float) delta.getY ();

      x1 += dx;
      y1 += dy;

    }; // setFirst

    /*************************************************************
     * Reset first point in curve to a fixed positon
     * @param  newPoint  point
     */
    public void setFirstFixed (Point2D.Float newPoint) {

      x1 = (float) newPoint.getX ();
      y1 = (float) newPoint.getY ();

    }; // setFirstFixed

    /*************************************************************
     * Reset last point in curve.
     * @param  delta  translation vector
     */
    public void setLast (Point2D.Float delta) {

      float dx = (float) delta.getX ();
      float dy = (float) delta.getY ();
       
      x2 += dx;
      y2 += dy;

    }; // setLast

    /*************************************************************
     * Reset last point in curve to a fixed position
     * @param  newPoint  point
     */
    public void setLastFixed (Point2D.Float newPoint) {

      x2 = (float) newPoint.getX ();
      y2 = (float) newPoint.getY ();

    }; // setLastFixed

    /*************************************************************
     * Are (x, y) and (xe, ye) essentially the same?
     * @return  boolean  true if essentially the same point
     */
    public boolean isSame (double x, double y, double xe, double ye, double step)
    {
        double d = (xe - x) * (xe -x) + (ye - y) * (ye -y);
        return d < step * step; 

    }; // isSame


    /*************************************************************
     * Return the next point on the quad curve (step units beyond
     * current).  Return null if past end point.
     * @param   current         the current point
     * @param   step            distance to next point
     * @return  Point2D.Double  the next point
     */
    public Point2D.Double next (Point2D.Double current, double step)
    {

        if ( ! isSame (current.x, current.y, x2, y2, step)) {    

            Point2D.Double tmp = val.eval (current, step);

            if (! isSame (tmp.x, tmp.y, x2, y2, step)) {
                return tmp;
            }; // if

        }; // if
		trc.info ("curr x: " + current.x + " curr y: " + current.y + " x2: " + x2 + " y2: " + y2);
        return null;

    }; // next

	public boolean onQCurve (Point2D.Double selected)
	{
		Point2D.Double P1 = new Point2D.Double(x1, y1);
		
		while (P1 != null) //(P1.getX () <= x2 && P1.getY () <= y2)
		{
			trc.info ((int) P1.getX () + ", " + (int) P1.getY () + ", " + (int) selected.getX () + ", " + (int) selected.getY ());
			
			if (((int) selected.getX () == (int) P1.getX () && (int) selected.getY () == (int) P1.getY ())
   			   || ((int) selected.getX () == (int) P1.getX () && (int) selected.getY () == (int) (P1.getY () + 1))
			   || ((int) selected.getX () == (int) P1.getX () && (int) selected.getY () == (int) (P1.getY () - 1)))
			{
				return true;
			} // if

			P1 = next (P1, 1.0);

		} // while

		return false;

	} // onQCurve

	public int getEdgeIndex () 
	{
		return edgeIndex;

	} // getEdgeIndex

	public void setEdgeIndex (int edgeIndex)
	{
		this.edgeIndex = edgeIndex;

	} // setEdgeIndex

	public Node getStartNode ()
	{
		return startNode;

	} // getStartNode

	public void setStartNode (Node startNode) 
	{
		this.startNode = startNode;

	} // setStartNode

	public void setOutCondition (String outCondition)
	{
		startNode.outCondition [edgeIndex] = outCondition;

	} // setOutCondition

	public String getOutCondition () 
	{
		return startNode.outCondition [edgeIndex];

	} // getOutCondition

	public void setOutCondType (int outCondType) 
	{
		this.outCondType = outCondType;

	} // setOutCondType

	public int getOutCondType ()
	{
		return outCondType;

	} // getOutCondType

}; // class


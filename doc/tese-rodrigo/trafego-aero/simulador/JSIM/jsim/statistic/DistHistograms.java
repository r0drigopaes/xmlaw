 /******************************************************************
 * @(#) DistHistograms.java     1.3     98/2/7
 *
 * Copyright (c) 2005, John Miller, Yongfu Ge
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
 * @author      John Miller, Yongfu Ge
 */


package jsim.statistic;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import jsim.variate.*;


/******************************************************************
 * This class implements a control frame to test both the
 * Histogram class and the distributions in the variate package.
 */

public class DistHistograms extends    Frame
                            implements ItemListener,
                                       ActionListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Foreground color
     */ 
    private final static Color  F_BG_COLOR = new Color (200, 200, 200);
  
    /**
     * The number which represents bernoulli distribution, etc.
     * Discrete distributions are followed with '//d'
     */
    private static final int BERNOULLI        = 1;      //d
    private static final int BETA             = 2;
    private static final int BINOMIAL         = 3;      //d
    private static final int CAUCHY           = 4;
    private static final int CHISQUARE        = 5;
    private static final int DISCRETEPROB     = 6;      //d
    private static final int ERLANG           = 7;
    private static final int EXPONENTIAL      = 8;
    private static final int F_DISTRIBUTION   = 9;
    private static final int GAMMA            = 10;
    private static final int GEOMETRIC        = 11;     //d
    private static final int HYPEREXPONENTIAL = 12; 
    private static final int HYPERGEOMETRIC   = 13;     //d
    private static final int LOGNORMAL        = 14;
    private static final int NEGATIVEBINORMAL = 15;     //d
    private static final int NORMAL           = 16;
    private static final int POISSION         = 17;     //d
    private static final int RANDI            = 18;     //d
    private static final int STUDENTT         = 19;
    private static final int TRIANGLAR        = 20;
    private static final int UNIFORM          = 21;
    private static final int VARIATE          = 22;
    private static final int WEIBULL          = 23;
    private static final int DEFAULT          = 24;

    //////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Text field for interval 
     */
    private final TextField  intervalTF;    

    /**
     * Text field for number
     */
    private final TextField  numberTF; 

    /**
     * Choice for generating type
     */
    private final Choice     generatorType;

    /**
     * Button for generating
     */
    private final Button     toGenBtn;

    /**
     * Button for cancel
     */
    private final Button     cancelBtn;

    ///////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Instance of Histogram
     */
    private Histogram  his;

    /**
     * Array to store the data generated by the distribution
     */
    private double []  valDbl;

    /**
     * Size of valDbl array 
     */
    private int        number    = 1000;

    /**
     * Interval value
     */
    private double     interval  = 0.05;

    /**
     * Integer variable for type of distribution
     */
    private int        genIndex;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 2939487162543756451L;

    /**************************************************************
     * Construct a histogram control frame. 
     */
    public DistHistograms ()
    {
       this ("Distribution Histograms");

    }; // DistHistograms
    
   
    /**************************************************************
     * Construct a histogram control frame with a title passed in.
     * @param  title   the title of the distribution histogram
     */
    public DistHistograms (String title)
    {
        super (title);
         
        setBackground (F_BG_COLOR);
       
        /**********************************************************
         * Create componemts and add them to the frame 
         */
        Label intervalL = new Label ("Interval: ", Label.RIGHT); 
        Label numberL   = new Label ("Numbers to generate: ", Label.RIGHT); 
        Label genTypeL  = new Label ("Generator type: ", Label.RIGHT); 

        intervalTF    = new TextField ("0.05", 10);    
        numberTF      = new TextField ("1000", 10);    
        generatorType = new Choice ();
        toGenBtn      = new Button ("Show histogram");
        cancelBtn     = new Button ("Quit");

        generatorType.addItem ("Bernoulli");         //1
        generatorType.addItem ("Beta");              //2
        generatorType.addItem ("Binomial");          //3
        generatorType.addItem ("Cauchy");            //4
        generatorType.addItem ("ChiSquare");         //5
        generatorType.addItem ("DiscreteProb");      //6
        generatorType.addItem ("Erlang");            //7
        generatorType.addItem ("Exponential");       //8
        generatorType.addItem ("F_Distribution");    //9
        generatorType.addItem ("Gamma");             //10
        generatorType.addItem ("Geometric");         //11
        generatorType.addItem ("HyperExponential");  //12
        generatorType.addItem ("HyperGeometric");    //13
        //generatorType.addItem ("LCGRandom"); 
        generatorType.addItem ("LogNormal");         //14
        generatorType.addItem ("NegativeBinomial");  //15
        generatorType.addItem ("Normal");            //16
        generatorType.addItem ("Poisson");           //17
        generatorType.addItem ("Randi");             //18
        generatorType.addItem ("StudentT");          //19
        generatorType.addItem ("Triangular");        //20
        generatorType.addItem ("Uniform");           //21
        generatorType.addItem ("Variate");           //22
        generatorType.addItem ("Weibull");           //23
        generatorType.addItem ("Default");           //24

        setLayout (new GridLayout (4, 2));
      
        toGenBtn.addActionListener (this);
        cancelBtn.addActionListener (this);
      
        numberTF.setBackground (Color.pink);
        intervalTF.setBackground (Color.pink);
        generatorType.setBackground (Color.pink);

        add (numberL);
        add (numberTF);      
        add (intervalL);
        add (intervalTF);
        add (genTypeL);
        add (generatorType);
        add (toGenBtn);
        add (cancelBtn);

        setSize (getSize ().width, getSize ().height + (getSize ().height) / 2);
        pack ();
        //setSize (300, 300);       
        setLocation (190, 0);
        // show (); the show method was deprecated as of jdk 1.5
		setVisible(true);

    }; // DistHistograms
    
   

    /**************************************************************
     * Fill an array of size = number with the random numbers 
     * generated from a specified generator. 
     * @param gIndex  the number represents the distribution
     */
    public void fillArray (int gIndex)
    {
        Random ran   = new Random ();
        double alpha = ran.nextDouble ();
        double beta  = ran.nextDouble ();

        while (alpha <= 0.0 )  alpha = ran.nextDouble ();
        while (beta <= 0.0 )   beta  = ran.nextDouble ();

        valDbl = new double [number];
        int stream = number % LCGRandom.MAX_STREAMS; 
        Variate dist = new Variate (stream);

        //gIndex is Choice index plus 1, since index starts from 0.

        switch (gIndex) {

        case BERNOULLI:
            dist = new Bernoulli (alpha, stream); 
            break;

        case BETA:
            dist = new Beta (alpha, beta, stream); 
            break;

        case BINOMIAL:
            intervalTF.setText ("0.5");  // set interval to be 1.
            getData ();
            while (alpha < 10.0)  alpha *= 100;
            dist = new Binomial ((int) alpha, beta, stream);             
            break;

        case CAUCHY:
            dist = new Cauchy (alpha, beta, stream);             
            break;

        case CHISQUARE:
            while (alpha < 10)  alpha *= 10;
            dist = new ChiSquare ((int) alpha, stream);             
            break;
        
        case DISCRETEPROB:
            double [] cumProb = new double [number];
            double [] value   = new double [number];
            for (int i = 0; i < number; i++) {
                cumProb[i] = ran.nextDouble ();
                value[i]   = ran.nextDouble ();
            }; // for      
            dist = new DiscreteProb (cumProb, value, stream);
            break;

        case ERLANG:
            while (beta < 10.0)  beta *= 50.0;
            beta = beta % 50 + 1;
            dist = new Erlang (alpha, (int) beta, stream);
            break;
        
        case EXPONENTIAL:
            dist = new Exponential (alpha, stream);
            break;
        
        case F_DISTRIBUTION:
            while ( alpha < 1.0)  alpha *= 100;
            while ( beta < 1.0)   beta  *= 100;
            alpha = alpha % 99 + 1;
            beta  = beta % 99 + 1;
            dist = new F_Distribution ((int) alpha, (int) beta, stream);
            break;

        case GAMMA:
            dist = new Gamma (alpha, beta, stream); 
            break;

        case GEOMETRIC:
            intervalTF.setText ("0.5");  // set interval to be 1.
            getData ();
            dist = new Geometric (alpha, stream); 
            break;
        
        case HYPEREXPONENTIAL:
            dist = new HyperExponential (alpha, beta, stream);             
            break;
  
        case HYPERGEOMETRIC:
            intervalTF.setText ("0.5");  // set interval to be 1.
            getData ();
            while (alpha < 10.0)  alpha *= 100.0;
            while (beta < 10.0)   beta  *= 100.0;
            double p = ran.nextDouble ();
            dist = (alpha > beta) ?
                   new HyperGeometric ((int) alpha, (int) beta,  p, stream) :
                   new HyperGeometric ((int) beta,  (int) alpha, p, stream); 
            break;

        case LOGNORMAL:
            dist = new LogNormal (alpha, beta, stream); 
            break;

        case NEGATIVEBINORMAL:
            intervalTF.setText ("1.0");  // set interval to be 1.
            getData ();
            while (beta < 1.0 )  beta *= 1000.0;
            dist = new NegativeBinomial (alpha, (int) beta, stream);
            break;

        case NORMAL:
            dist = new Normal (alpha, beta, stream);             
            break;

        case POISSION:
            dist = new Poisson (alpha, stream); 
            break;
 
        case RANDI:
            intervalTF.setText ("1.0");  // set interval to be 1.
            getData ();
            while (alpha < 1.0)  alpha *= 1000.0;
            while (beta < 1.0)   beta *= 1000.0;
            Randi br;
            dist = (alpha < beta) ? 
                   new Randi ((int) alpha, (int) beta, stream) :
                   new Randi ((int) beta,  (int) alpha, stream);
            break;

        case STUDENTT:
            while (alpha < 1)  alpha *= 100.0;
            dist = new StudentT ((int) alpha, stream); 
            break;
 
        case TRIANGLAR:
            if (alpha < beta) {
                dist = new Triangular (alpha, beta, stream); 
            } else if ( alpha > beta) {
                dist = new Triangular (beta, alpha, stream); 
            } else {  // equal
                dist = new Triangular (alpha, beta * 2, stream); 
            }; // if
            break;
      
        case UNIFORM:
            dist = new Uniform (alpha, beta, stream); 
            break;
 
        case VARIATE:
            break;

        case WEIBULL:
            dist = new Weibull (alpha, beta, stream);
            break;

        default:
            System.out.println ("Error: Unknown Distribution");

        }; // switch

        for (int i = 0; i < number; i++) {
            valDbl [i] = dist.gen ();
        }; // for

    }; // fillArray


    /************************************************************
     * Get the title for Histogram according to the distribution chosen.
     * @param distIndex  the number represents the distribution
     * @return  String  the title of the distribution
     */  
    private String getTitle (int distIndex)
    {
        switch (distIndex) {
         
        case BERNOULLI:        return "Histogram - Bernoulli";
        case BETA:             return "Histogram - Beta";    
        case BINOMIAL:         return "Histogram - Binomial";
        case CAUCHY:           return "Histogram - Cauchy"; 
        case CHISQUARE:        return "Histogram - Chi-Square";
        case DISCRETEPROB:     return "Histogram - Discrete-Prob";
        case ERLANG:           return "Histogram - Erlang";    
        case EXPONENTIAL:      return "Histogram - Exponential"; 
        case F_DISTRIBUTION:   return "Histogram - F-Distribution";
        case GAMMA:            return "Histogram - Gamma";     
        case GEOMETRIC:        return "Histogram - Geometric";
        case HYPEREXPONENTIAL: return "Histogram - Hyper-Exponential";
        case HYPERGEOMETRIC:   return "Histogram - Hyper-Geometric";
        case LOGNORMAL:        return "Histogram - Log-Normal";
        case NEGATIVEBINORMAL: return "Histogram - Negative-Binomial";
        case NORMAL:           return "Histogram - Normal";    
        case POISSION:         return "Histogram - Poission";
        case RANDI:            return "Histogram - Randi";  
        case STUDENTT:         return "Histogram - Student-T";
        case TRIANGLAR:        return "Histogram - Triangular";
        case UNIFORM:          return "Histogram - Uniform";  
        case VARIATE:          return "Histogram - Variate";
        case WEIBULL:          return "Histogram - Weibull";
        default:               return "Histogram - Unknown";
 
        } // switch
    
    };  // getTitle

   
    /**************************************************************
     * Display the histogram.
     */
    public void showHisto ()
    {
        getData ();
            
        // choice index begins with 0
        fillArray (genIndex + 1);
        his = new Histogram (valDbl, interval, getTitle (genIndex + 1));

    }; // showHisto
  
  
    /**************************************************************
     * Collect the data from the editor boxes and the choice.
     */
    public synchronized void getData ()
    {
        String gType = new String (""); 
        String gNum  = new String (""); 
        String gIntv = new String (""); 

        gNum     = numberTF.getText ();
        gType    = generatorType.getSelectedItem ();
        genIndex = generatorType.getSelectedIndex ();
        gIntv    = intervalTF.getText ();
        if (gIntv != null) {
            interval = (Double.valueOf (gIntv)).doubleValue (); 
        }; // if
        if (gNum != null) {
            number =  (Integer.valueOf (gNum)).intValue ();
        }; // if

    }; // getData


    /**************************************************************
     * Start.
     */
    public void start ()
    {
        his.setEnabled (true);
        his.setVisible (true);  // super.start ()

    }; // start

 
    /**************************************************************
     * Stop.
     */
    public void stop ()
    {
        his.setEnabled (false);
        his.setVisible (false);
 
    }; // stop


    /**************************************************************
     * Perform an action when a button clicked. 
     * @param  e   the input action event
     */
    public void actionPerformed (ActionEvent e)
    {
        // Display histogram using selected input parameters.
        if (e.getActionCommand ().equals ("Show histogram")) {
            showHisto ();
        } else {  // Just shutdown...
            dispose ();
            invalidate ();
        }; // if
    
    }; // actionPerformed


    /**************************************************************
     * Perform an action when a choice is made.
     * @param  e   the input item event
     */
    public void itemStateChanged (ItemEvent e)
    {
        getData ();

    }; // itemStateChanged


}; // DistHistograms


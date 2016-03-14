/******************************************************************
 * @(#) VariateConverter.java     1.3
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

package jsim.process;

import java.lang.reflect.*;
import java.util.*;
import jsim.variate.*;


/******************************************************************
 * An auxiliary class supporting the conversion from variate to 
 * String and vice versa.
 */

class VariateConverter 
{

    /****************************************************************
     * Get the name of the variate.
     * @return  String name of varaite
     */
    public static String toString (Variate variate)
    {
        Class<?>  c    = variate.getClass ();
        String    name = c.getName ();
        return name.substring (name.lastIndexOf ('.') + 1);

    }; // toString


    /****************************************************************
     * Method to construct a variate.
     * @param   name     name of variate
     * @param   params   parameters (alpha, ...)
     * @param   stream   random number stream
     * @return  Variate  newly created random variate
     */
    public static Variate toVariate (String   distType,
                                     Integer  stream,
                                     Double   alpha,
                                     Double   beta,
                                     Double   gamma)
    {
        Variate         variate           = null;
        Class<?>        variateDefinition = null;
        Class<?> []     argsClass         = null;
        Object []       objArr            = null;
        Constructor     constructor       = null;
        String          className         = "jsim.variate." + distType;
        Vector<Object>  v                 = new Vector<Object> ();
        int             i;

        if (alpha != null)  v.add (alpha);
        if (beta  != null)  v.add (beta);
        if (gamma != null)  v.add (gamma);

        argsClass = new Class<?> [v.size () + 1];
        objArr    = new Object [v.size () + 1];
        for (i = 0; i < v.size (); i++) {
            argsClass [i] = double.class;
            objArr [i]    = (Double) v.get (i);
        } // for
        argsClass [i] = int.class;
        objArr [i]    = stream;

        try {

            variateDefinition = Class.forName(className);
            constructor = variateDefinition.getConstructor (argsClass);
            variate = (Variate) constructor.newInstance (objArr);

        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (NoSuchMethodException e) {
            System.out.println(e);
        } catch (InstantiationException e) {
            System.out.println(e);
        } catch (IllegalAccessException e) {
            System.out.println(e);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        } catch (InvocationTargetException e) {
            System.out.println(e);
        } // try
    
        return variate;

    } // toVariate

    
    /****************************************************************
     * Main method for testing class.
     * @param  args  command-line arguments
     */
    public static void main (String [] args)
    {
        String   className = "jsim.variate.Variate";
        Object   myObject = null;
        Class<?> myClass = null;
        Object   objArr[] = new Object[1]; objArr[0] = new Integer(1);
        Constructor myConstructors[] = null;
    
        try {
            myClass = Class.forName(className);
            myConstructors = myClass.getConstructors ();
            myObject = myConstructors[0].newInstance(objArr);
    
            Variate v = VariateConverter.toVariate ("Variate", new Integer (1),
                                              null, null, null);
            v.printName ();
    
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace ();
        }; // try
    
    }; // main


}; // class


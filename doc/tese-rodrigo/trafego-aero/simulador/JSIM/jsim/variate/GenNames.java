/******************************************************************
 * @(#) GenNames.java     1.3     99/8/20
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
 * @author      John MilleR
 */
 

package jsim.variate;

import java.io.*;


/******************************************************************
 * Generate a list of all variate names.  Stores as constants
 * within a generated java file (VarNames.java).
 */

public class GenNames
{

    /**************************************************************
     * Main method.
     * @param  args  command-line arguments
     */
    public static void main (String [] args) throws IOException
    {
	BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
        String name;

        System.out.println (
            "\n" +
            "/******************************************************\n" +
            " * VarNames.java -- a generated class" +
            " */" +
            "\n" +
            "package jsim.variate;\n" +
            "\n" +
            "\n" +
            "/******************************************************\n" +
            " * Class listing names of all variates." +
            " */" +
            "\n" +
            "public class VarNames {\n" +
            "\n" +
            "\tpublic static final String [] NAME = {" );
    
        while ((name = br.readLine ()) != null) {

            if ( ! name.startsWith ("GenNames") &&
		 ! name.startsWith ("VarNames")) {
                name = name.substring (0, name.indexOf ('.'));
                System.out.println ("\t\t\"" + name + "\",");
            }; // if

        }; // while
    
        System.out.println ("\t};");
        System.out.println ("}; // class");

    }; // main


}; // class


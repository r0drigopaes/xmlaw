/******************************************************************
 * @(#) Token.java     1.3
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

import java.awt.*;


/******************************************************************
 * The Token class allows tokens to be created.  A Token object
 * controls its own acquisition.
 */

public class Token
{
    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Whether the token is busy
     */
    private boolean    busy;

    /**
     * Entity being served
     */
    private SimObject  client;

    /**
     * Position of client
     */
    private Point      location;


    /**************************************************************
     * Constructs a free, unpositioned token (service unit).
     */
    Token ()
    {
        busy     = false;
        client   = null;
        location = null;

    }; // Token


    /**************************************************************
     * Is the token busy?
     * @return  boolean  whether the token is busy
     */
    boolean isBusy ()
    {
        return busy;

    }; // isBusy


    /**************************************************************
     * Get the client using the service unit (null if none).
     * @return  SimObject  client using token
     */
    SimObject getClient ()
    {
        return client;

    }; // getClient


    /**************************************************************
     * Set the location of the token.
     * @param  loc  new location of token
     */
    void setLocation (Point loc)
    {
        location = loc;

    }; // setLocation


    /**************************************************************
     * Get the location of the token.
     * @param  Point  current location of token
     */
    Point getLocation ()
    {
        return location;

    }; // getLocation


    /**************************************************************
     * Assign client cli to the service unit.
     * @param  cli  client requesting token
     */
    void assign (SimObject cli)
    {
        busy   = true;
        client = cli; 

    }; // assign


    /**************************************************************
     * Free the token.
     */
    void free ()
    {
        busy   = false;
        client = null; 

    }; // free


}; // class


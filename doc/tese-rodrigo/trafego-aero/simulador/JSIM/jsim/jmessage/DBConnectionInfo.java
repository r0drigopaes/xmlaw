/******************************************************************
 * @(#) DBConnectionInfo.java     1.3
 *
 * Copyright (c) 2000 John Miller
 * All Right Reserved
 *-----------------------------------------------------------------
 * Permission to use, copy, modify and distribute this software and
 * its documentation without fee is hereby granted provided that
 * this copyright notice appears in all copies.
 * WE MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY ANY USER AS A RESULT OFUSING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *-----------------------------------------------------------------
 *
 * @version     1.3 5 (December 2000)
 * @author      Xueqin Huang, John Miller
 */

package jsim.jmessage;

import java.io.Serializable;

/******************************************************************
 * This class contains the information required to establish a database
 * connection.
 */
public class DBConnectionInfo implements Serializable 
{
    /**
     * Type of client/server framework. Only needed by the Cloudscape
     * database management system that we use here by default.
     */
    private String	framework;

    /**
     * Database URL
     */
    private String	url;

    /**
     * User name
     */
    private String	username;

    /**
     * Password
     */
    private String	passwd;

    /**
     * Driver class name
     */
    private String	driverClassName;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 5847584212343544565L;

    /******************************************************************
     * Constructs a DBConnectionInfo object using connection framework,
     * database url, username, password, and JDBC driver class name.
     * @param  framework	Connection framework
     * @param  url		Database URL
     * @param  usrname		User name
     * @param  driverClassName	JDBC driver class name
     */
    public DBConnectionInfo (String framework, String url, String usrname,
			     String passwd, String driverClassName) 
    {
	this.framework = framework;
	this.url = url;
	this.username = username;
	this.passwd = passwd;
	this.driverClassName = driverClassName;

    }; // DBConnectionInfo constructor

    /******************************************************************
     * Returns the type of client/server connection framework used by
     * this connection.
     * @return  framework	The client/server connection framework
     */
    public String getFramework () 
    { 
	return framework; 

    }; // getFramework

    /******************************************************************
     * Returns the database URL.
     * @return  url		The database URL
     */
    public String getUrl ()  
    { 
	return url; 

    }; // getUrl

    /******************************************************************
     * Returns the user name.
     * @return  username	The user name
     */
    public String getUserName () 
    { 
	return username; 

    }; // getUserName

    /******************************************************************
     * Returns the password.
     * @return  passwd		The password
     */
    public String getPassword () 
    { 
	return passwd; 

    }; // getPassword

    /******************************************************************
     * Returns the JDBC driver class name.
     * @param  driverClassName	The JDBC driver class name
     */
    public String getDriverClassName () 
    { 
	return driverClassName; 

    }; // getDriverClassName

}; // class DBConnectionInfo

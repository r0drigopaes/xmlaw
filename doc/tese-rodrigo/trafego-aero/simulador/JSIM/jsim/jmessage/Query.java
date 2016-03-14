/******************************************************************
 * @(#) Query.java   1.3
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
 * @version 1.3 (12 December 2000)
 * @author  Xueqin Huang, John Miller
 */

package jsim.jmessage;

/***************************************************************
 * The Query class is a JSIM message type encapsulating a SQL query
 * string.
 */
public class Query extends Message {

    /**
     * The SQL query string to be sent to the DBAgent.
     */
    private String		queryStr;

	/**
	 * Unique identifier of the serialized object
	 */
	private static final long serialVersionUID = 6854703020302915825L;

    /***************************************************************
     * Constructs a Query message.
     * @param actionType	The type of query: "Sequence query" or
     *				"Normal query"
     */
    public Query (String actionType, String queryStr) {

	this.actionType = actionType;
	this.queryStr = queryStr;

    }; // Query constructor


    /***************************************************************
     * Returns the query string.
     * @return String		The query string
     */
    public String getQueryString () 
    { 
	return queryStr; 

    }; // getQueryString 

}; // class Query

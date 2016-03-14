/******************************************************************
 * @(#) NodeProps.java   1.3
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

import jsim.util.Trunc;

/***************************************************************
 * The NodeProps class represents the properties of a node to be stored
 * in the database.
 */
public class NodeProps implements java.io.Serializable 
{
    /**
     * The node name
     */
    public String    nodeName;

    /**
     * The node type (SOURCE, FACILITY, SINK, etc)
     */
    public int       nodeType;

    /**
     * The number of tokens associated with this node.
     */
    public int       numOfTokens;

    /**
     * The name of the distribution type (variate name)
     */
    public String    timeDist;

    /**
     * The alpha parameter for the distribution
     */
    public Double    alpha = null;

    /**
     * The beta parameter for the distribution
     */
    public Double    beta = null;

    /**
     * The beta parameter for the distribution
     */
    public Double    gamma = null;

    /**
     * The stream parameter for the distribution
     */
    public Integer   stream = null;

	/**
	 * Unique identifier of the serialized object
	 */
	private static final long serialVersionUID = 4837485712323354654L;

    /***************************************************************
     * Constructs a NodeProps instance.
     * @param nodeName		The node name
     * @param nodeType		The node type
     * @param numOfTokens	The number of tokens
     * @param timeDist		The distribution name
     * @param alpha		The first parameter of the distribution,
     * 				if there are at least 2 parameters.
     * @param beta		The second parameter of the distribution,
     *				if there are at least 3 parameters.
     * @param gamma		The third parameter of the distribution,
     *				if there are at least 4 parameters.
     * @param stream		The last parameter of the distribution.
     *				(All distribution variates have at least
     *				this parameter.)
     */
    public NodeProps (String   nodeName,
                      int      nodeType,
                      int      numOfTokens,
                      String   timeDist,
                      Double   alpha,
                      Double   beta,
                      Double   gamma,
		      Integer  stream) 
    {

        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.numOfTokens = numOfTokens;
        this.timeDist = timeDist;
	this.alpha = alpha;
	this.beta = beta;
	this.gamma = gamma;
        this.stream = stream;

    }; // NodeProps constructor


    /***************************************************************
     * Returns an XML fragment representing this instance of NodeProps.
     */
    public String toString ()
    {
	String  xmlStr = "";
	
	xmlStr += "\n            <NodeType>" + nodeType + "</NodeType>";
	xmlStr += "\n            <NodeName>" + nodeName + "</NodeName>";
	xmlStr += "\n            <NumOfTokens>" + numOfTokens + "</NumOfTokens>";
	xmlStr += "\n            <DistributionType>" + timeDist
						+ "</DistributionType>";
	if (alpha != null) {
	   xmlStr += "\n            <Alpha>" + alpha + "</Alpha>";
	};

	if (beta  != null) {
	   xmlStr += "\n            <Beta>" + beta + "</Beta>";
	};

	if (gamma != null) {
	   xmlStr += "\n            <Gamma>" + gamma + "</gamma>";
	};

	xmlStr += "\n            <Stream>" + stream + "</Stream>";

	return xmlStr;

    }; // toString

}; // class NodeProps

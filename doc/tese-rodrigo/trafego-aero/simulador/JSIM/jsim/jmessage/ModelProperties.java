/******************************************************************
 * @(#) ModelProperties.java   1.3
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

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import jsim.util.Trunc;


/***************************************************************
 * The ModelProperties class represents all properties of a model.
 * It is used by ModelBean and ModelAgent to exchange information
 * about the input properties of a model.
 */
public class ModelProperties extends Message 
{
    /**
     * The model name
     */
    private String      modelName;

    /**
     * Number of nodes in the model
     */
    private int	       numOfNodes;

    /**
     * The name of each node
     */
    private String []   nodeName;

    /**
     * The type of each node
     */
    private int []      nodeType;

    /**
     * The number of tokens for each node
     */
    private int []      numOfTokens;

    /**
     * The time distribution of each node
     */
    private String []   timeDist;

    /**
     * The first parameter of a two-param variate for each node
     */
    private Double []   alpha;

    /**
     * The second parameter of a three-param variate for each node
     */
    private Double []   beta;

    /**
     * The third parameter of a four-param variate for each node
     */
    private Double []   gamma;

    /**
     * The last parameter of a variate for each node
     */
    private Integer []  stream;

	/**
	 * Unique identifier of the serialized object
	 */
	private static final long serialVersionUID = 2334545677878654323L;

    /***************************************************************
     * Constructs a ModelProperties instance.
     * @param actionType	The message type
     * @param modelName		The model name
     * @param numOfNodes	The total number of model nodes
     * @param nodeName		The name of each node
     * @param timeDist		The time distribution for each node
     * @param nodeType		The type of each node
     * @param numOfTokens	The # of tokens for each node
     * @param stream		The stream parameter for the variate of each node
     * @param alpha		The alpha parameter for the variate of each node.
     *				null if no alpha parameter.
     * @param beta              The beta parameter for the variate of each node.
     *                          null if no beta parameter.
     * @param gamma             The gamma parameter for the variate of each node.
     *                          null if no gamma parameter.
     */
    public ModelProperties (String     actionType,
                    String     modelName,
                    int	       numOfNodes,
                    String []  nodeName,
                    String []  timeDist,
                    int []     nodeType,
                    int []     numOfTokens,
                    Integer [] stream,
                    Double []  alpha,
                    Double []  beta,
                    Double []  gamma) {

	this.actionType = actionType;
        this.modelName = modelName;
        this.numOfNodes = numOfNodes;
        this.nodeName = nodeName;
        this.timeDist = timeDist;
        this.nodeType = nodeType;
        this.numOfTokens = numOfTokens;
        this.stream = stream;

	this.alpha = new Double [numOfNodes];
	this.beta = new Double [numOfNodes];
	this.gamma = new Double [numOfNodes];
	for (int i = 0; i < numOfNodes; i++) {
           this.alpha [i] = Trunc.trunc (alpha [i]);
           this.beta [i] =  Trunc.trunc (beta [i]);
           this.gamma [i] = Trunc.trunc (gamma [i]);
	}; // for

    }; // ModelProperties constructor


    /***************************************************************
     * Returns the model name.
     * @return String	The model name
     */
    public String getModelName () 
    { 
	return modelName; 

    }; // getModelName


    /***************************************************************
     * Returns the number of nodes.
     * @return int	The number of nodes
     */
    public int getNumOfNodes () 
    { 
	return numOfNodes; 

    }; // getNumOfNodes


    /***************************************************************
     * Returns the node names.
     * @return String [] The name of each node
     */
    public String [] getNodeName () 
    { 
	return nodeName; 
 
    }; // getNodeName

    /***************************************************************
     * Returns the type of distribution.
     * @return String [] The distribution type
     */
    public String [] getDistributionType () 
    { 
	return timeDist; 

    }; // getDistributionType


    /***************************************************************
     * Returns the node type.
     * @return int []	The node type
     */
    public int [] getNodeType () 
    { 
	return nodeType;

    }; // getNodeType


    /***************************************************************
     * Returns the number of tokens for each node.
     * @return int [] The number of tokens for each node
     */
    public int [] getNumOfTokens () 
    { 
	return numOfTokens;

    }; // getNumOfTokens


    /***************************************************************
     * Returns the stream parameters.
     * @return  Integer []	The stream parameters
     */
    public Integer [] getStream () 
    { 
	return stream;

    }; // getStream


    /***************************************************************
     * Returns the alpha parameters.
     * @return  Double []	The alpha parameters
     */
    public Double [] getAlpha () 
    { 
	return alpha; 

    }; // getAlpha


    /***************************************************************
     * Returns the beta parameters.
     * @return  Double []       The beta parameters
     */
    public Double [] getBeta () 
    { 
	return beta;

    }; // getBeta


    /***************************************************************
     * Returns the gamma parameters.
     * @return  Double []       The gamma parameters
     */
    public Double [] getGamma () 
    { 
	return gamma;

    }; // getGamma


    /***************************************************************
     * Sets the distribution type.
     * @param  distType		The distribution type
     */
    public void setDistributionType (String [] distType) 
    {
        timeDist = distType; 

    }; // setDistributionType


    /***************************************************************
     * Sets the number of tokens.
     * @param  numOfTokens         The number of tokens
     */
    public void setNumOfTokens (int [] numOfTokens) 
    { 
	this.numOfTokens = numOfTokens; 

    }; // setNumOfTokens


    /***************************************************************
     * Sets the stream parameters.
     * @param stream		The stream parameters.
     */
    public void setStream (Integer [] stream)  
    { 
	this.stream = stream; 
   
    }; // setStream


    /***************************************************************
     * Sets the alpha parameters.
     * @param alpha            The alpha parameters.
     */
    public void setAlpha (Double [] alpha) 
    { 
	this.alpha = alpha;

    }; // setAlpha


    /***************************************************************
     * Sets the beta parameters.
     * @param beta            The beta parameters.
     */
    public void setBeta (Double [] beta)   
    { 
	this.beta = beta; 

    }; // setBeta


    /***************************************************************
     * Sets the gamma parameters.
     * @param gamma           The gamma parameters.
     */
    public void setGamma (Double [] gamma) 
    { 
	this.gamma = gamma; 

    }; // setGamma


    /***************************************************************
     * Returns an XML fragment representing this instance of ModelProperties.
     * @return String 		The XML fragment
     */
    public String toString ()
    {
	String  xmlStr = "";
	
	xmlStr += "\n        <ModelName>" + modelName + "</ModelName>";
	for (int i = 0; i < numOfNodes; i++) {
	   xmlStr += "\n        <Node inputID='" + i + "'>";
	   xmlStr += "\n          <NodeType>" + nodeType [i] + "</NodeType>";
	   xmlStr += "\n          <NodeName>" + nodeName [i] + "</NodeName>";
	   xmlStr += "\n          <NumOfTokens>" + numOfTokens [i] + "</NumOfTokens>";
	   xmlStr += "\n          <DistributionType>" + timeDist [i]
						+ "</DistributionType>";
	   if (alpha [i] != null) {
		   xmlStr += "\n          <Alpha>" + alpha [i] + "</Alpha>";
	   };

	   if (beta [i] != null) {
		   xmlStr += "\n          <Beta>" + beta [i] + "</Beta>";
	   };

	   if (gamma [i] != null) {
		   xmlStr += "\n          <Gamma>" + gamma [i] + "</gamma>";
	   };

	   xmlStr += "\n          <Stream>" + stream [i] + "</Stream>";
	   xmlStr += "\n        </Node>";
	}; // for

	return xmlStr;

    }; // toString

}; // class ModelProperties

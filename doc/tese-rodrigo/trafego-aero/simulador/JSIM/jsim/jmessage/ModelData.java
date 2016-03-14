
/******************************************************************
 * @(#) ModelData.java   1.3
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

import java.io.*;

/***************************************************************
 * The ModelData class contains all model information to be stored in the
 * database. It includes model name, and the input and output of all
 * nodes associated with this model.
 */
public class ModelData implements Serializable 
{
    /**
     * Serialization id
     */
    private static final long	serialVersionUID = -8234208208924284028L;

    /**
     * The model name
     */
    public String		modelName;

    /**
     * The input model properties
     */
    public NodeProps []		props;

    /**
     * The output statistical result of the simulation
     */
    public NodeStats []		stats;


    /***************************************************************
     * Constructs an object of type ModelData by model name, input node
     * properties, and output statictical result.
     * @param  modelName	The model name
     * @param  props		The input properties of all the nodes
     * @param  stats		The output statistical result of the simulation
     */
    public ModelData (String modelName, NodeProps [] props, NodeStats [] stats)
    {
	this.modelName = modelName;
	this.props = props;
	this.stats = stats;

    }; // ModelData constructor


    /***************************************************************
     * Returns the model name.
     * @return  String		The model name
     */
    public String		getModelName () 
    { 
	return modelName;

    }; // getModelName


    /***************************************************************
     * Returns the node properties.
     * @return  NodeProps []	The properties of all nodes
     */
    public NodeProps []		getNodeProps () 
    { 
	return props;

    }; // getNodeProps


    /***************************************************************
     * Returns the statistical output of the simulation.
     * @return  NodeStats []		The statistical output
     */
    public NodeStats []		getOutputStats () 
    { 
	return stats;

    }; // getOutputNodeStats


    /***************************************************************
     * Returns an XML fragment representing this instance of ModelData.
     * @return  String	The XML fragment to be returned
     */
    public String toString ()
    {
	String xmlStr = "";

	xmlStr += "\n        <ModelName>" + modelName + "</ModelName>";

	xmlStr += "\n        <ModelNodes>";
	for (int i = 0; i < props.length; i++) {
	   xmlStr += "\n          <Node id='" + i + "'>";
	   xmlStr += props [i].toString ();
	   xmlStr += stats [i].toString ();
	   xmlStr += "\n          </Node>";
	}; // for
	xmlStr += "\n        </ModelNodes>";

	return xmlStr;
	
    }; // toString


    /***************************************************************
     * Meets the Java serialization requirements.
     * @param  out	The ObjectOutputStream
     * @exception java.io.IOException
     */
    public void writeObject (ObjectOutputStream out)
		throws IOException 
    {

	out.writeObject (modelName);
	int nNodes = props.length;
	out.writeInt (nNodes);
	for (int i = 0; i < nNodes; i++) {
		out.writeObject (props [i]);
		out.writeObject (stats [i]);
	}; // for

    }; // writeObject


    /***************************************************************
     * Meets the Java serialization requirements.
     * @param  in	The ObjectInputStream
     * @exception	java.io.Exception, java.lang.ClassNotFoundException
     */
    public void readObject (ObjectInputStream in)
		throws IOException, ClassNotFoundException {

	modelName = (String) in.readObject ();
	int nNodes = (int) in.readInt ();
	props = new NodeProps [nNodes];
	stats = new NodeStats [nNodes];
	for (int i = 0; i < nNodes; i++) {
		props [i] = (NodeProps) in.readObject ();
		stats [i] = (NodeStats) in.readObject ();
	}; // for

    }; // readObject


    /***************************************************************
     * Meets the Koala XML serialization requirements
     * @param  in	The XML serialization GeneratorInputStream provided by KOML
     * @exception	java.io.IOException, java.lang.ClassNotFoundException
    public static void readObject (ObjectInputStream in)
		throws IOException, ClassNotFoundException {

        XMLSerializer.readObjectModel (in);

    }; // readObject
     */

}; // class ModelData


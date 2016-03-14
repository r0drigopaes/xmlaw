/******************************************************************
 * @(#) NodeStats.java     1.3
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
 * @version     1.3 (5 December 2000)
 * @author      Xueqin Huang, John Miller
 */

package jsim.jmessage;

import java.io.Serializable;

/******************************************************************
 */
public class NodeStats implements Serializable
{
    /**
     * Serialization ID for instances of this class.
     */
    private static final long   serialVersionUID=6865934528700823025L;

    ///////////////// Time statistics \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public Integer tNoSamples = null;
    public Double  tMinValue = null;
    public Double  tMaxValue = null;
    public Double  tMeanValue = null;
    public Double  tDeviation = null;
    public Double  tInterval = null;
    public Double  tPrecision = null;

    ///////////////// Time observation statistics \\\\\\\\\\\\\\\\\\\
    public Integer tObsNoSamples = null;
    public Double  tObsMinValue = null;
    public Double  tObsMaxValue = null;
    public Double  tObsMeanValue = null;
    public Double  tObsDeviation = null;
    public Double  tObsInterval = null;
    public Double  tObsPrecision = null;

    ///////////////// Occupancy statistics \\\\\\\\\\\\\\\\\\\\\\\\\
    public Integer oNoSamples = null;
    public Double  oMinValue = null;
    public Double  oMaxValue = null;
    public Double  oMeanValue = null;
    public Double  oDeviation = null;
    public Double  oInterval = null;
    public Double  oPrecision = null;

    ///////////////// Occupancy observation statistics \\\\\\\\\\\\\
    public Integer oObsNoSamples = null;
    public Double  oObsMinValue = null;  
    public Double  oObsMaxValue = null; 
    public Double  oObsMeanValue = null;
    public Double  oObsDeviation = null;
    public Double  oObsInterval = null; 
    public Double  oObsPrecision = null;


    /**************************************************************
     * Constructs an instance of NodeStats.
     * @param  tStat		The time statistics
     * @param  oStat		The occupancy statistics
     * @param  tObsStat		The time observation staticstics
     * @param  oObsStat		The occupancy observation statistics
     */
    public NodeStats (Double []	tStat,
                  Double []	tObsStat,
                  Double []	oStat,
                  Double []	oObsStat)
    {
	if (tStat != null) {
	   tNoSamples = new Integer (tStat [0].intValue ());
	   tMinValue = tStat [1];
	   tMaxValue = tStat [2];
	   tMeanValue = tStat [3];
	   tDeviation = tStat [4];
	   tInterval = tStat [5];
	   tPrecision = tStat [6];
	}; // if

	if (tObsStat != null) {
	   tObsNoSamples = new Integer (tObsStat [0].intValue ());
           tObsMinValue = tObsStat [1];
           tObsMaxValue = tObsStat [2];
           tObsMeanValue = tObsStat [3];
           tObsDeviation = tObsStat [4];
           tObsInterval = tObsStat [5];
           tObsPrecision = tObsStat [6];
	}; // if

	if (oStat != null) {
	   oNoSamples = new Integer (oStat [0].intValue ());
           oMinValue = oStat [1];
           oMaxValue = oStat [2];
           oMeanValue = oStat [3];
           oDeviation = oStat [4];
           oInterval = oStat [5];
           oPrecision = oStat [6];
	}; // if

	if (oObsStat != null) {
           oObsNoSamples = new Integer (oObsStat [0].intValue ());
           oObsMinValue = oObsStat [1];
           oObsMaxValue = oObsStat [2];
           oObsMeanValue = oObsStat [3];
           oObsDeviation = oObsStat [4];
           oObsInterval = oObsStat [5];
           oObsPrecision = oObsStat [6];
	}; // if

    }; // NodeStats constructor


    /**************************************************************
     * Returns an XML fragment representing the NodeStats
     * @return  String		The XML fragment representing this instance
     *				of NodeStats
     */
    public String toString ()
    {
	String xmlStr = "";

	if (tNoSamples != null) {
	   xmlStr += "\n            <TimeStat>";
	   xmlStr += "\n              <NoSamples>" + tNoSamples + "</NoSamples>";
	   xmlStr += "\n              <MinValue>" + tMinValue + "</MinValue>";
	   xmlStr += "\n              <MaxValue>" + tMaxValue + "</MaxValue>";
	   xmlStr += "\n              <MeanValue>" + tMeanValue + "</MeanValue>";
	   xmlStr += "\n              <Deviation>" + tDeviation + "</Deviation>";
	   xmlStr += "\n              <Interval>" + tInterval + "</Interval>";
	   xmlStr += "\n              <Precision>" + tPrecision + "</Precision>";
	   xmlStr += "\n            </TimeStat>";
	}; // if

	if (tObsNoSamples != null) {
           xmlStr += "\n            <TimeObsStat>";
           xmlStr += "\n              <NoSamples>" + tObsNoSamples + "</NoSamples>";
           xmlStr += "\n              <MinValue>" + tObsMinValue + "</MinValue>";
           xmlStr += "\n              <MaxValue>" + tObsMaxValue + "</MaxValue>";   
           xmlStr += "\n              <MeanValue>" + tObsMeanValue + "</MeanValue>";
           xmlStr += "\n              <Deviation>" + tObsDeviation + "</Deviation>";
           xmlStr += "\n              <Interval>" + tObsInterval + "</Interval>";
           xmlStr += "\n              <Precision>" + tObsPrecision + "</Precision>";
           xmlStr += "\n            </TimeObsStat>";
	}; // if

	if (oNoSamples != null) {
           xmlStr += "\n            <OccupancyStat>";
           xmlStr += "\n              <NoSamples>" + oNoSamples + "</NoSamples>";
           xmlStr += "\n              <MinValue>" + oMinValue + "</MinValue>";
           xmlStr += "\n              <MaxValue>" + oMaxValue + "</MaxValue>";   
           xmlStr += "\n              <MeanValue>" + oMeanValue + "</MeanValue>";
           xmlStr += "\n              <Deviation>" + oDeviation + "</Deviation>";
           xmlStr += "\n              <Interval>" + oInterval + "</Interval>";
           xmlStr += "\n              <Precision>" + oPrecision + "</Precision>";
           xmlStr += "\n            </OccupancyStat>";
	}; // if

	if (oObsNoSamples != null) {
           xmlStr += "\n            <OccupancyObsStat>";
           xmlStr += "\n              <NoSamples>" + oObsNoSamples + "</NoSamples>";
           xmlStr += "\n              <MinValue>" + oObsMinValue + "</MinValue>";
           xmlStr += "\n              <MaxValue>" + oObsMaxValue + "</MaxValue>";
           xmlStr += "\n              <MeanValue>" + oObsMeanValue + "</MeanValue>";
           xmlStr += "\n              <Deviation>" + oObsDeviation + "</Deviation>";
           xmlStr += "\n              <Interval>" + oObsInterval + "</Interval>";
           xmlStr += "\n              <Precision>" + oObsPrecision + "</Precision>";
           xmlStr += "\n            </OccupancyObsStat>";
	}; // if
		
	return xmlStr;

    }; // toString

}; // class NodeStats

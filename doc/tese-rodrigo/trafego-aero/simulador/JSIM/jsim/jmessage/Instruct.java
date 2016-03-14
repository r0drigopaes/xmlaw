/******************************************************************
 * @(#) Instruct.java     1.3
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

/******************************************************************
 * This class contains data for the Instruct event.
 */
public class Instruct extends Message 
{
    /**
     * The ID for the scenario
     */
    private Integer     scenarioID;

	/**
	 * Unique identifier of the serialized object
	 */
	private static final long serialVersionUID = 4345743228832314324L;

    /**************************************************************
     * Constructs an Instruct message by actionType and scenarioID.
     * @param  actionType       Type of the message
     * @param  scenarioID	ID for the scenario
     */
    public Instruct (String actionType, Integer scenarioID) 
    {

	this.actionType = actionType;
        this.scenarioID = scenarioID;

    }; // Instruct constructor

    /**************************************************************
     * Returns the ID of the scenario.
     * @param  Integer		The scenario ID
     */
    public Integer getScenarioID () 
    { 
	return scenarioID;
 
    }; // getScenario

}; // class Instruct

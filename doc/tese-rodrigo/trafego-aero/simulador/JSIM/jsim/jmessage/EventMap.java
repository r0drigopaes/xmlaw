/******************************************************************
 * @(#) EventMap.java     1.3
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
 * @author      John A. Miller, Xueqin Huang
 */

package jsim.jmessage;

/******************************************************************
 * This class contains all the events supported by JSIM.
 */
public class EventMap
{
    //////// Events between model beans (ModelBean) \\\\\\\\\\\\\\\\\\\\
    /**
     * An event from a model bean to another model bean for the purpose
     * of simulation federation. (Message type: Inject)
     */
    public static final long INJECT_EVT   = 10;


    //////// Events between ModelAgenta and ModelBeans \\\\\\\\\\\\\\\\\
    /**
     * An event from a model agent to a model bean to inquire about the
     * the properties of the model represented by the model bean.
     * (Message type: Inquire)
     */
    public static final long INQUIRE_EVT  = 20;

    /**
     * An event from a model bean to inform a model agent about the
     * properties of the model represented by the model bean.
     * (Message type: ModelProperties)
     */
    public static final long INFORM_EVT   = 21;

    /**
     * An event from a model agent to a model bean to reflect the
     * changes made by the user to the model properties back to the
     * model itself. (Message type: ModelProperties)
     */
    public static final long CHANGE_EVT   = 22;

    /**
     * An event from a model bean to notify a model agent that the changes
     * to the model have been successfully updated.
     * (Message type: NONE)
     */
    public static final long CHANGED_EVT  = 23;

    /**
     * An event from a model agent to a model bean containing instructions
     * about how the simulation is to be executed.
     * (Message type: Simulate)
     */
    public static final long SIMULATE_EVT = 24;

    /**
     * An event from a model bean to a model agent carrying interim or
     * final simulation result. 
     * (Message type: InterimReport/FinalReport)
     */
    public static final long REPORT_EVT   = 25;


    /////// Events between the ScenarioAgent and ModelAgents \\\\\\\\\\\\
    /**
     * An event from the scenario agent to model agents participating in 
     * a simulation federation. It carries a global ID for the scenario
     * and instructions for all participating model agents to start the
     * simulation. The simulation result collected by each of the model
     * agent can later be stored in the database using the same scenario 
     * ID. (Message type: Instruct)
     */
    public static final long INSTRUCT_EVT = 30;


    /////// Events between ModelAgents and the DBAgent \\\\\\\\\\\\\\\\\\
    /**
     * An event from a model agent to the database agent carrying data to
     * to be stored in the database. (Message type: Store)
     */
    public static final long STORE_EVT    = 40;


    ///// Events between the ScenarioAgent and the DBAgent \\\\\\\\\\\\\\
    /**
     * An event from the scenario agent to the database agent carrying a
     * SQL query statement. (Message type: Query)
     */
    public static final long QUERY_EVT    = 50;

    /**
     * An event from the database agent to the scenario agent carrying a
     * query result. (Message type: Result)
     */
    public static final long RESULT_EVT   = 51;

}; // EventMap

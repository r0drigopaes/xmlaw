/* ****************************************************************
 *   @filename:		Mask.java
 *   @projectname:  Law
 *   @date:			Jul 6, 2004
 *   @author 		guga - LES (PUC-Rio)
 * 	 
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.event;

import br.pucrio.inf.les.law.util.BidirectionalIntTable;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author guga - LES (PUC-Rio)
 * @version $Revision$
 */
public abstract class Masks {
    

    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(Masks.class);

    public static final int MESSAGE_ARRIVAL = (int)Math.pow(2,0);

    public static final int TRANSITION_ACTIVATION = (int)Math.pow(2,1);
    
    public static final int CLOCKTICK = (int)Math.pow(2,2);

    public static final int DEACTIVATION_EVENT = (int)Math.pow(2,3);    

    public static final int NORM_ACTIVATION = (int)Math.pow(2,4);
    
    public static final int NORM_DEACTIVATION = (int)Math.pow(2,5);
    
    public static final int CLOCK_ACTIVATION = (int)Math.pow(2,6);    

    public static final int FAILURE_STATE_REACHED = (int)Math.pow(2,7);

    public static final int SUCCESSFUL_STATE_REACHED = (int)Math.pow(2,8);

    public static final int ACTION_ACTIVATION = (int)Math.pow(2,9);

    public static final int SCENE_SUCCESSFUL_COMPLETION = (int)Math.pow(2,10);

    public static final int SCENE_TIME_TO_LIVE_ELAPSED = (int)Math.pow(2,11);

    public static final int SCENE_FAILURE_COMPLETION = (int)Math.pow(2,12);

    public static final int SCENE_CREATION = (int)Math.pow(2,13);
    
    /**
     * This constant is used only for tests purposes.
     */
    public static final int STUB_EVENT = (int)Math.pow(2,14);
    
    /**
     * This constant is used only for tests purposes.
     */
    public static final int STUB_ACTIVATION = (int)Math.pow(2,15);
    
    public static final int CLOCK_DEACTIVATION = (int)Math.pow(2,16);
    
    public static final int STUB_DEACTIVATION = (int)Math.pow(2,17);
    
    public static final int COMPLIANT_MESSAGE = (int)Math.pow(2,18);
    
    public static final int ROLE_ACTIVATION = (int)Math.pow(2,19);
    
    public static final int ROLE_DEACTIVATION = (int)Math.pow(2,20);
    
    public static final int TIME_TO_LIVE_ELAPSED = (int)Math.pow(2,21);
    
    public static final int UPDATE_CRITICALITY = (int)Math.pow(2,22);
    
    public static final int CLOCK_TIMEOUT = (int)Math.pow(2,23);

    /**
     * Mask for all events
     */
    public static final int ALL = (int)Math.pow(2,24) - 1;

    private static BidirectionalIntTable table;

    private static void initTable() {
        table = new BidirectionalIntTable();

        table.put(MESSAGE_ARRIVAL, "message_arrival");
        table.put(TRANSITION_ACTIVATION, "transition_activation");
        table.put(DEACTIVATION_EVENT, "deactivation_event");
        table.put(CLOCKTICK, "clock_tick");
        table.put(CLOCK_ACTIVATION, "clock_activation");
        table.put(NORM_ACTIVATION, "norm_activation");
        table.put(FAILURE_STATE_REACHED, "failure_state_reached");
        table.put(SUCCESSFUL_STATE_REACHED, "successful_state_reached");
        table.put(ACTION_ACTIVATION, "action_activation");
        table.put(NORM_DEACTIVATION, "norm_deactivation");
        table.put(SCENE_SUCCESSFUL_COMPLETION, "sucessful_scene_completion");
        table.put(SCENE_TIME_TO_LIVE_ELAPSED, "time_to_live_elapsed");
        table.put(SCENE_FAILURE_COMPLETION, "failure_scene_completion");
        table.put(SCENE_CREATION, "scene_creation");
        table.put(STUB_EVENT, "stub_event");
        table.put(STUB_ACTIVATION, "stub_activation");
        table.put(CLOCK_DEACTIVATION, "clock_deactivation");
        table.put(STUB_DEACTIVATION, "stub_deactivation");
        table.put(COMPLIANT_MESSAGE, "compliant_message");
        table.put(ROLE_ACTIVATION, "role_activation");
        table.put(ROLE_DEACTIVATION, "role_deactivation");
        table.put(TIME_TO_LIVE_ELAPSED, "time_to_live_elapsed");
        table.put(UPDATE_CRITICALITY, "update_criticality");
        table.put(CLOCK_TIMEOUT, "clock_timeout");

        // Chech the consistency of the table
        List<Integer> keys = table.getKeys();
        int sum = 0;
        for (Integer integer : keys) {
            sum += integer.intValue();
        }
        if (sum != ALL) 
        {
            LOG.error("Inconsistent Masks Table. Check if all values put in the table are consistent with the constants declared");
            table = null;
        }

    }

    private static BidirectionalIntTable getTable() {
        if (table == null) {
            initTable();
        }
        return table;
    }

    public static String getName(int mask) {
        return (String) getTable().getByKey(mask);
    }

    public static int getMask(String eventName) {
        return getTable().getByValue(eventName);
    }
    
}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.5  2006/05/02 23:33:55  mgatti
 * UPDATE LOG: 02/05/2006:  XMLaw - Actions e Constraints
 * UPDATE LOG: Está dando quando as duas cenas de uma mesma organização estão especificadas (o "saco" dos states e transitions está pegando tudo)
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.4  2006/02/22 20:49:30  mgatti
 * UPDATE LOG: Maíra: 1ª Versão da Integração DimaX & XMLaw
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.3  2006/01/11 18:51:29  lfrodrigues
 * UPDATE LOG: inclusao das mascaras de norm_deactivatin e compliant_message
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.1  2005/12/05 18:29:48  rbp
 * UPDATE LOG: Versão inicial
 * UPDATE LOG: Revision 1.1 2005/08/17 17:48:14 lfrodrigues
 * instalacao do maven como ferramenta de build automatico Revision 1.1
 * 2005/05/10 16:22:56 guga Inicio Refactoring Revision 1.1 2005/04/29 21:40:57
 * guga Modificações na estrutura e primeira versao da implementacao do TAC SCM
 * Revision 1.7 2005/02/15 20:56:04 rbp Versão da dissertação Revision 1.6
 * 2004/12/16 16:08:35 rbp Included scene lifecycle and also the specification
 * of who can create and participate of a scene. Revision 1.5 2004/12/14
 * 20:06:32 guga Ajuste: Atualizando actions e constraints Revision 1.3
 * 2004/11/24 01:06:04 rbp Main modifications: - Allow multiple instances of the
 * same scene - Introducing a 3 layer mapping from XML: XML --> Descriptor Layer
 * --> Model Layer - Framework of actions introduced; now it is possible use
 * java code as consequence of some law configuration. - Implementation of a
 * case study in the context of product trading using SWT and JFace ( it
 * requires to configure the eclipse for running the application) - Some bugs
 * were fixed altough I don't remember of them. Revision 1.1 2004/09/22 01:04:38
 * rbp This version contains severals modifications: - Package renaming -
 * Specification of laws from XML language - Added a Sax based Xml parser for
 * reading from XML - Some hard-coded code were removed and placed in the
 * Config.properties file - Added the simple application example Revision 1.2
 * 2004/09/10 17:49:44 rbp Main modifications: Message is not an event anymore.
 * The information can be carried through the InfoCarrier object. TriggerManager
 * complete modification. Now it allows multiple norm and clock instances.
 * Revision 1.1 2004/07/14 21:08:16 rbp Inserindo alterações para permitir
 * multiplas organizações, cenários, não-determinismo etc. Revision 1.1
 * 2004/07/06 16:53:29 guga atualizacao subjects e observer por identificador
 ******************************************************************************/

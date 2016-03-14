package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.component.action.ActionDescriptor;
import br.pucrio.inf.les.law.component.clock.ClockDescriptor;
import br.pucrio.inf.les.law.component.norm.NormDescriptor;
import br.pucrio.inf.les.law.model.Id;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class TriggerKeeper {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(TriggerKeeper.class);

    /**
     * List of clocks that may exist in this scene
     */
    protected Hashtable<Id, ClockDescriptor> clocks = new Hashtable<Id, ClockDescriptor>();

    /**
     * List of norms that may exist in this scene
     */
    protected Hashtable<Id, NormDescriptor> norms = new Hashtable<Id, NormDescriptor>();
    
    /**
     * List of actions that may exist in this scene
     */
    protected Hashtable<Id, ActionDescriptor> actions = new Hashtable<Id, ActionDescriptor>();

    public void addClockDescriptor(ClockDescriptor clockDescriptor) {
        clocks.put(clockDescriptor.getId(), clockDescriptor);
    }

    public ClockDescriptor getClockDescriptor(Id clockDescriptorId) {
        return clocks.get(clockDescriptorId);
    }

    public void addNormDescriptor(NormDescriptor normDescriptor) {
        norms.put(normDescriptor.getId(), normDescriptor);
    }

    public NormDescriptor getNormDescriptor(Id normDescriptorId) {
        return norms.get(normDescriptorId);
    }
   
    public void addActionDescriptor(ActionDescriptor actionDescriptor) {
		actions.put(actionDescriptor.getId(),actionDescriptor);
	}
    
    public ActionDescriptor getActionDescriptor(Id actionDescriptorId) {
        return actions.get(actionDescriptorId);
    }

    protected void enableTriggers(Context context) {
    	//Se cadastrando para observar clocks
        for (ClockDescriptor clock : clocks.values()) {
            context.enableTrigger(clock);
        }
        //Se cadastrando para observar normas
        for (NormDescriptor norm : norms.values()) {
            context.enableTrigger(norm);
        }
        //Se cadastrando para observar actions
        for (ActionDescriptor action : actions.values()) {
            context.enableTrigger(action);
        }
       
    }

	public Hashtable<Id, ClockDescriptor> getClocks() {
		return clocks;
	}

	public Hashtable<Id, NormDescriptor> getNorms() {
		return norms;
	}

	public Hashtable<Id, ActionDescriptor> getActions() {
		return actions;
	}

	

}
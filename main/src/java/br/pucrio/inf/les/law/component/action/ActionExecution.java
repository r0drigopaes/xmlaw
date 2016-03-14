
package br.pucrio.inf.les.law.component.action;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.component.ElementTriggerDescriptor;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.execution.AbstractExecution;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.model.LawException;

/**
 * @author 	 mgatti
 */
public abstract class ActionExecution extends AbstractExecution {
	
	/**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ActionExecution.class);
	
	private Map<String,Object> parameters;
	
	private Event activationEvent;
	
	private ExecutionState executionState = ExecutionState.RUNNING ;
	
	public abstract void execute(Map<String,Object> parameters) 
			throws LawException;

	
	public ActionExecution(Context context, Map<String,Object> parameters, 
			ElementTriggerDescriptor descriptor){
		super(context,descriptor);
		this.parameters = parameters;
		executionState = ExecutionState.RUNNING;
		
		if (LOG.isDebugEnabled()) {
            LOG.debug("Creating Action execution ");
        }
        start();
	}

	public boolean isSatisfied(Map<String,Object> parameters) {
		return false;
	}

	public void start() {		
		try {
			execute(parameters);
		} catch (LawException e) {
			LOG.debug("Error while executing action "+ e.toString());
		}
	}
	
	public Event getActivationEvent(Map<String,Object> parameters) {
		return this.activationEvent;
	}
	
	public void setActivationEvent(Event activationEvent){
		this.activationEvent = activationEvent;
	}
	
    public boolean shouldStop(Event event) {
        return true;
    }

    public ExecutionState getExecutionState() {
        return executionState;
    }

	public void setExecutionState(ExecutionState state) {
		executionState = state;
	}
}
package br.pucrio.inf.les.law.component.protocol;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.execution.AbstractExecution;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProtocolExecution extends AbstractExecution implements IObserver {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ProtocolExecution.class);
    
    private ExecutionState executionState = ExecutionState.RUNNING ;

    private ArrayList<State> currentStates = new ArrayList<State>();

    public ProtocolExecution(Context context, State initialState, IDescriptor descriptor) {
        super(context,descriptor);
        this.context.attachObserver(this, Masks.ALL);
        this.currentStates.add(initialState);
    }

    public void resetProtocol(State initialState) {
        currentStates.clear();
        currentStates.add(initialState);
    }

    public void update(Event event) throws LawException {    	
        ArrayList<State> toBeAdded = new ArrayList<State>();
        for (State state : currentStates) {
            toBeAdded.addAll(state.step(event, context));
        }
        if (toBeAdded.size() == 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Nothing changed on the protocol state.");
            }
        } else {
            currentStates = toBeAdded;
            
            //if the event caused an transition and it is a message arrival event
            // a message compliant event is fired
            if(event.getType() == Masks.MESSAGE_ARRIVAL)
            {
            	Event messageCompliant = new Event(Masks.COMPLIANT_MESSAGE,new Id());
            	messageCompliant.addEventContent(Event.MESSAGE,event.getEventContent(Event.MESSAGE));
            	context.fire(messageCompliant);
            }
        }
    }

    public List<State> getCurrentState() {
        return currentStates;

    }

    public void stopExecution() {
//        currentStates = null;
//        context = null;
    }

    public boolean shouldStop(Event event) {
        return true;
    }

    public ExecutionState getExecutionState() {
        // TODO Verificar quando o protocolo termina a sua execução.
        return ExecutionState.RUNNING;
    }

	public void setExecutionState(ExecutionState executionState) {
		this.executionState = executionState;
	}

	public void notifySubjectDeath(Subject subject) {
		// TODO Auto-generated method stub
		
	}
}

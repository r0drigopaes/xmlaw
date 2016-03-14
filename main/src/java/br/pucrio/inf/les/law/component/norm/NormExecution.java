package br.pucrio.inf.les.law.component.norm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.message.RoleReference;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.AbstractExecution;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.model.LawException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NormExecution extends AbstractExecution {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(NormExecution.class);
    
    private ExecutionState executionState= ExecutionState.RUNNING ;

    private Map<RoleReference, Boolean> assignees;    

    private Type type;

    public enum Type {
        PERMISSION, OBLIGATION, PROHIBITION
    };

    /**
     * A norm is activated for a specific agent. This agent is identified
     * through the maching of variables originated at the message. Norms specify
     * an assignee variable which contains the role agent instance.
     */
    public NormExecution(Context context, Type type, ArrayList<RoleReference> assignees,
            IDescriptor descriptor) {
        super(context, descriptor);
        this.context = context;
        this.type = type;
        this.assignees = new HashMap<RoleReference, Boolean>();
        for (RoleReference assignee : assignees) {
			this.assignees.put(assignee,true);
		}
        

    }

    public void stopExecution() {
//        context.removeExecution(getId());
    }

    public boolean shouldStop(Event event) throws LawException {
        // Verifica se para o evento event a norma deve ser desativada
    	Message msg = (Message) event.getContent().get(Event.MESSAGE);
        if (msg == null) {
            throw new LawException("There is a need to pass the parameter `"
                    + Event.MESSAGE + "` in order to see if the norm : "
                    + toString() + " should be deactivated",
                    LawException.ROLES_DOESNT_MATCH);
        }
        
        boolean isActivated;
        RoleReference roleReference;
        Map<AgentIdentification, String> agents = new HashMap<AgentIdentification, String>();
        agents.putAll(msg.getAllReceivers());
        agents.put(msg.getSender(), msg.getSenderRole());
        for (AgentIdentification agentId : agents.keySet()) {
			String roleId = agents.get(agentId);
			for(Map.Entry entry : assignees.entrySet())
	        {
	        	roleReference = (RoleReference)entry.getKey();
	        	isActivated = (Boolean)entry.getValue();
	        	if (!isActivated){
	        		if (agentId.equals(roleReference.getRoleInstanceValue())){
	        			context.fire(new Event(getId(),Masks.NORM_DEACTIVATION, getId()));
	        	        event.addEventContent("assigneeId",agentId);
	        			entry.setValue(false);
	        		}
	        	}
	        }
		}
       
        return (!assignees.containsValue(true));
        
    }
    
    public Map<RoleReference, Boolean> getAssignees(){
    	return assignees;
    }
    
    public boolean containsAgentId(AgentIdentification aid){
    	for(Map.Entry entry : assignees.entrySet())
        {
    		RoleReference roleReference = (RoleReference)entry.getKey();
        	if (roleReference.getRoleInstanceValue().equals(aid)){
                return true;
        	}
        }
    	return false;
    }
    
    public ExecutionState getExecutionState(){
    	return executionState;
    }

	public void setExecutionState(ExecutionState state) {
		executionState = state;
	}

}

//Source file: D:\\MAÍRA\\2006.1.0\\XMLAW\\Projeto\\src\\xmlaw\\component\\norm\\NormDescriptor.java

package br.pucrio.inf.les.law.component.norm;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.ElementTriggerDescriptor;
import br.pucrio.inf.les.law.component.message.RoleReference;
import br.pucrio.inf.les.law.component.norm.NormExecution.Type;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NormDescriptor extends ElementTriggerDescriptor {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(NormDescriptor.class);

    private ArrayList<RoleReference> assignees;
    
    private RoleReference assignee;

    private NormExecution.Type type;

    public NormDescriptor(Id id, Type type) {
        super(id);
        this.type = type;
        assignees = new ArrayList<RoleReference>();
    }

    public void addAssignee(RoleDescriptor roleDescriptor, String roleInstance) throws LawException {
        if (roleDescriptor==null){
            throw new LawException("Assignee of norm ["+toString()+"] cannot have a null role descriptor.",LawException.ROLE_NOT_DECLARED);
        }
        if (roleInstance==null){
            throw new LawException("Assignee of norm ["+toString()+"] cannot have a null role instance.",LawException.ROLE_NOT_DECLARED);
        }
        RoleReference roleRef = new RoleReference();
        roleRef.setRoleInstance(roleInstance);
        roleRef.setRoleDescriptor(roleDescriptor);
        assignees.add(roleRef);
    }

    public boolean shouldCreate(Context context, Map<String,Object> parameters) throws LawException{
    	Message msg = (Message) parameters.get(Event.MESSAGE);
    	boolean resultReceiver = false;
    	boolean resultSender = false;
    	if (msg!=null){
    		for (AgentIdentification receiverId : msg.getAllReceivers().keySet()) {
    			resultReceiver = setAssigneeRoleInstanceValue(msg.getAllReceivers().get(receiverId),receiverId);
			}
    		resultSender = setAssigneeRoleInstanceValue(msg.getSenderRole(),msg.getSender());
    		
	        if (resultReceiver || resultSender){
	        	return true;
	        }else{
	        	return false;
	        }
    	}else{ 
            throw new LawException(
                    "There is a need to pass the parameter `"+Event.MESSAGE+"` in order to create the norm: "+getId(),
                    LawException.MISSING_MESSAGE_IN_EVENT_CONTENT);
        }
    }
    
    public boolean setAssigneeRoleInstanceValue(String roleId, AgentIdentification agentId){
    	boolean result = false;
    	for (RoleReference assignee : assignees) {
    		if (assignee.getRoleDescriptor().getId().toString().equals(roleId)){
            	assignee.setRoleInstanceValue(agentId);
            	this.assignee = assignee;
            	result = true;
            }
		}
    	return result;
    }
    
    public IExecution createExecution(Context context,
            Map<String, Object> parameters) throws LawException {
        // Verifies is some assignee was declared in the descriptor
        if (assignee==null){
            throw new LawException("Declaration of norm ["+toString()+"] is inconsistent. The norm has no assignee and an assignee is mandatory",LawException.INVALID_NORM);
        }

        // first fire the event
        Event event = new Event(getId(), Masks.NORM_ACTIVATION, getId());
        parameters.put("assigneeId",assignee.getRoleInstanceValue());
        event.setContent(parameters);
        context.fire(event);

        // return the NormExecution
        return new NormExecution(context, type, assignees, this);
    }

    public RoleReference getAssignee() {
        return assignee;
    }
    
    public ArrayList<RoleReference> getAssignees() {
        return assignees;
    }

    public NormExecution.Type getType() {
        return type;
    }

    public boolean needContext() {
        return false;
    }
}

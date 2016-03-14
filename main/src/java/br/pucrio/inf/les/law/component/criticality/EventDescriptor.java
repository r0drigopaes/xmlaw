package br.pucrio.inf.les.law.component.criticality;

import java.util.ArrayList;
import java.util.Map;

import br.pucrio.inf.les.law.component.message.RoleReference;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.execution.Trigger;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class EventDescriptor implements IDescriptor{

	private Id id;
    private String eventId;
    private int eventType;
	private double value;
    private ArrayList<RoleReference> assignees;
    
	public EventDescriptor(String id, String event_id, int event_type,double value){
		this.id = new Id(id);
		this.eventId = event_id;
		this.eventType = event_type;
		this.value = value;
		this.assignees = new ArrayList<RoleReference>();
	}
	
	public boolean isListening(int eventType, Id eventId){
		return (this.eventType==eventType &&
				eventId.getAsString().equals(this.eventId));
	}
	
	public void addAssignee(RoleDescriptor roleDescriptor, String roleInstance) throws LawException {
        if (roleDescriptor==null){
            throw new LawException("Assignee of criticality analysis with event-id = [" + eventId +"] and event-type = [" + eventType +"] cannot have a null role descriptor.",LawException.ROLE_NOT_DECLARED);
        }
        if (roleInstance==null){
            throw new LawException("Assignee of criticality analysis with event-id = [" + eventId +"] and event-type = [" + eventType +"] cannot have a null role instance.",LawException.ROLE_NOT_DECLARED);
        }
        RoleReference roleRef = new RoleReference();
        roleRef.setRoleInstance(roleInstance);
        roleRef.setRoleDescriptor(roleDescriptor);
        assignees.add(roleRef);
    }
	
	public ArrayList<RoleReference> getAssignees(){
		return assignees;
	}
	
	/**
	 * @return Returns the eventType.
	 */
	public int getEventType() {
		return eventType;
	}
	
	/**
	 * @return Returns the value.
	 */
	public double getValue() {
		return this.value;
	}

	public boolean needContext() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean shouldCreate(Context context, Map<String, Object> parameters) throws LawException {
		// TODO Auto-generated method stub
		return false;
	}

	public IExecution createExecution(Context context, Map<String, Object> parameters) throws LawException {
		// TODO Auto-generated method stub
		return null;
	}

	public Id getId() {
		return id;
	}

	public Trigger getTrigger() {
		// TODO Auto-generated method stub
		return null;
	}
}

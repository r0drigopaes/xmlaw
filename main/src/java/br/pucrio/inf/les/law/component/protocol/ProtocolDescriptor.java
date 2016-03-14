package br.pucrio.inf.les.law.component.protocol;

import br.pucrio.inf.les.law.component.message.MessageDescriptor;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.execution.Trigger;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProtocolDescriptor implements IDescriptor {
	
	private static final Log LOG = LogFactory.getLog(ProtocolDescriptor.class);

    private State initialState;

    private Id id;
    
    private Map<Id,MessageDescriptor> messageDescriptors;
    
    public ProtocolDescriptor(Id descriptorId){
        this.id = descriptorId;
        messageDescriptors = new HashMap<Id,MessageDescriptor>();        
    }

    public ProtocolDescriptor(Id descriptorId, State initialState)
            throws LawException {
        this.id = descriptorId;
        setInitialState(initialState);
        
    }
    
    public void setInitialState(State initialState) throws LawException{
        if (!initialState.isInitial()) {
            throw new LawException(
                    "Attempt to create a protocol passing a state as initial state that is not a initial state ",
                    LawException.INVALID_PROTOCOL_SPECIFICATION);
        }
        this.initialState = initialState;
    }

    public IExecution createExecution(Context context,
            Map<String, Object> parameters) throws LawException {
        return new ProtocolExecution(context, initialState,this);
    }

    public Id getId() {
        return id;
    }

    public Trigger getTrigger() {
        // There is no trigger for a protocol
        return null;
    }

    public boolean needContext() {
        return false;
    }

    public State getInitialState() {
        return initialState;
    }

    public boolean shouldCreate(Context context, Map<String, Object> parameters) throws LawException {
        return true;
    }
    
    public void addMessageDescriptor(MessageDescriptor messageDescriptor)
    {
    	if(messageDescriptors.containsKey(messageDescriptor.getId()))
    	{
    		LOG.warn("Attempted to insert an already inserted message descriptor " + messageDescriptor.getId());
    		return;
    	}
    	messageDescriptors.put(messageDescriptor.getId(),messageDescriptor);
    }

	public MessageDescriptor getMessageDescriptor(Id messageId) 
	{		
		return messageDescriptors.get(messageId);
	}
}

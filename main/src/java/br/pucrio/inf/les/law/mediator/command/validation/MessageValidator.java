package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.util.Constants;

public abstract class MessageValidator 
{
	private MessageValidator succesor;
	
	public enum Component
	{
		ORGANIZATION_EXECUTION,
		ORGANIZATION_DESCRIPTOR,
		ROLE_DESCRIPTOR,
		SCENE_DESCRIPTOR,
		SCENE_EXECUTION
	}
	
	public Message handleMessage(Message message, ExecutionManager manager, Map<Component,Object> components)
	{
		Message reply = handle(message,manager, components);
		if(reply == null && getSuccesor() != null)
		{
			return getSuccesor().handleMessage(message,manager, components);
		}
		else
		{
			return reply;
		}
	}
	
	protected abstract Message handle(Message message, ExecutionManager manager, Map<Component,Object> components);
	
	public Message createFailureReply(Message message, String failure)
	{
		Message reply = message.createReply(new AgentIdentification(Constants.MEDIATOR_ID.getValue()),
											Constants.MEDIATOR_NAME.getValue());
		reply.setPerformative(Message.FAILURE);
		reply.setContentValue(MessageContentConstants.KEY_TXT_MESSAGE,failure);
		return reply;		
	}

	public MessageValidator getSuccesor() {
		return succesor;
	}

	public void setSuccesor(MessageValidator succesor) {
		this.succesor = succesor;
	}

}

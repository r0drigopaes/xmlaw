package br.pucrio.inf.les.law.mediator.command;

import java.util.HashMap;
import java.util.Map;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;

public abstract class Command {
	private MessageValidator validatorChain;
	
	public Command()
	{
		setValidatorChain(createHandlerChain());
	}
	
	public Message execute(Message message, ExecutionManager executionManager) 
	{
		Map<MessageValidator.Component, Object> components = new HashMap<MessageValidator.Component, Object>();
		MessageValidator msgValidator = getValidatorChain();
		Message chainReply = null;
		if (msgValidator!=null){
			chainReply = msgValidator.handleMessage(message,executionManager, components);
		}
		if (chainReply == null) {
			return executeCommand(message, executionManager, components);
		} else {
			return chainReply;
		}
	}

	protected abstract Message executeCommand(Message message,
			ExecutionManager executionManager,
			Map<MessageValidator.Component, Object> components);

	public abstract String name();

	public MessageValidator getValidatorChain() {
		return validatorChain;
	}
	
	public void setValidatorChain(MessageValidator handlerChain) {
		this.validatorChain = handlerChain;
	}
	
	protected abstract MessageValidator createHandlerChain();
}

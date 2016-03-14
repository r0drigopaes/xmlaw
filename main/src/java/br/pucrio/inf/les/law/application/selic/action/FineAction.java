package br.pucrio.inf.les.law.application.selic.action;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.component.ElementTriggerDescriptor;
import br.pucrio.inf.les.law.component.action.ActionExecution;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.model.LawException;

public class FineAction extends ActionExecution {
	
	private static final Log LOG = LogFactory.getLog(FineAction.class);
	
	public FineAction(Context context, Map<String, Object> parameters,
			ElementTriggerDescriptor descriptor) {
		super(context, parameters, descriptor);
	}

	@Override
	public void execute(Map<String, Object> parameters) throws LawException {
		
		AgentIdentification agentReceiverId = null;
		try{
			agentReceiverId = (AgentIdentification) parameters.get("assigneeId");
		}
		catch(NullPointerException e){
			LOG.debug("FineAction could not get the assignee through the parameters.");
		}
		if (agentReceiverId!=null){
			LOG.info("Agent " + agentReceiverId.getName() + " has been fined.");
		}else{
			LOG.debug("FineAction could not get the assignee through the parameters.");
		}
		setExecutionState(ExecutionState.FINISHED);
	}

}

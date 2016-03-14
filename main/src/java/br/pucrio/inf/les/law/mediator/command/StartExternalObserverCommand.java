package br.pucrio.inf.les.law.mediator.command;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.criticality.ExternalObserver;
import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.scene.SceneDescriptor;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.mediator.Mediator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator.Component;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.util.Constants;

public class StartExternalObserverCommand extends Command {
	
	private static final Log LOG = LogFactory.getLog(StartExternalObserverCommand.class);
	
	private Mediator mediator;
	
	public StartExternalObserverCommand(Mediator mediator){
		this.mediator = mediator;
	}

	@Override
	protected Message executeCommand(Message message,
			ExecutionManager executionManager, Map<Component, Object> components) {

		String agentName = message.getContentValue("agentName");
		AgentIdentification aid = 
			new AgentIdentification(message.getContentValue("agentId"));
		String address = message.getContentValue("addressHost");
		String portNumber = message.getContentValue("portNumber");
		int eventType = Masks.getMask(message.getContentValue("eventType"));
		
		String sceneExecution = message.getContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID);
		
		boolean observerCreated = false;
		
		for(Map.Entry<String,ExecutionManager> entry : mediator.getExecutionManagers().entrySet())
		{
			ExecutionManager managerOrg = entry.getValue();
			OrganizationExecution organizationExecution = (OrganizationExecution)managerOrg.getExecutionById(new Id(entry.getKey()));
	    	
	    	if (organizationExecution.isAgentLoged(aid)){
	    		
	    		OrganizationDescriptor orgDesc = (OrganizationDescriptor) organizationExecution.getDescriptor();
	    		
	    		if (!sceneExecution.equals("")){
	    			//cria o observador no contexto da cena
		    			for (SceneDescriptor sceneDesc : orgDesc.getSceneDescriptors()) {
		    			List<IExecution> sceneExecutions = managerOrg
							.getExecutionsByDescriptorId(sceneDesc.getId());
		    		
			    		for (Iterator iter = sceneExecutions.iterator(); iter
								.hasNext();) {
			    			
							SceneExecution sceneExec = (SceneExecution) iter.next();
							
							if (sceneExec.getId().toString().equals(sceneExecution)){
							
								try {
									
									ExternalObserver ext = new
										ExternalObserver(aid,agentName,sceneExec.getContext(), sceneExec, eventType, address, portNumber);
									
									observerCreated = true;
								} catch (LawException e) {
									LOG.debug(e);
								}
							}
						}
					}
	    		}else{
	    			//cria o observador contexto da organização
	    			try {
						ExternalObserver ext = new
							ExternalObserver(aid,agentName,organizationExecution.getContext(),organizationExecution,eventType, address, portNumber);
						
						observerCreated = true;
					} catch (LawException e) {
						LOG.debug(e);
					}
	    		
	    		}
	    		
	    	}
	    }
		Message reply = message.createReply(new AgentIdentification(Constants.MEDIATOR_ID.getValue()),
											Constants.MEDIATOR_NAME.getValue());
		if (observerCreated){
			reply.setPerformative(Message.CONFIRM);			
		}else{
			reply.setPerformative(Message.REFUSE);			
		}
		LOG.debug("Mensagem de retorno do comando START_EXTERNAL_OBSERVER: " + reply);
		return reply;
	}

	@Override
	public String name() {
		return MessageContentConstants.CMD_START_EXTERNAL_COMMAND;
	}

	@Override
	protected MessageValidator createHandlerChain() {
		return null;
	}

}

package br.pucrio.inf.les.law.mediator.command;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.message.RoleReference;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.mediator.command.validation.EnterSceneValidator;
import br.pucrio.inf.les.law.mediator.command.validation.EnteredOrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.ExecutionManagerValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.mediator.command.validation.OrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.PerformRoleValidator;
import br.pucrio.inf.les.law.mediator.command.validation.RoleValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SceneExecutionValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SenderValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator.Component;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.Constants;
import br.pucrio.inf.les.law.util.LawResourceBundle;


/**
 * Logs the agent inside a scene Execution. The command expects for the
 * sceneExecutionId, the organizationExecutionId, the command attribute and the
 * role performed into the scene. In case of failure or success, an text message
 * will be included into the field txtMessage <br>
 * Example: an agent wants to enter into the scene execution 2198 of
 * organization 73 with the role sender, so it must send the following message:
 * <br>
 * 
 * key: command value: enterScene <br>
 * key: orgExecutionId value: 73 <br>
 * key: sceneExecutionId value: 2198 <br>
 * key: role value: sender
 * 
 * A possive response is: <br>
 * 
 * key: orgExecutionId value: 73 <br>
 * key: sceneExecutionId value: 2198 <br>
 * key: command value: enterScene <br>
 * 
 * @see br.pucrio.inf.les.law.communication.MessageContentConstants
 * @author lfrodrigues
 * 
 */
public class EnterInSceneCommand extends Command {
	
	private static final Log LOG = LogFactory.getLog(EnterInSceneCommand.class);
	
	protected Message executeCommand(Message message,
			ExecutionManager executionManager, Map<Component, Object> components) {

		OrganizationExecution orgExecution = (OrganizationExecution) components
				.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		SceneExecution sceneExecution = (SceneExecution) components
				.get(MessageValidator.Component.SCENE_EXECUTION);
		RoleDescriptor roleDescriptor = (RoleDescriptor) components
				.get(MessageValidator.Component.ROLE_DESCRIPTOR);

		AgentIdentification aid = message.getSender();

		
		if (!sceneExecution.getLoggedAgents().containsKey(aid)) {
			//firing role_activation event...
			RoleReference roleRef = new RoleReference();
			roleRef.setRoleDescriptor(roleDescriptor);
			roleRef.setRoleInstanceValue(aid);
			Event roleActivationEvent = new Event(roleDescriptor.getId(), Masks.ROLE_ACTIVATION, new Id());
			roleActivationEvent.addEventContent(Event.ROLEREFERENCE, roleRef);
			LOG.info("Disparando evento role_activation para o agente [" + aid + "] na cena [" + sceneExecution.getId() + "] para o papel [" + roleDescriptor.getId() + "].");
			sceneExecution.getContext().fireEventInContext(roleActivationEvent);
			//end firing.
		}
		
		//note: the scene entrance permission was checked in the
		// SceneExecutionValidator
		sceneExecution.forceSceneEntrance(aid, roleDescriptor);
		

		Message reply = message.createReply(new AgentIdentification(Constants.MEDIATOR_ID.getValue()),
											Constants.MEDIATOR_NAME.getValue());
		reply.setPerformative(Message.INFORM);
		reply.setContentValue(MessageContentConstants.KEY_COMMAND, this.name());
		reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, orgExecution.getId().toString());
		reply.setContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID, sceneExecution.getId().getAsString());
		reply.setContentValue(MessageContentConstants.KEY_TXT_MESSAGE, LawResourceBundle.getInstance()
				.format("msg.info.entered_scene",
						sceneExecution.getDescriptor().getId().toString(),
						sceneExecution.getId().toString(),
						roleDescriptor.getId().toString()));
		return reply;
	}

	public String name() {
		return MessageContentConstants.CMD_ENTER_SCENE;
	}

	protected MessageValidator createHandlerChain() {
		MessageValidator execMan = new ExecutionManagerValidator();
		MessageValidator sender = new SenderValidator();
		MessageValidator org = new OrganizationValidator();
		MessageValidator enterOrg = new EnteredOrganizationValidator();
		MessageValidator role = new RoleValidator();
		MessageValidator sceneExec = new SceneExecutionValidator();
		MessageValidator enterScene = new EnterSceneValidator();

		execMan.setSuccesor(sender);
		sender.setSuccesor(org);
		org.setSuccesor(enterOrg);
		enterOrg.setSuccesor(role);
		role.setSuccesor(sceneExec);
		sceneExec.setSuccesor(enterScene);

		return execMan;
	}

}
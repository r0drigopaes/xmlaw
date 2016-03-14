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
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.mediator.command.validation.EnteredOrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.ExecutionManagerValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.mediator.command.validation.OrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.RoleValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SenderValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator.Component;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.Constants;
import br.pucrio.inf.les.law.util.LawResourceBundle;

/**
 * Allows the sender agent to perform the role requested. The command expects
 * for the organization execution id and the role defined into the organization.
 * The agent must have entered in the organization previouslly. In case of
 * failure or success the field txtMessage will be set with an text message <br>
 * 
 * example: an agent wants to perform the role foo in the organization fubar
 * (execution id 34), so it must send a message containing:<br>
 * 
 * key: command value: performRole <br>
 * key: orgExecutionId value: 34 <br>
 * key: role value: foo<br>
 * 
 * The answer will be:<br>
 * 
 * key: command value: performRole <br>
 * key: orgExecutiond value: 34 <br>
 * key: role value: foo<br>
 * 
 * @see br.pucrio.inf.les.law.communication.MessageContentConstants
 * @author lfrodrigues
 * 
 */
public class PerformRoleCommand extends Command {
	
	private static final Log LOG = LogFactory.getLog(PerformRoleCommand.class);
	
	protected Message executeCommand(Message message,
			ExecutionManager executionManager, Map<Component, Object> components) {
		
		
		OrganizationExecution organizationExecution = 
			(OrganizationExecution) components.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		
		RoleDescriptor roleDescriptor = 
			(RoleDescriptor) components.get(MessageValidator.Component.ROLE_DESCRIPTOR);
		
		AgentIdentification senderId = message.getSender();

		organizationExecution.addRoleToAgent(senderId, roleDescriptor);
		
		//firing role_activation event...
		RoleReference roleRef = new RoleReference();
		roleRef.setRoleDescriptor(roleDescriptor);
		roleRef.setRoleInstanceValue(senderId);
		
		Event roleActivationEvent = new Event(roleDescriptor.getId(), Masks.ROLE_ACTIVATION, new Id());
		roleActivationEvent.addEventContent(Event.ROLEREFERENCE, roleRef);
		LOG.info("Disparando evento role_activation para o agente [" + senderId + "] na organização [" + organizationExecution.getId() + "] para o papel [" + roleDescriptor.getId() + "].");
		organizationExecution.getContext().fire(roleActivationEvent);
		//end firing.

		Message reply = message.createReply(new AgentIdentification(Constants.MEDIATOR_ID.getValue()),
											Constants.MEDIATOR_NAME.getValue());
		reply.setPerformative(Message.INFORM);
		reply.setContentValue(MessageContentConstants.KEY_COMMAND, this.name());
		reply.setContentValue(MessageContentConstants.KEY_ROLE_NAME, roleDescriptor.getId().getAsString());
		reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, organizationExecution.getId()
				.getAsString());
		reply.setContentValue(MessageContentConstants.KEY_TXT_MESSAGE, LawResourceBundle.getInstance()
				.format("msg.info.perform_role", roleDescriptor.getId().toString()));
		return reply;
	}

	public String name() {
		return MessageContentConstants.CMD_PERFORM_ROLE;
	}

	protected MessageValidator createHandlerChain() {
		MessageValidator execMan = new ExecutionManagerValidator();
		MessageValidator sender = new SenderValidator();
		MessageValidator org = new OrganizationValidator();
		MessageValidator enterOrg = new EnteredOrganizationValidator();
		MessageValidator role = new RoleValidator();

		execMan.setSuccesor(sender);
		sender.setSuccesor(org);
		org.setSuccesor(enterOrg);
		enterOrg.setSuccesor(role);

		return execMan;
	}
}

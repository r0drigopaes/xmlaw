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
import br.pucrio.inf.les.law.mediator.command.validation.SenderValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator.Component;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.Constants;
import br.pucrio.inf.les.law.util.LawResourceBundle;

/**
 * Removes an agent from an organization. The command expects for organization
 * execution id and reply with the organization execution id and the command
 * name<br>
 * In case of failure, the command will reply a message with the field
 * txtMessage containig the failure reason. Otherwise the field will contain an
 * success message<br>
 * 
 * example: An agent wants to leave the fubar organization so it must send a
 * message containg <br>
 * 
 * key: command value: quitOrg<br>
 * key: organization value: fubar<br>
 * 
 * the command will reply<br>
 * 
 * key: command value: quitOrg<br>
 * key: organizationExecution Id value: 34<br>
 * 
 * @see br.pucrio.inf.les.law.communication.MessageContentConstants
 * @author lfrodrigues
 * 
 */
public class QuitOrganizationCommand extends Command {
	
	private static final Log LOG = LogFactory.getLog(QuitOrganizationCommand.class);

	protected Message executeCommand(Message message,
			ExecutionManager executionManager, Map<Component, Object> components) {
		
		OrganizationExecution organizationExecution = (OrganizationExecution) components
		.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		
		RoleDescriptor roleDescriptor = 
			(RoleDescriptor) components.get(MessageValidator.Component.ROLE_DESCRIPTOR);
		AgentIdentification senderId = message.getSender();
		//firing role_deactivation event...
		RoleReference roleRef = new RoleReference();
		roleRef.setRoleDescriptor(roleDescriptor);
		roleRef.setRoleInstanceValue(senderId);
		
		Event roleDeactivationEvent = new Event(roleDescriptor.getId(), Masks.ROLE_DEACTIVATION, new Id());
		roleDeactivationEvent.addEventContent(Event.ROLEREFERENCE, roleRef);
		if (LOG.isDebugEnabled()){
			LOG.debug("Disparando evento role_deactivation para o agente [" + senderId + "] na organização [" + organizationExecution + "] para o papel [" + roleDescriptor + "].");
		}
		organizationExecution.getContext().fire(roleDeactivationEvent);
		//end firing.
		
		organizationExecution.removeLogedAgent(message.getSender());

		Message reply = message.createReply(new AgentIdentification(Constants.MEDIATOR_ID.getValue()),
											Constants.MEDIATOR_NAME.getValue());
		reply.setPerformative(Message.INFORM);
		reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, organizationExecution.getId()
				.getAsString());
		reply.setContentValue(MessageContentConstants.KEY_COMMAND, this.name());
		reply.setContentValue(MessageContentConstants.KEY_TXT_MESSAGE, LawResourceBundle.getInstance()
				.format("msg.info.quit",
						organizationExecution.getDescriptor().getId().toString(),
						organizationExecution.getId().toString()));

		return reply;
	}

	protected MessageValidator createHandlerChain() {
		MessageValidator execMan = new ExecutionManagerValidator();
		MessageValidator sender = new SenderValidator();
		MessageValidator org = new OrganizationValidator();

		execMan.setSuccesor(sender);
		sender.setSuccesor(org);
		org.setSuccesor(new EnteredOrganizationValidator());

		return execMan;
	}

	public String name() {
		return MessageContentConstants.CMD_QUIT_ORGANIZATION;
	}

}

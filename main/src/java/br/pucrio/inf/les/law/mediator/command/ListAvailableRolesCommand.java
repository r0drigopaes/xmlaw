package br.pucrio.inf.les.law.mediator.command;

import java.util.Map;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.mediator.command.validation.ExecutionManagerValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.mediator.command.validation.OrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SenderValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator.Component;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.Constants;
import br.pucrio.inf.les.law.util.LawResourceBundle;

/**
 * List the avaiables roles declared inside a organization. The command expects
 * for the organization execution id.In case of failure or success, an text
 * message will be put into the filed txtMessage<br>
 * example: an agent wants to know the roles declared inside the fubar
 * organization (execution id 72), it must send the following message: <br>
 * key: command value: listRoles <br>
 * key: orgExecutionId value: fubar <br>
 * The response massage will contain the roles declared into fubar and the total
 * number of roles<br>
 * key: command value: listRoles <br>
 * key: organization value: 72 <br>
 * key: role1 value: foo <br>
 * key: role2 value: bar <br>
 * key: total value: 2 <br>
 * 
 * @see br.pucrio.inf.les.law.communication.MessageContentConstants
 * @author lfrodrigues
 */
public class ListAvailableRolesCommand extends Command
{
	protected Message executeCommand(Message message, ExecutionManager executionManager,
			Map<Component, Object> components)
	{

		OrganizationExecution organizationExecution = (OrganizationExecution) components
				.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		OrganizationDescriptor organizationDescriptor = (OrganizationDescriptor) components
				.get(MessageValidator.Component.ORGANIZATION_DESCRIPTOR);

		Message reply = message.createReply(new AgentIdentification(Constants.MEDIATOR_ID.getValue()),
											Constants.MEDIATOR_NAME.getValue());
		reply.setPerformative(Message.INFORM);
		reply.setContentValue(MessageContentConstants.KEY_COMMAND, this.name());

		int i = 0;
		for (Id roleDescriptorId : organizationDescriptor.getRoleDescriptors().keySet())
		{
			i++;
			reply.setContentValue(MessageContentConstants.KEY_ROLE_NAME + i, roleDescriptorId.getAsString());
		}

		reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, organizationExecution.getId()
				.getAsString());
		reply.setContentValue(MessageContentConstants.KEY_TOTAL, "" + i);
		reply.setContentValue(MessageContentConstants.KEY_TXT_MESSAGE, LawResourceBundle.getInstance()
				.format("msg.info.list_roles",
						organizationDescriptor.getId().toString(),
						organizationExecution.getId().toString()));

		return reply;
	}

	public String name()
	{
		return MessageContentConstants.CMD_LIST_ROLES;
	}

	protected MessageValidator createHandlerChain()
	{
		MessageValidator execMan = new ExecutionManagerValidator();
		MessageValidator sender = new SenderValidator();
		MessageValidator org = new OrganizationValidator();

		execMan.setSuccesor(sender);
		sender.setSuccesor(org);

		return execMan;
	}

}

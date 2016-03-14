package br.pucrio.inf.les.law.mediator.command;

import java.util.Map;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.mediator.command.validation.ExecutionManagerValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.mediator.command.validation.OrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SenderValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator.Component;
import br.pucrio.inf.les.law.util.Constants;
import br.pucrio.inf.les.law.util.LawResourceBundle;

/**
 * Logs an agent into an organization. The command expects for the execution id,
 * it replies with the organization execution id, the organization name and this
 * command identification. In case of failure, the command will reply a message
 * with the field txtMessage containig the failure reason. Otherwise the content
 * will be a positive message<br>
 * example: An agent wants to enter in the fubar organization so it must send a
 * message containg the organization execution id.<br>
 * key: command value: enterOrg <br>
 * key: orgExecutionId value: 72 <br>
 * the command will reply <br>
 * key: command value: enterOrg <br>
 * key: orgExecutionId value: 72 <br>
 * key: organization value: fubar <br>
 * 
 * @see br.pucrio.inf.les.law.communication.MessageContentConstants
 * @author lfrodrigues
 */
public class EnterInOrganizationCommand extends Command
{
	protected Message executeCommand(Message message, ExecutionManager executionManager,
			Map<Component, Object> components)
	{

		OrganizationExecution organizationExecution = (OrganizationExecution) components
				.get(MessageValidator.Component.ORGANIZATION_EXECUTION);

		organizationExecution.addLogedAgent(message.getSender());

		Message reply = message.createReply(new AgentIdentification(Constants.MEDIATOR_ID.getValue()),
											Constants.MEDIATOR_NAME.getValue());
		reply.setPerformative(Message.INFORM);
		reply.setContentValue(MessageContentConstants.KEY_COMMAND, this.name());
		reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, organizationExecution.getId()
				.getAsString());
		reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_NAME, organizationExecution.getDescriptor()
				.getId().getAsString());
		reply.setContentValue(MessageContentConstants.KEY_TXT_MESSAGE, LawResourceBundle.getInstance()
				.format("msg.info.entered_org",
						organizationExecution.getDescriptor().getId().toString(),
						organizationExecution.getId().toString()));
		return reply;
	}

	public String name()
	{
		return MessageContentConstants.CMD_ENTER_ORGANIZATION;
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

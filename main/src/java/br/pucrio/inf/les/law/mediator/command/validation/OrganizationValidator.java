package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class OrganizationValidator extends MessageValidator 
{

	@Override
	protected Message handle(Message message, ExecutionManager manager,
			Map<MessageValidator.Component, Object> components) {
		String orgExecStr = message
				.getContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID);
		// organization value is not informed
		if (orgExecStr == null) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.no_parameter",
							MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID));
		}

		IExecution organizationExec = manager.getExecutionById(new Id(orgExecStr));
		// organization was not declared
		if (organizationExec == null) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.not_declared_org", orgExecStr));
		}

		components.put(MessageValidator.Component.ORGANIZATION_EXECUTION,
				organizationExec);
		components.put(MessageValidator.Component.ORGANIZATION_DESCRIPTOR,
				organizationExec.getDescriptor());

		return null;
	}

}

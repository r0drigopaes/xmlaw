package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.util.LawResourceBundle;

import java.util.Map;

public class EnteredOrganizationValidator extends MessageValidator {

	@Override
	protected Message handle(Message message, ExecutionManager manager,
			Map<Component, Object> components) {
		OrganizationExecution organization = (OrganizationExecution) components
				.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		AgentIdentification senderId = message.getSender();

		if (!organization.isAgentLoged(senderId)) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.not_entered_org",
							organization.getDescriptor().getId()));
		}

		return null;
	}
}

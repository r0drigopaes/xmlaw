package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class PerformRoleValidator extends MessageValidator {

	@Override
	protected Message handle(Message message, ExecutionManager manager,
			Map<Component, Object> components) {
		OrganizationExecution organizationExecution = (OrganizationExecution) components
				.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		RoleDescriptor role = (RoleDescriptor) components
				.get(MessageValidator.Component.ROLE_DESCRIPTOR);
		AgentIdentification aid = message.getSender();

		// agent does not perform the role
		if (organizationExecution.doesAgentPerformRole(aid, role) == false) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.doesnt_perform_role",
							role.getId().getAsString()));
		}

		return null;
	}
}

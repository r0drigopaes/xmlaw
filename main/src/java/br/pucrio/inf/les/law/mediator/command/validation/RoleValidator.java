package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class RoleValidator extends MessageValidator {
	@Override
	protected Message handle(Message message, ExecutionManager manager,
			Map<MessageValidator.Component, Object> components) {
		String role = message.getContentValue(MessageContentConstants.KEY_ROLE_NAME);
		// role value is not informed
		if (role == null) {
			return createFailureReply(message,
					LawResourceBundle.getInstance().format("msg.no_parameter",
							MessageContentConstants.KEY_ROLE_NAME));
		}

		OrganizationDescriptor organizationDescriptor = (OrganizationDescriptor) components
				.get(MessageValidator.Component.ORGANIZATION_DESCRIPTOR);

		RoleDescriptor roleDescriptor = organizationDescriptor
				.getRoleDescriptors().get(new Id(role));
		// role was not declared
		if (roleDescriptor == null) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.not_declared_role", role,
							organizationDescriptor.getId()));
		}

		components.put(MessageValidator.Component.ROLE_DESCRIPTOR,
				roleDescriptor);

		return null;
	}

}

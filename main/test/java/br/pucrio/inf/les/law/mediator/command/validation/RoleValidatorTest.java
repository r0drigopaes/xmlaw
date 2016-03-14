package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class RoleValidatorTest extends MessageValidatorScenario {
	public void setUp() throws Exception {
		super.setUp();
		handler = new RoleValidator();

		components.put(MessageValidator.Component.ORGANIZATION_DESCRIPTOR,
				table.get(new Id("auction")));
	}

public void testRoleNotInformed()
	{
		String expected = LawResourceBundle.getInstance().format("msg.no_parameter",MessageContentConstants.KEY_ROLE_NAME);
		sendMessageAndAssertFailure(expected);
	}	public void testRoleNotDeclared() {
		message.setContentValue(MessageContentConstants.KEY_ROLE_NAME,
				"no declared role");
		String expected = LawResourceBundle.getInstance().format(
				"msg.not_declared_role", "no declared role",
				orgExecution.getDescriptor().getId());
		sendMessageAndAssertFailure(expected);
	}

	public void testRoleValidator() {
		message.setContentValue(MessageContentConstants.KEY_ROLE_NAME, "seller");
		Message response = handler.handle(message, manager, components);

		assertNull(response);
		assertEquals(
				components.get(MessageValidator.Component.ROLE_DESCRIPTOR),
				new RoleDescriptor(new Id("seller")));
	}

}

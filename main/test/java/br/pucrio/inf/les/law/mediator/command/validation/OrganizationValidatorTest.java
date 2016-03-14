package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class OrganizationValidatorTest extends MessageValidatorScenario {
	public void setUp() throws Exception {
		super.setUp();
		handler = new OrganizationValidator();
	}

	public void testNoOrganizationInformed() {
		String expected = LawResourceBundle.getInstance().format(
				"msg.no_parameter", MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID);
		sendMessageAndAssertFailure(expected);
	}

	public void testNoOrganizationDeclared() {
		message.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID,
				"no declared org");
		String expected = LawResourceBundle.getInstance().format(
				"msg.not_declared_org", "no declared org");
		sendMessageAndAssertFailure(expected);
	}

	public void testOrganizationValidator() {
		message.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID,
				orgExecution.getId().toString());
		Message response = handler.handle(message, manager, components);

		assertNull(response);
		OrganizationDescriptor orgDescriptor = (OrganizationDescriptor) components
				.get(MessageValidator.Component.ORGANIZATION_DESCRIPTOR);
		assertEquals(orgDescriptor.getId(), new Id("auction"));

		OrganizationExecution orgExecution = (OrganizationExecution) components
				.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		assertSame(orgExecution, super.orgExecution);
	}

}

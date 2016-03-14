package br.pucrio.inf.les.law.mediator.command;

import br.pucrio.inf.les.law.XMLawScenario;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class PerformRoleCommandTest extends XMLawScenario
{
	private PerformRoleCommand	performRoleCommand;

	public void setUp() throws Exception
	{
		super.setUp();
		performRoleCommand = new PerformRoleCommand();
	}

	public void testName()
	{
		assertEquals(MessageContentConstants.CMD_PERFORM_ROLE, performRoleCommand.name());
	}

	public void testPerformRoleCommand()
	{
		RoleDescriptor role = new RoleDescriptor(new Id("seller"));
		components.put(MessageValidator.Component.ROLE_DESCRIPTOR, role);

		orgExecution.addLogedAgent(senderAgent);

		Message reply = performRoleCommand.executeCommand(requestMessage, null, components);

		assertNotNull(reply);
		assertEquals(Message.INFORM, reply.getPerformative());
		assertEquals(MessageContentConstants.CMD_PERFORM_ROLE, reply
				.getContentValue(MessageContentConstants.KEY_COMMAND));

		assertEquals(role.getId().toString(), reply.getContentValue(MessageContentConstants.KEY_ROLE_NAME));
		assertEquals(orgExecution.getId().toString(), reply
				.getContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID));
		assertTrue(orgExecution.doesAgentPerformRole(senderAgent, role));

		String expectedTxtMsg = LawResourceBundle.getInstance()
				.format("msg.info.perform_role", role.getId().toString());
		assertEquals(expectedTxtMsg, reply.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));

	}

}

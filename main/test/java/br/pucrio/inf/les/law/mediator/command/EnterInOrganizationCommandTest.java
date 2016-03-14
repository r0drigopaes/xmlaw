package br.pucrio.inf.les.law.mediator.command;

import br.pucrio.inf.les.law.XMLawScenario;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class EnterInOrganizationCommandTest extends XMLawScenario
{

	private Command	enterOrgCommand;

	public void setUp() throws Exception
	{
		super.setUp();
		enterOrgCommand = new EnterInOrganizationCommand();
	}

	public void testName()
	{
		assertEquals(enterOrgCommand.name(), MessageContentConstants.CMD_ENTER_ORGANIZATION);
	}

	public void testEnterInOrganizationCommand() throws LawException
	{
		Message reply = enterOrgCommand.executeCommand(requestMessage, null, components);

		assertNotNull(reply);
		assertEquals(Message.INFORM, reply.getPerformative());
		assertEquals(MessageContentConstants.CMD_ENTER_ORGANIZATION, reply
				.getContentValue(MessageContentConstants.KEY_COMMAND));
		assertEquals(orgExecution.getId().toString(), reply
				.getContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID));
		assertEquals(orgExecution.getDescriptor().getId().toString(), reply
				.getContentValue(MessageContentConstants.KEY_ORGANIZATION_NAME));

		String expectedTxtMsg = LawResourceBundle.getInstance()
				.format("msg.info.entered_org",
						orgExecution.getDescriptor().getId().toString(),
						orgExecution.getId().toString());
		assertEquals(expectedTxtMsg, reply.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));

		assertTrue(orgExecution.isAgentLoged(senderAgent));
	}
}
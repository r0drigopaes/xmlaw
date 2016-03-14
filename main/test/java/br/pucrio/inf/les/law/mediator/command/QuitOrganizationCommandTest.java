package br.pucrio.inf.les.law.mediator.command;

import br.pucrio.inf.les.law.XMLawScenario;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class QuitOrganizationCommandTest extends XMLawScenario
{
	private QuitOrganizationCommand	quitOrgCommand;

	public void setUp() throws Exception
	{
		super.setUp();
		quitOrgCommand = new QuitOrganizationCommand();
	}

	public void testName()
	{
		assertEquals(MessageContentConstants.CMD_QUIT_ORGANIZATION, quitOrgCommand.name());
	}

	public void testQuitOrganizationCommand()
	{
		orgExecution.addLogedAgent(senderAgent);

		Message reply = quitOrgCommand.executeCommand(requestMessage, null, components);

		assertNotNull(reply);
		assertEquals(Message.INFORM, reply.getPerformative());

		assertEquals(MessageContentConstants.CMD_QUIT_ORGANIZATION, reply
				.getContentValue(MessageContentConstants.KEY_COMMAND));

		assertFalse(orgExecution.isAgentLoged(senderAgent));

		String expectedTxtMsg = LawResourceBundle.getInstance()
				.format("msg.info.quit",
						orgExecution.getDescriptor().getId().toString(),
						orgExecution.getId().toString());
		assertEquals(expectedTxtMsg,reply.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));
	}

}

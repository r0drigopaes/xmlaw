package br.pucrio.inf.les.law.mediator.command;

import br.pucrio.inf.les.law.XMLawScenario;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class EnterInSceneCommandTest extends XMLawScenario
{
	private Command	enterSceneCommand;

	public void setUp() throws Exception
	{
		super.setUp();
		enterSceneCommand = new EnterInSceneCommand();
	}

	public void testName()
	{
		assertEquals(enterSceneCommand.name(), MessageContentConstants.CMD_ENTER_SCENE);
	}

	public void testEnterSceneCommand()
	{
		RoleDescriptor role = new RoleDescriptor(new Id("seller"));
		components.put(MessageValidator.Component.ROLE_DESCRIPTOR, role);

		Message reply = enterSceneCommand.executeCommand(requestMessage, null, components);

		assertNotNull(reply);
		assertEquals(Message.INFORM, reply.getPerformative());
		assertEquals(MessageContentConstants.CMD_ENTER_SCENE, reply
				.getContentValue(MessageContentConstants.KEY_COMMAND));
		assertEquals(orgExecution.getId().toString(), reply
				.getContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID));

		assertEquals(sceneExecution.getId().toString(), reply
				.getContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID));
		String expectedTxtMsg = LawResourceBundle.getInstance().format(	"msg.info.entered_scene",
																		sceneExecution.getDescriptor().getId()
																				.toString(),
																		sceneExecution.getId().toString(),
																		role.getId().toString());
		assertEquals(expectedTxtMsg, reply.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));

		assertTrue(sceneExecution.getLoggedAgents().containsKey(senderAgent));
		assertSame(sceneExecution.getLoggedAgents().get(senderAgent), role);
	}
}

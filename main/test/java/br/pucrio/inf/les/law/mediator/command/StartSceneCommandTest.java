package br.pucrio.inf.les.law.mediator.command;

import br.pucrio.inf.les.law.XMLawScenario;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class StartSceneCommandTest extends XMLawScenario
{

	private StartSceneCommand	startSceneCommand;

	public void setUp() throws Exception
	{
		super.setUp();
		startSceneCommand = new StartSceneCommand();
	}

	public void testName()
	{
		assertEquals(startSceneCommand.name(), MessageContentConstants.CMD_START_SCENE);
	}

	public void testStartSceneCommand()
	{
		Message reply = startSceneCommand.executeCommand(requestMessage, manager, components);

		assertNotNull(reply);
		assertEquals(Message.INFORM, reply.getPerformative());
		assertEquals(MessageContentConstants.CMD_START_SCENE, reply
				.getContentValue(MessageContentConstants.KEY_COMMAND));

		assertEquals(orgExecution.getId().toString(), reply
				.getContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID));
		assertEquals("game", reply.getContentValue(MessageContentConstants.KEY_SCENE_NAME));

		String newSceneExecutionId = reply.getContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID);
		assertNotNull(newSceneExecutionId);

		SceneExecution newSceneExecution = (SceneExecution) manager.getExecutionById(new Id(newSceneExecutionId));
		assertNotNull(newSceneExecution);

		assertSame(sceneExecution.getDescriptor(), newSceneExecution.getDescriptor());

		String expectedTxtMessage = LawResourceBundle.getInstance().format(	"msg.info.start_scene",
																			newSceneExecution.getDescriptor().getId()
																					.toString(),
																			newSceneExecutionId,
																			orgExecution.getDescriptor().getId()
																					.toString(),
																			orgExecution.getId().toString());
		assertEquals(expectedTxtMessage,reply.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));
	}
}

package br.pucrio.inf.les.law.mediator.command;

import br.pucrio.inf.les.law.XMLawScenario;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.scene.SceneDescriptor;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class ListRunningScenesCommandTest extends XMLawScenario
{
	private ListRunningScenesCommand	listRunningScenesCommand;

	public void setUp() throws Exception
	{
		super.setUp();
		listRunningScenesCommand = new ListRunningScenesCommand();
	}

	public void testName()
	{
		assertEquals(MessageContentConstants.CMD_LIST_RUNNING_SCENES, listRunningScenesCommand.name());
	}

	public void testListRunningScenesCommand() throws Exception
	{
		SceneExecution sceneExec2 = (SceneExecution) manager.newInstance(orgExecution.getId(), new Id("game"), null);

		Message reply = listRunningScenesCommand.executeCommand(requestMessage, manager, components);

		assertNotNull(reply);
		assertEquals(Message.INFORM, reply.getPerformative());
		assertEquals(MessageContentConstants.CMD_LIST_RUNNING_SCENES, reply
				.getContentValue(MessageContentConstants.KEY_COMMAND));

		assertEquals(orgExecution.getId().toString(), reply
				.getContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID));

		String expectedTxtMsg = LawResourceBundle.getInstance()
				.format("msg.info.list_scenes",
						orgExecution.getDescriptor().getId().toString(),
						orgExecution.getId().toString());
		assertEquals(expectedTxtMsg, reply.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));

		assertEquals("" + 2, reply.getContentValue(MessageContentConstants.KEY_TOTAL));

		SceneDescriptor sceneDesc = (SceneDescriptor) components.get(MessageValidator.Component.SCENE_DESCRIPTOR);

		String scene1 = reply.getContentValue(MessageContentConstants.KEY_SCENE_NAME + 1);
		String scene2 = reply.getContentValue(MessageContentConstants.KEY_SCENE_NAME + 2);

		assertEquals(sceneDesc.getId().toString(), scene1);
		assertEquals(sceneDesc.getId().toString(), scene2);

		String scene1ExecStr = reply.getContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID + 1);
		String scene2ExecStr = reply.getContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID + 2);

		assertFalse(scene1ExecStr.equals(scene2ExecStr));
		assertTrue(scene1ExecStr.equals(sceneExecution.getId().toString()) || scene1ExecStr.equals(sceneExec2.getId()
				.toString()));
		assertTrue(scene2ExecStr.equals(sceneExecution.getId().toString()) || scene2ExecStr.equals(sceneExec2.getId()
				.toString()));
	}

}

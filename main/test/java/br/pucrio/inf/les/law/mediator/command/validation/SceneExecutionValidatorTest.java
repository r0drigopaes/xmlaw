package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class SceneExecutionValidatorTest extends MessageValidatorScenario {
	public void setUp() throws Exception {
		super.setUp();
		handler = new SceneExecutionValidator();
	}

	public void testSceneExecutionNotInformed() {
		String expected = LawResourceBundle.getInstance().format(
				"msg.no_parameter", MessageContentConstants.KEY_SCENE_EXECUTION_ID);
		sendMessageAndAssertFailure(expected);
	}

	public void testSceneIdIsNotExecuting() {
		message.setContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID,
				"just to invalidate");
		String expected = LawResourceBundle.getInstance().format(
				"msg.doesnt_exist_scene", "just to invalidate");
		sendMessageAndAssertFailure(expected);
	}

	public void testSceneExecution() {
		message.setContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID,
				sceneExecution.getId().toString());

		Message response = handler.handle(message, manager, components);

		assertNull(response);
		assertSame(components.get(MessageValidator.Component.SCENE_EXECUTION),
				sceneExecution);
	}

}

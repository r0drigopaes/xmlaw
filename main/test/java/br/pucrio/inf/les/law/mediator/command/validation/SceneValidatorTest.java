package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.scene.SceneDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class SceneValidatorTest extends MessageValidatorScenario {
	public void setUp() throws Exception {
		super.setUp();
		handler = new SceneValidator();

		components.put(MessageValidator.Component.ORGANIZATION_DESCRIPTOR,
				table.get(new Id("auction")));
	}

	public void testNoSceneInformed() {
		String expected = LawResourceBundle.getInstance().format(
				"msg.no_parameter", MessageContentConstants.KEY_SCENE_NAME);
		sendMessageAndAssertFailure(expected);
	}

	public void testNoSceneDeclared() {
		message.setContentValue(MessageContentConstants.KEY_SCENE_NAME,
				"no declared scene");
		String expected = LawResourceBundle.getInstance().format(
				"msg.not_declared_scene", "no declared scene");

		sendMessageAndAssertFailure(expected);
	}

	public void testSceneValidator() {
		message.setContentValue(MessageContentConstants.KEY_SCENE_NAME, "game");
		Message response = handler.handle(message, manager, components);

		assertNull(response);

		SceneDescriptor sceneDescriptor = (SceneDescriptor) components
				.get(MessageValidator.Component.SCENE_DESCRIPTOR);
		assertEquals(sceneDescriptor.getId(), new Id("game"));
	}

}

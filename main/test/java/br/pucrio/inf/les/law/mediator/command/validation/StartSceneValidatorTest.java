package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class StartSceneValidatorTest extends MessageValidatorScenario {

	public void setUp() throws Exception {
		super.setUp();
		handler = new StartSceneValidator();

		components.put(MessageValidator.Component.ORGANIZATION_EXECUTION,
				orgExecution);
		components.put(MessageValidator.Component.SCENE_DESCRIPTOR, table
				.get(new Id("game")));
	}

	public void testAgentCannotStartScene() {
		// the default sender role is senderRole, which is not allowed to start
		// a scence
		String expected = LawResourceBundle.getInstance().format(
				"msg.cannot_create_scene",
				sceneExecution.getDescriptor().getId());
		sendMessageAndAssertFailure(expected);
	}

	public void testAgentCanStartScene() {
		// the seller role can start a scene
		orgExecution.addLogedAgent(sender);
		orgExecution.addRoleToAgent(sender,
				new RoleDescriptor(new Id("seller")));

		Message response = handler.handle(message, manager, components);

		assertNull(response);
	}

}

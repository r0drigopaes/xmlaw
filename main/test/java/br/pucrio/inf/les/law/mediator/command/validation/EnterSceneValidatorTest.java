package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class EnterSceneValidatorTest extends MessageValidatorScenario {

	public void setUp() throws Exception {
		super.setUp();
		handler = new EnterSceneValidator();

		components.put(MessageValidator.Component.ORGANIZATION_EXECUTION,
				orgExecution);
		components.put(MessageValidator.Component.SCENE_EXECUTION,
				sceneExecution);
	}

	public void testNoPermissionToEnter() {
		// the observer role is not performed by the sender agent neither
		// allowed to enter the scene
		components.put(MessageValidator.Component.ROLE_DESCRIPTOR,
				new RoleDescriptor(new Id("observer")));

		String expected = LawResourceBundle.getInstance().format(
				"msg.cannot_enter_scene", sceneExecution.getId());
		sendMessageAndAssertFailure(expected);
	}

	public void testPermissionToEnter() {
		components.put(MessageValidator.Component.ROLE_DESCRIPTOR,
				new RoleDescriptor(new Id("buyer")));
		orgExecution.addLogedAgent(sender);
		orgExecution
				.addRoleToAgent(sender, new RoleDescriptor(new Id("buyer")));

		Message response = handler.handle(message, manager, components);
		assertNull(response);
	}

}

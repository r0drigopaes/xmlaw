package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.scene.SceneDescriptor;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class StartSceneValidator extends MessageValidator {

	@Override
	protected Message handle(Message message, ExecutionManager manager,
			Map<Component, Object> components) {
		OrganizationExecution organizationExecution = (OrganizationExecution) components
				.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		SceneDescriptor sceneDescriptor = (SceneDescriptor) components
				.get(MessageValidator.Component.SCENE_DESCRIPTOR);
		AgentIdentification aid = message.getSender();

		// agent cannot create scene
		if (!sceneDescriptor.canCreateScene(aid, organizationExecution)) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.cannot_create_scene",
							sceneDescriptor.getId().getAsString()));
		}

		return null;
	}

}

package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.component.scene.SceneDescriptor;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class SceneValidator extends MessageValidator {

	@Override
	protected Message handle(Message message, ExecutionManager manager,
			Map<Component, Object> components) {
		String scene = message
				.getContentValue(MessageContentConstants.KEY_SCENE_NAME);
		// scene value is not informed
		if (scene == null) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.no_parameter",
							MessageContentConstants.KEY_SCENE_NAME));
		}

		OrganizationDescriptor organizationDescriptor = (OrganizationDescriptor) components
				.get(MessageValidator.Component.ORGANIZATION_DESCRIPTOR);
		Id sceneDecriptorId = new Id(scene);
		for (SceneDescriptor sceneDescriptor : organizationDescriptor
				.getSceneDescriptors()) {
			if (sceneDescriptor.getId().equals(sceneDecriptorId)) {
				components.put(MessageValidator.Component.SCENE_DESCRIPTOR,
						sceneDescriptor);
				break;
			}
		}

		SceneDescriptor sceneDescriptor = (SceneDescriptor) components
				.get(MessageValidator.Component.SCENE_DESCRIPTOR);
		// there is no descriptor for such scene
		if (sceneDescriptor == null) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.not_declared_scene", scene));
		}

		return null;
	}

}

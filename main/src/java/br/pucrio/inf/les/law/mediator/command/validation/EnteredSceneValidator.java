package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class EnteredSceneValidator extends MessageValidator {

	protected Message handle(Message message, ExecutionManager manager,
			Map<Component, Object> components) {
		SceneExecution sceneExecution = (SceneExecution) components
				.get(MessageValidator.Component.SCENE_EXECUTION);
		AgentIdentification aid = message.getSender();

		// check if the agent has entered the scene
		if (!sceneExecution.getLoggedAgents().containsKey(aid)) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.not_entered_scene", new Object[]{aid.getName(),sceneExecution.getId()}));
		}

		return null;
	}

}

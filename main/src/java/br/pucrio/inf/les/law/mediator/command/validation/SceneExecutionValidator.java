package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class SceneExecutionValidator extends MessageValidator {
	protected Message handle(Message message, ExecutionManager manager,
			Map<Component, Object> components) {
		String sceneExecutionStr = message
				.getContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID
						.toString());
		// scene execution value is not informed
		if (sceneExecutionStr == null) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.no_parameter",
							MessageContentConstants.KEY_SCENE_EXECUTION_ID));
		}

		SceneExecution sceneExecution = (SceneExecution) manager
				.getExecutionById(new Id(sceneExecutionStr));
		// scene execution does not exist
		if (sceneExecution == null) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.format("msg.doesnt_exist_scene", sceneExecutionStr));
		}

		components.put(MessageValidator.Component.SCENE_EXECUTION,
				sceneExecution);

		return null;
	}

}

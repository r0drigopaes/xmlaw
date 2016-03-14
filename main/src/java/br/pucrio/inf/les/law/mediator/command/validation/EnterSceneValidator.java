package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class EnterSceneValidator extends MessageValidator {
	private static final Log LOG = LogFactory.getLog(EnterSceneValidator.class);

	protected Message handle(Message message, ExecutionManager manager,
			Map<Component, Object> components) {
		OrganizationExecution organizationExecution = (OrganizationExecution) components
				.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		SceneExecution sceneExecution = (SceneExecution) components
				.get(MessageValidator.Component.SCENE_EXECUTION);

		AgentIdentification aid = message.getSender();
		RoleDescriptor roleDescriptor = (RoleDescriptor) components
				.get(MessageValidator.Component.ROLE_DESCRIPTOR);

		try {
			if (organizationExecution.getLogedAgents().contains(aid) == false) {
				return createFailureReply(message, LawResourceBundle.getInstance()
						.format("msg.not_entered_org",
								organizationExecution.getId().getAsString()));
			}
			
			// agent cannot enter scene
			if (!sceneExecution.canAgentEnter(aid, roleDescriptor,
					organizationExecution)) {
				return createFailureReply(message, LawResourceBundle
						.getInstance().format("msg.cannot_enter_scene",
								new Object[]{aid, sceneExecution.getId()}));
			}
		} catch (LawException le) {
			LOG.error("Error validating entrance of agent in scene "
					+ sceneExecution.getId().getAsString(), le);
		}

		return null;
	}

}

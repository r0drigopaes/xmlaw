package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.HashMap;
import java.util.Map;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;

public abstract class MessageValidatorScenario extends XMLawParserScenario {
	protected ExecutionManager manager;

	protected OrganizationExecution orgExecution;

	protected SceneExecution sceneExecution;

	protected Map<MessageValidator.Component, Object> components;

	protected AgentIdentification sender;

	protected Message message;

	protected MessageValidator handler;

	public void setUp() throws Exception {
		super.setUp();
		manager = new ExecutionManager(table);

		orgExecution = (OrganizationExecution) manager.newInstance(null,
				new Id("auction"), null);
		sceneExecution = (SceneExecution) manager.newInstance(orgExecution
				.getId(), new Id("game"), null);

		components = new HashMap<MessageValidator.Component, Object>();

		sender = new AgentIdentification("sender");
		message = new Message(Message.REQUEST);
		message.setSender(sender, "senderRole");
	}

	protected void sendMessageAndAssertFailure(String failureMessage) {
		Message response = handler.handle(message, manager, components);

		assertNotNull(response);
		assertEquals(response.getPerformative(), Message.FAILURE);
		assertEquals(failureMessage, response
				.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));
	}

}

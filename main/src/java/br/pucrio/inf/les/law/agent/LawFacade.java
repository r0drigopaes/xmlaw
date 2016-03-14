package br.pucrio.inf.les.law.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.communication.asf.ASFCommunication;
import br.pucrio.inf.les.law.util.Constants;
import br.pucrio.inf.les.law.util.IdGenerator;

public class LawFacade
{
	private static final Log	LOG	= LogFactory.getLog(LawFacade.class);

	private Agent				agent;

	public LawFacade(Agent agent)
	{
		this.agent = agent;
	}

	private Message createMediatorMessage(String command)
	{
		Message msg = new Message(Message.REQUEST);
		msg.setContentValue(MessageContentConstants.KEY_COMMAND, command);
		msg.setProtocol(MessageContentConstants.MEDIATOR_PROTOCOL);
		msg.setConversationId(String.valueOf(IdGenerator.getInstance().getNewId()));
		msg.setSender(agent.getAgentIdentification());
		msg.addReceiver(new AgentIdentification(Constants.MEDIATOR_ID.getValue()), Constants.MEDIATOR_NAME.getValue());
		return msg;
	}

	public Message informOrganizationToMediator(String lawURL)
	{
		Message msg = createMediatorMessage(MessageContentConstants.CMD_ADD_LAW);
		msg.setContentValue(MessageContentConstants.KEY_LAW_URL, lawURL);
		agent.getCommunicationChannel().send(msg);
		return waitRightAnswer(msg);
	}

	public Message enterOrganization(String organizationExecution)
	{

		Message msg = createMediatorMessage(MessageContentConstants.CMD_ENTER_ORGANIZATION);
		msg.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, organizationExecution);
		agent.getCommunicationChannel().send(msg);
		return waitRightAnswer(msg);
	}

	public Message performRole(String orgExecution, String role)
	{
		Message msg = createMediatorMessage(MessageContentConstants.CMD_PERFORM_ROLE);
		msg.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, orgExecution);
		msg.setContentValue(MessageContentConstants.KEY_ROLE_NAME, role);
		agent.getCommunicationChannel().send(msg);
		return waitRightAnswer(msg);
	}

	public Message startScene(String sceneName, String orgExecution)
	{
		Message msg = createMediatorMessage(MessageContentConstants.CMD_START_SCENE);
		msg.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, orgExecution);
		msg.setContentValue(MessageContentConstants.KEY_SCENE_NAME, sceneName);

		agent.getCommunicationChannel().send(msg);

		return waitRightAnswer(msg);
	}

	public Message enterInScene(String sceneExecution, String orgExecution, String role)
	{
		Message msg = createMediatorMessage(MessageContentConstants.CMD_ENTER_SCENE);
		msg.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, orgExecution);
		msg.setContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID, sceneExecution);
		msg.setContentValue(MessageContentConstants.KEY_ROLE_NAME, role);

		agent.getCommunicationChannel().send(msg);

		return waitRightAnswer(msg);
	}

	public void requestForExternalObserver(Message msgComing)
	{
		Message msg = createMediatorMessage(MessageContentConstants.CMD_START_EXTERNAL_COMMAND);
		msg.setContent(msgComing.getContent());
		agent.send(msg);
	}

	private Message waitRightAnswer(Message original)
	{

		Message answer = agent.getCommunicationChannel().waitRightMessage(original.getConversationId());

		// TODO Colocar funcionalidade na camada de comunicação (nível
		// inferior).
		if (agent.getCommunicationChannel() instanceof ASFCommunication)
		{
			((ASFCommunication) agent.getCommunicationChannel()).removeReceivedMessage(answer);
		}

		if (!answer.getConversationId().equals(original.getConversationId()))
		{
			LOG.warn("Message received [" + answer.getConversationId()
					+ "] does not match with the expected one ["
					+ original.getConversationId()
					+ "]");
		}

		return answer;
	}

}

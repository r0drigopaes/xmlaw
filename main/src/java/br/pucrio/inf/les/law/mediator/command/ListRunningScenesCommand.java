package br.pucrio.inf.les.law.mediator.command;

import java.util.Map;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.scene.SceneDescriptor;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.mediator.command.validation.ExecutionManagerValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.mediator.command.validation.OrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SenderValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator.Component;
import br.pucrio.inf.les.law.util.Constants;
import br.pucrio.inf.les.law.util.LawResourceBundle;

/**
 * Informs the ids of the current running scenes for an organization. The
 * command expects command name and the organization execution id. In case of
 * failure or success an text message will be put into the txtMessage field.
 * Example: <br>
 * 
 * Imagine that the fubar organization (with execution id 43) has 2 scenes, foo
 * and bar. In this moment there are three scene executions inside fubar, two
 * from foo (with execution ids 22 and 25) and another one form bar (with
 * execution id 67). To know the running scenes at this moment, an agent must
 * send the following mesage:<br>
 * 
 * key: command value: listScenes <br>
 * key: orgExecutionId value: 43 <br>
 * 
 * The reply content is built with the following format:<br>
 * 
 * key: command value: listScenes <br>
 * key: organizationId value: 43<br>
 * key: scene1 value: foo<br>
 * key: sceneExecutionId1 value: 22<br>
 * key: scene2 value: foo<br>
 * key: sceneExecutionId2 value: 25<br>
 * key: scene3 value: bar<br>
 * key: sceneExecutionId3 value: 67<br>
 * key: total value: 3<br>
 * 
 * @see br.pucrio.inf.les.law.communication.MessageContentConstants
 * @author lfrodrigues
 * 
 */
public class ListRunningScenesCommand extends Command
{

	protected Message executeCommand(Message message, ExecutionManager executionManager,
			Map<Component, Object> components)
	{
		Message reply = message.createReply(new AgentIdentification(Constants.MEDIATOR_ID.getValue()),
											Constants.MEDIATOR_NAME.getValue());
		reply.setPerformative(Message.INFORM);
		reply.setContentValue(MessageContentConstants.KEY_COMMAND, this.name());

		OrganizationExecution orgExec = (OrganizationExecution) components
				.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		OrganizationDescriptor orgDesc = (OrganizationDescriptor) components
				.get(MessageValidator.Component.ORGANIZATION_DESCRIPTOR);

		reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, orgExec.getId().toString());
		int i = 0;
		for (SceneDescriptor sceneDesc : orgDesc.getSceneDescriptors())
		{
			for (IExecution sceneExec : executionManager.getExecutionsByDescriptorId(sceneDesc.getId()))
			{
				i++;
				reply.setContentValue(MessageContentConstants.KEY_SCENE_NAME + i, sceneDesc.getId().toString());
				reply.setContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID + i, sceneExec.getId().toString());
			}
		}

		reply.setContentValue(MessageContentConstants.KEY_TOTAL, "" + i);
		reply.setContentValue(MessageContentConstants.KEY_TXT_MESSAGE, LawResourceBundle.getInstance()
				.format("msg.info.list_scenes", orgDesc.getId().toString(), orgExec.getId().toString()));

		return reply;
	}

	public String name()
	{
		return MessageContentConstants.CMD_LIST_RUNNING_SCENES;
	}

	protected MessageValidator createHandlerChain()
	{
		MessageValidator execMan = new ExecutionManagerValidator();
		MessageValidator sender = new SenderValidator();
		MessageValidator org = new OrganizationValidator();

		execMan.setSuccesor(sender);
		sender.setSuccesor(org);

		return execMan;
	}
}

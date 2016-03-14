package br.pucrio.inf.les.law.mediator.command;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.scene.SceneDescriptor;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.mediator.command.validation.EnteredOrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.ExecutionManagerValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.mediator.command.validation.OrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SceneValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SenderValidator;
import br.pucrio.inf.les.law.mediator.command.validation.StartSceneValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator.Component;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.util.Constants;
import br.pucrio.inf.les.law.util.LawResourceBundle;

/**
 * Creates a new scene execution. The command expects for the organization
 * execution id and scene descriptor. The field txtMessage will contain an text
 * message of success or failure. Example: an agent wants to create the scene
 * foo (which execution id is 328) in the organization bar (which execution id
 * is 789) and it performs the role to do so. A message containing the follow
 * parameters must be sent<br>
 * 
 * key: command value: createScene<br>
 * key: organizationExecution value: 789<br>
 * key: scene value: foo<br>
 * 
 * a positive respose will be<br>
 * 
 * key: command value: createScene<br>
 * key: organization value: 789<br>
 * key: scene value: foo<br>
 * key: sceneExecutionId value: 328<br>
 * 
 * @see br.pucrio.inf.les.law.communication.MessageContentConstants
 * @author lfrodrigues
 * 
 */
public class StartSceneCommand extends Command
{

	private static final Log	LOG	= LogFactory.getLog(StartSceneCommand.class);

	protected Message executeCommand(Message message, ExecutionManager executionManager,
			Map<Component, Object> components)
	{
		OrganizationExecution organizationExecution = (OrganizationExecution) components
				.get(MessageValidator.Component.ORGANIZATION_EXECUTION);
		SceneDescriptor sceneDescriptor = (SceneDescriptor) components.get(MessageValidator.Component.SCENE_DESCRIPTOR);

		IExecution sceneExecution = null;
		try
		{
			sceneExecution = executionManager.newInstance(organizationExecution.getId(), sceneDescriptor.getId(), null);
		}
		catch (LawException le)
		{
			LOG.warn("Error while creating Scene " + sceneDescriptor.getId().getAsString(), le);
		}

		Message reply = message.createReply(new AgentIdentification(Constants.MEDIATOR_ID.getValue()),
											Constants.MEDIATOR_NAME.getValue());
		reply.setPerformative(Message.INFORM);
		reply.setContentValue(MessageContentConstants.KEY_COMMAND, this.name());
		reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, organizationExecution.getId()
				.getAsString());
		reply.setContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID, sceneExecution.getId().getAsString());
		reply.setContentValue(MessageContentConstants.KEY_SCENE_NAME, sceneDescriptor.getId().getAsString());
		reply.setContentValue(MessageContentConstants.KEY_TXT_MESSAGE, LawResourceBundle.getInstance()
				.format("msg.info.start_scene",
						sceneExecution.getDescriptor().getId().toString(),
						sceneExecution.getId().toString(),
						organizationExecution.getDescriptor().getId().toString(),
						organizationExecution.getId().toString()));
		return reply;
	}

	public String name()
	{
		return MessageContentConstants.CMD_START_SCENE;
	}

	protected MessageValidator createHandlerChain()
	{
		MessageValidator execMan = new ExecutionManagerValidator();
		MessageValidator sender = new SenderValidator();
		MessageValidator org = new OrganizationValidator();
		MessageValidator enterOrg = new EnteredOrganizationValidator();
		MessageValidator scene = new SceneValidator();
		MessageValidator createScene = new StartSceneValidator();

		execMan.setSuccesor(sender);
		sender.setSuccesor(org);
		org.setSuccesor(enterOrg);
		enterOrg.setSuccesor(scene);
		scene.setSuccesor(createScene);

		return execMan;
	}

}

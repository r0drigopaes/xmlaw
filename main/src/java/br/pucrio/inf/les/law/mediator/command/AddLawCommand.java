package br.pucrio.inf.les.law.mediator.command;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.mediator.Mediator;
import br.pucrio.inf.les.law.mediator.command.validation.ExecutionManagerValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SenderValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator.Component;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.XMLawParser;
import br.pucrio.inf.les.law.util.Constants;
import br.pucrio.inf.les.law.util.LawResourceBundle;

/**
 * Insert a new organization into the mediator. The command expects for the
 * lawurl parameter. The command replies a message containing the organization
 * descriptor name, the organization execution id and this command. In case of
 * failure, the command will reply a message with the field txtMessage containig
 * the failure reason. If if success, the content of the field will be a
 * positive message <br>
 * Example: an agent wants to insert an organization into the mediator so it
 * must send a message containing:<br>
 * key: command value: addLaw<br>
 * key:lawUrl value: http://www.somewhere.com/fooOrgLaw.xml<br>
 * The command will reply:<br>
 * key: organization value: fooOrg<br>
 * key: orgExecutionId value: 98<br>
 * key: command value: addLaw<br>
 * 
 * @see br.pucrio.inf.les.law.communication.MessageContentConstants
 * @author lfrodrigues
 */
public class AddLawCommand extends Command
{

	private static final Log	LOG	= LogFactory.getLog(AddLawCommand.class);

	private Mediator			mediator;

	public AddLawCommand(Mediator mediator)
	{
		this.mediator = mediator;
	}

	protected Message executeCommand(Message message, ExecutionManager executionManager,
			Map<Component, Object> components)
	{

		// retrieves the lawUrl from the message content
		String lawURLStr = message.getContentValue(MessageContentConstants.KEY_LAW_URL);
		// law url is not informed
		if (lawURLStr == null)
		{
			return super.getValidatorChain().createFailureReply(message,
																LawResourceBundle.getInstance()
																		.format("msg.no_parameter",
																				MessageContentConstants.KEY_LAW_URL));
		}

		// parse the law url
		DescriptorTable table = null;
		InputStream urlIn = null;
		try
		{
			URL lawURL = new URL(lawURLStr);
			urlIn = lawURL.openStream();
			XMLawParser parser = new XMLawParser();
			table = parser.load(urlIn, lawURLStr);
		}
		catch (MalformedURLException mue)
		{
			return super.getValidatorChain().createFailureReply(message,
																LawResourceBundle.getInstance()
																		.format("msg.url.malformed", lawURLStr));
		}
		catch (IOException ioe)
		{
			return super.getValidatorChain().createFailureReply(message,
																LawResourceBundle.getInstance()
																		.format("msg.url.unable_to_open", lawURLStr));
		}
		catch (SAXException se)
		{
			return super.getValidatorChain().createFailureReply(message,
																LawResourceBundle.getInstance()
																		.getString("msg.file.malformed"));
		}
		catch (Throwable t)
		{
			return super.getValidatorChain().createFailureReply(message,
																LawResourceBundle.getInstance()
																		.getString("msg.file.error_parsing"));
		}
		finally
		{
			if (urlIn != null)
			{
				try
				{
					urlIn.close();
				}
				catch (IOException ioe)
				{
					LOG.warn("Error closing connection from " + lawURLStr, ioe);
				}
			}
		}

		// creates a new execution manager
		ExecutionManager newManager = new ExecutionManager(table);
		Context organizationContext = new Context(newManager, mediator.getRootContext());

		// retrieves the organization descriptor created
		// certify that there is only one organization created
		List<IDescriptor> organizationList = newManager.getDescriptorsByDescriptorClass(OrganizationDescriptor.class);
		if (organizationList.size() > 1)
		{
			LOG.warn("The law " + lawURLStr
					+ " declares "
					+ organizationList.size()
					+ " organizations, considering the first one");
		}

		// creates a new organization execution
		IDescriptor organizationDescriptor = organizationList.get(0);
		IExecution organizationExecution = null;
		try
		{
			organizationExecution = newManager.newInstanceInContext(organizationContext,
																	organizationDescriptor.getId(),
																	null);
		}
		catch (LawException le)
		{
			LOG.error("Unable to create execution of organization " + organizationDescriptor.getId(), le);
			return super.getValidatorChain().createFailureReply(message,
																LawResourceBundle.getInstance()
																		.format("msg.error_creating_org",
																				organizationDescriptor.getId()));
		}

		mediator.addExecutionManager(organizationExecution.getId().toString(), newManager);
		// at this point the organization was properly inserted into the
		// mediator
		// sends a reply message to the agent containig the name of the
		// organization and its execution id
		Message reply = message.createReply(new AgentIdentification(Constants.MEDIATOR_ID.getValue()),
											Constants.MEDIATOR_NAME.getValue());
		reply.setPerformative(Message.INFORM);
		reply.setContentValue(MessageContentConstants.KEY_COMMAND, this.name());
		reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_NAME, organizationDescriptor.getId().toString());
		reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, organizationExecution.getId()
				.toString());
		reply.setContentValue(MessageContentConstants.KEY_TXT_MESSAGE, LawResourceBundle.getInstance()
				.format("msg.info.law_added",
						organizationDescriptor.getId().toString(),
						organizationExecution.getId().toString()));

		return reply;
	}

	public String name()
	{
		return MessageContentConstants.CMD_ADD_LAW;
	}

	protected MessageValidator createHandlerChain()
	{
		MessageValidator execMan = new ExecutionManagerValidator();
		MessageValidator sender = new SenderValidator();

		execMan.setSuccesor(sender);

		return execMan;
	}

}

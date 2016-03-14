package br.pucrio.inf.les.law.mediator.command;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.CommunicationProvider;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.mediator.Mediator;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class AddLawCommandTest extends XMLawParserScenario
{

	private Mediator			mediator;

	private AddLawCommand		addLawCommand;

	private Message				message;

	private String				lawsDir;

	private static final Log	LOG	= LogFactory.getLog(AddLawCommandTest.class);

	public void setUp() throws Exception
	{
		super.setUp();
		LOG.info("Creating Mediator");
		mediator = new Mediator();
		addLawCommand = new AddLawCommand(mediator);

		message = new Message(Message.REQUEST);

		String fs = System.getProperty("file.separator");
		lawsDir = System.getProperty("user.dir") + fs + "laws" + fs + "invalidLaws" + fs;
	}

	public void tearDown() throws Exception
	{
		super.tearDown();
		LOG.info("Closing mediators connection");
		CommunicationProvider.destroyMediatorCommunication();
	}

	private void sendMessageAndAssert(Message message, String failureMessage)
	{
		Message response = null;
		try
		{
			response = addLawCommand.executeCommand(message, null, null);
		}
		catch (Exception e)
		{
			fail("Exception thrown while executing addLawCommand: " + e.getMessage());
			LOG.error(e);
		}

		assertNotNull(response);
		assertEquals(Message.FAILURE, response.getPerformative());
		assertEquals(failureMessage, response.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));
	}

	public void testAddLaw() throws Exception
	{
		String fs = System.getProperty("file.separator");
		File lawFile = new File(System.getProperty("user.dir") + fs + "laws" + fs + "TestLaw.xml");
		message.setContentValue(MessageContentConstants.KEY_LAW_URL, lawFile.toURL().toString());

		Message response = addLawCommand.executeCommand(message, null, null);

		assertNotNull(response);
		assertEquals(Message.INFORM, response.getPerformative());
		assertEquals(MessageContentConstants.CMD_ADD_LAW, response.getContentValue(MessageContentConstants.KEY_COMMAND));
		String orgName = response.getContentValue(MessageContentConstants.KEY_ORGANIZATION_NAME);
		assertEquals("auction", orgName);
		String orgExecStr = response.getContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID);
		assertNotNull(orgExecStr);

		String expectedTxtMsg = LawResourceBundle.getInstance().format("msg.info.law_added", orgName, orgExecStr);
		assertEquals(expectedTxtMsg, response.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));

		ExecutionManager manager = mediator.getExecutionManager(orgExecStr);
		assertNotNull(manager);

		IExecution orgExec = manager.getExecutionById(new Id(orgExecStr));
		assertEquals(OrganizationExecution.class, orgExec.getClass());

		assertSame(mediator.getRootContext(), orgExec.getContext().getParent());
	}

	public void testName()
	{
		assertEquals(addLawCommand.name(), MessageContentConstants.CMD_ADD_LAW);
	}

	public void testNoLawURLInformed()
	{
		String expected = LawResourceBundle.getInstance().format(	"msg.no_parameter",
																	MessageContentConstants.KEY_LAW_URL);
		sendMessageAndAssert(message, expected);
	}

	public void testMalformedURL()
	{
		message.setContentValue(MessageContentConstants.KEY_LAW_URL, "malformed url");

		String expected = LawResourceBundle.getInstance().format("msg.url.malformed", "malformed url");
		sendMessageAndAssert(message, expected);
	}

	public void testCouldNotOpenUrl()
	{
		message.setContentValue(MessageContentConstants.KEY_LAW_URL,
								"http://localhost/essearquivonaodeveriaexistir.xml");
		String expected = LawResourceBundle.getInstance()
				.format("msg.url.unable_to_open", "http://localhost/essearquivonaodeveriaexistir.xml");
		sendMessageAndAssert(message, expected);
	}

	public void testErrorSaxException() throws Exception
	{
		File file = new File(lawsDir + "TestMalformedLawFile.xml");
		message.setContentValue(MessageContentConstants.KEY_LAW_URL, file.toURL().toString());

		String expected = LawResourceBundle.getInstance().getString("msg.file.malformed");
		sendMessageAndAssert(message, expected);
	}

	public void testErrorLawException() throws Exception
	{

		File file = new File(lawsDir + "TestInvalidLawFile.xml");
		message.setContentValue(MessageContentConstants.KEY_LAW_URL, file.toURL().toString());
		String expected = LawResourceBundle.getInstance().getString("msg.file.error_parsing");
		sendMessageAndAssert(message, expected);
	}

	public void testUnableToCreateOrganization()
	{
		// TODO how this error should be tested?
	}
}
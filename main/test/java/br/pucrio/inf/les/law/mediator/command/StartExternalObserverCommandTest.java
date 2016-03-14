package br.pucrio.inf.les.law.mediator.command;

import org.apache.log4j.Logger;

import br.pucrio.inf.les.law.XMLawScenario;
import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.CommunicationProvider;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.mediator.Mediator;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class StartExternalObserverCommandTest extends XMLawScenario {
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger
			.getLogger(StartExternalObserverCommandTest.class);

	private StartExternalObserverCommand cmd;

	private Mediator mediator;

	public void setUp() throws Exception {
		super.setUp();
		mediator = new Mediator();
		mediator.addExecutionManager(orgExecution.getId().toString(),manager);
		cmd = new StartExternalObserverCommand(mediator);
	}
	
	protected void tearDown() throws Exception
	{
		super.tearDown();
		LOG.info("closing mediator's connection");
		CommunicationProvider.destroyMediatorCommunication();
	}

	public void testStartExternalObserverCommand() throws LawException {

		Message message = new Message(Message.REQUEST);

		message.setContentValue("agentName", "maira");
		message.setContentValue("addressHost", "localhost");
		message.setContentValue("portNumber", "1025");
		message.setContentValue("eventType", "update_criticality");

		AgentIdentification aid = new AgentIdentification(message.getContentValue("agentName"));
		boolean hasEntered = orgExecution.addLogedAgent(aid);
		
		assertTrue(hasEntered);
		
		RoleDescriptor role = new RoleDescriptor(new Id("seller"));
		orgExecution.addRoleToAgent(aid,role);
		
		// TODO ele pode nao ter um papel na organizacao e ter na cena 
		assertTrue( orgExecution.doesAgentPerformRole(aid, role) );
		
		hasEntered = sceneExecution.enterScene(aid,role,orgExecution);
		
		assertTrue(hasEntered);
		
		Message response = cmd.executeCommand(message, null, null);
		assertEquals(Message.CONFIRM, response.getPerformative());

	}

}

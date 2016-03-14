package br.pucrio.inf.les.law.mediator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.XMLawTestCase;
import br.pucrio.inf.les.law.communication.CommunicationProvider;
import br.pucrio.inf.les.law.communication.jade.JadeMessageWraperTest;
import br.pucrio.inf.les.law.model.LawException;

public class MediatorTest extends XMLawTestCase
{
	private static final Log	LOG	= LogFactory.getLog(JadeMessageWraperTest.class);

	private Mediator			mediator;

	public void setUp() throws Exception
	{
		super.setUp();
		LOG.info("Creating mediator");
		mediator = new Mediator();
	}

	public void testIsAlive() throws LawException, InterruptedException
	{
		mediator.startMediator();
		assertTrue(mediator.isAlive());
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
		LOG.info("closing mediator's connection");
		CommunicationProvider.destroyMediatorCommunication();
	}
}
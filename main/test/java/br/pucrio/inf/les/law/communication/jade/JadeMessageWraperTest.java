package br.pucrio.inf.les.law.communication.jade;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.XMLawTestCase;
import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;

public class JadeMessageWraperTest extends XMLawTestCase {
	
	private ACLMessage aclMessage;

	private Message lawMessage;
	
	private AID jadeAID;
	
	private AgentIdentification agentIdentification;
	
	private AgentContainer mainContainer;
	
	private static final Log LOG = LogFactory.getLog(JadeMessageWraperTest.class);
	
	public void setUp() throws Exception {
		super.setUp();

		//starts jade main container
		LOG.info("Creating main container");
		mainContainer = JadePlatform.createContainer();
		
		//creates a Jade AID
		jadeAID = new AID("someAgent", AID.ISLOCALNAME);
		jadeAID.addAddresses("somewhere@teccomm");
		
		//creates a Agent Identification
		agentIdentification = new AgentIdentification("someAgent");
		agentIdentification.addAddresses("somewhere@teccomm");

		// 
		// creates an aclMessage
		//

		aclMessage = new ACLMessage(ACLMessage.getInteger(Message.INFORM));
		// user defined parameters
		aclMessage.addUserDefinedParameter("udParam1", "udValue1");
		aclMessage.addUserDefinedParameter("udParam2", "udValue2");
		// receiver settings
		aclMessage.addReceiver(new AID("receiver", AID.ISLOCALNAME));
		aclMessage.addUserDefinedParameter(JadeCommunication.RECEIVER_ROLE,
				"receiverRole");
		// content settings
		String content = JadeMessageWrapper.ENTRY_SEPARATOR + "key1"
				+ JadeMessageWrapper.KEY_VALUE_SEPARATOR + "value1"
				+ JadeMessageWrapper.ENTRY_SEPARATOR + "key2"
				+ JadeMessageWrapper.KEY_VALUE_SEPARATOR + "value2";
		aclMessage.setContent(content);
		// sender settings
		aclMessage.setSender(new AID("sender", AID.ISLOCALNAME));
		aclMessage.addUserDefinedParameter(JadeCommunication.SENDER_ROLE,
				"senderRole");
		// conversatoin id
		aclMessage.setConversationId("conversationId");
		// protocol settings
		aclMessage.setProtocol("protocol");

		//
		// creates a XMLaw message
		//

		lawMessage = new Message(Message.INFORM);
		// user defined parameters
		lawMessage.addUserDefined("udParam1", "udValue1");
		lawMessage.addUserDefined("udParam2", "udValue2");
		// reciver settings
		lawMessage.addReceiver(new AgentIdentification("receiver"), "receiverRole");
		// content settings
		lawMessage.setContentValue("key1", "value1");
		lawMessage.setContentValue("key2", "value2");
		// sender settings
		lawMessage.setSender(new AgentIdentification("sender"), "senderRole");
		// conversation id
		lawMessage.setConversationId("conversationId");
		// protocol settings
		lawMessage.setProtocol("protocol");

	}
	
	protected void tearDown() throws Exception {
		LOG.info("killing container");
		try
		{
			mainContainer.kill();
		}
		catch(StaleProxyException spe)
		{
			LOG.info(spe);
		}
	}

	public void testTransformMessage() {
		Message tranformedMessage = JadeMessageWrapper.getInstance().transform(
				aclMessage);
		assertEquals(lawMessage,tranformedMessage);
	}

	public void testTransformACLMessage() {
		ACLMessage transformedMessage = JadeMessageWrapper.getInstance()
				.transform(lawMessage);

		//verifies if the transformed message is equals to the acl message
		assertEquals(transformedMessage.getProtocol(), aclMessage.getProtocol());
		assertEquals(transformedMessage.getUserDefinedParameter("udParam1"),
				aclMessage.getUserDefinedParameter("udParam1"));
		assertEquals(transformedMessage.getUserDefinedParameter("udParam2"),
				aclMessage.getUserDefinedParameter("udParam2"));

		assertEquals(transformedMessage.getAllReceiver().next(), aclMessage
				.getAllReceiver().next());
		assertEquals(
				transformedMessage
						.getUserDefinedParameter(JadeCommunication.RECEIVER_ROLE),
				aclMessage
						.getUserDefinedParameter(JadeCommunication.RECEIVER_ROLE));
		assertEquals(transformedMessage.getSender(), aclMessage.getSender());
		assertEquals(transformedMessage
				.getUserDefinedParameter(JadeCommunication.SENDER_ROLE),
				aclMessage
						.getUserDefinedParameter(JadeCommunication.SENDER_ROLE));

		assertEquals(transformedMessage.getConversationId(), aclMessage
				.getConversationId());
		assertEquals(transformedMessage.getProtocol(), aclMessage.getProtocol());
		
		assertEquals(transformedMessage.getContent(),aclMessage.getContent());
	}
	
	public void testTransformAgentIdentification() 
	{
		AgentIdentification transformed = JadeMessageWrapper.getInstance().transform(jadeAID);
		assertEquals(transformed,agentIdentification);
	}

	public void testTransformAID() 
	{
		AID transformed = JadeMessageWrapper.getInstance().transform(agentIdentification);
		assertEquals(transformed.getName(),jadeAID.getName());
		assertEquals(transformed.getAllAddresses().next(),jadeAID.getAllAddresses().next());
	}

}

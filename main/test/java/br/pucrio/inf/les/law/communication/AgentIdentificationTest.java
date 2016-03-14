package br.pucrio.inf.les.law.communication;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import junit.framework.TestCase;

public class AgentIdentificationTest extends TestCase 
{
	
	/*
	 * Test method for 'br.pucrio.inf.les.law.model.AgentIdentification.AgentIdentification(String)'
	 */
	public void testAgentIdentification() 
	{
		//creates two agent id for test
		AgentIdentification agentId1 = new AgentIdentification("agentId1");
		agentId1.addAddresses("agent1@les");
		agentId1.addAddresses("agent1@teccomm");
		
		AgentIdentification agentId2 = new AgentIdentification("agentId2");
		agentId2.addAddresses("agent2@les");
		agentId2.addAddresses("agent2@teccomm");
		
		//agentId1 list addreess must have 2 addresses: agent1@les and agent1@teccomm
		assertEquals(agentId1.getAllAddresses().size(),2);
		
		//duplicated addresses must not be considered
		agentId1.addAddresses("agent1@les");
		assertEquals(agentId1.getAllAddresses().size(),2);
		
		//verifies if the address agent1@les was inserted
		assertTrue(agentId1.getAllAddresses().contains("agent1@les"));
		
		//agentId 1 is diferent of agentId 2 (white box test for equals)
		assertFalse(agentId1.equals(agentId2));
		
		//an agent Id is diferent from an object which is not an agent Id (white box test for equals)
		assertFalse(agentId1.equals(new Object()));
		
		//an agent Id is different from another agent Id with diferent name (white box test for equals)
		AgentIdentification agentId1Clone = new AgentIdentification("agentId");
		assertFalse(agentId1.equals(agentId1Clone));
		
		//an agent Id is different from another agent Id with the same name but no addresses (white box test for equals)
		agentId1Clone.setName("agentId1");
		assertFalse(agentId1.equals(agentId1Clone));		
		
		//an agent Id is different from anothe agent Id with the same name but diferent addresses (white box test for equals)
		agentId1Clone.addAddresses("agent1@les");
		assertFalse(agentId1.equals(agentId1Clone));

		agentId1Clone.addAddresses("agent1@teccomm");
		assertTrue(agentId1.equals(agentId1Clone));
		
		//verifies address removal for invalid address
		agentId1.removeAddresses("no valid address");
		assertEquals(agentId1.getAllAddresses().size(),2);
		
		//verifies address removal for a valid address
		agentId1.removeAddresses("agent1@les");
		assertFalse(agentId1.getAllAddresses().contains("agent1@les"));
		
		//verifies address clear
		agentId1.clearAllAddresses();
		assertEquals(agentId1.getAllAddresses().size(),0);

	}

}

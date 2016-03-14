package br.pucrio.inf.les.law.component.organization;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;

public class OrganizationExecutionTest extends XMLawParserScenario {
	public void testAgentEntranceAndRole() {
		ExecutionManager manager = new ExecutionManager(table);

		// Creates the organization instance
		OrganizationExecution organizationExecution = null;
		try {
			organizationExecution = (OrganizationExecution) manager
					.newInstance(null, new Id("auction"), null);
		} catch (LawException le) {
			fail("Could not create Organization Execution");
		}

		AgentIdentification agent1 = new AgentIdentification("agent1");
		AgentIdentification agent2 = new AgentIdentification("agent2");
		RoleDescriptor sellerRole = new RoleDescriptor(new Id("seller"));

		// logs the agent agent1 into the organization and verifies if it is
		// correctly loged in
		assertTrue(organizationExecution.addLogedAgent(agent1));
		assertTrue(organizationExecution.isAgentLoged(agent1));
		assertEquals(organizationExecution.getLogedAgents().size(), 1);

		// try to login the same agent again, it must return false and nothing
		// must change
		assertFalse(organizationExecution.addLogedAgent(agent1));
		assertTrue(organizationExecution.isAgentLoged(agent1));
		assertEquals(organizationExecution.getLogedAgents().size(), 1);

		// try to add a not declared role to the agent
		assertFalse(organizationExecution.addRoleToAgent(agent1,
				new RoleDescriptor(new Id("not declared role"))));

		// try to add a declared role to a not loged agent
		assertFalse(organizationExecution.addRoleToAgent(agent2, sellerRole));

		// add the seller role to the agent
		assertTrue(organizationExecution.addRoleToAgent(agent1, sellerRole));

		// verifies if the agent1 performs the seller role
		assertTrue(organizationExecution.doesAgentPerformRole(agent1,
				sellerRole));
		assertEquals(organizationExecution.getRolesPerformed(agent1).size(), 1);

		// ensure that the agent2 does not perform any role because it is not
		// loged
		assertFalse(organizationExecution.doesAgentPerformRole(agent2,
				sellerRole));
		assertEquals(organizationExecution.getRolesPerformed(agent2), null);

		// try to add the seller role again to agent 1
		assertFalse(organizationExecution.addRoleToAgent(agent1, sellerRole));

		// verifies if the roles performed by the agent 1 does not change
		assertTrue(organizationExecution.doesAgentPerformRole(agent1,
				sellerRole));
		assertEquals(organizationExecution.getRolesPerformed(agent1).size(), 1);

		// try to remove a not performed role from agent 1
		assertFalse(organizationExecution.removeRoleFromAgent(agent1,
				new RoleDescriptor(new Id("not performed role"))));

		// verifies that nothing changed
		assertTrue(organizationExecution.doesAgentPerformRole(agent1,
				sellerRole));
		assertEquals(organizationExecution.getRolesPerformed(agent1).size(), 1);

		// try to remove the role from a not loged agent
		assertFalse(organizationExecution.removeRoleFromAgent(agent2,
				sellerRole));

		// remove the seller role from agent 1
		assertTrue(organizationExecution
				.removeRoleFromAgent(agent1, sellerRole));
		assertFalse(organizationExecution.doesAgentPerformRole(agent1,
				sellerRole));
		assertEquals(organizationExecution.getRolesPerformed(agent1).size(), 0);

		// remove agent 1 from the organization
		organizationExecution.removeLogedAgent(agent1);
		assertFalse(organizationExecution.isAgentLoged(agent1));
		assertEquals(organizationExecution.getRolesPerformed(agent1), null);
		assertEquals(organizationExecution.getLogedAgents().size(), 0);
	}
}

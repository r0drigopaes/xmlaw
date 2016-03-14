package br.pucrio.inf.les.law.component.scene;

import java.util.Map;
import java.util.Set;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.protocol.State;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;

public class SceneHandlerTest extends XMLawParserScenario {

	public void testSceneHandler() throws LawException 
	{
		ExecutionManager execManager = new ExecutionManager(table);

		OrganizationExecution organizationExecution = (OrganizationExecution)execManager.newInstance(null,new Id("auction"),null);

		SceneDescriptor sceneDescriptor = (SceneDescriptor) table.get(new Id(
				"game"));

		assertNotNull(sceneDescriptor);

		assertTrue(sceneDescriptor.getTimeToLive() == -1);

		assertEquals(sceneDescriptor.getProtocolDescriptor().getId(), new Id(
				"contract-net"));

		assertEquals(sceneDescriptor.getClocks().get(0).getId(), new Id(
				"clock_1"));

		
		AgentIdentification agentId = new AgentIdentification("agentId");
		
		//a not loged agent cannot create a scene
		assertFalse(sceneDescriptor.canCreateScene(agentId,organizationExecution));
		
		//the dummy role cannot create the scene
		organizationExecution.addLogedAgent(agentId);
		organizationExecution.addRoleToAgent(agentId, new RoleDescriptor(
				new Id("dummy")));
		assertFalse(sceneDescriptor.canCreateScene(agentId,
				organizationExecution));

		// but the seller role can
		organizationExecution.addRoleToAgent(agentId, new RoleDescriptor(
				new Id("seller")));
		assertTrue(sceneDescriptor.canCreateScene(agentId,
				organizationExecution));

		// the limit for the buyer role are 2 agents
		assertEquals(new Integer(2), sceneDescriptor
				.getRoleLimit(new RoleDescriptor(new Id("buyer"))));

		// the number of entrance roles is 2
		Map<RoleDescriptor, Set<State>> entranceRoles = sceneDescriptor
				.getEntranceRoles();
		assertEquals(2, entranceRoles.size());

		// the number of entrance states for the role buyer are 2
		Set<State> states = entranceRoles.get(new RoleDescriptor(
				new Id("buyer")));
		assertEquals(2, states.size());

		// the state s0 must be inside of the states set
		State state = new State(new Id("s0"), "Initial State",
				State.Type.INITIAL);
		assertTrue(states.contains(state));
	}
}

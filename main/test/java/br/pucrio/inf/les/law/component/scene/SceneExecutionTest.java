package br.pucrio.inf.les.law.component.scene;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.protocol.ProtocolDescriptor;
import br.pucrio.inf.les.law.component.protocol.ProtocolScenario;
import br.pucrio.inf.les.law.component.protocol.State;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class SceneExecutionTest extends TestCase implements IObserver {
	/**
	 * Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(SceneExecutionTest.class);

	private int counter;

	public void setUp() {
		counter = 0;
	}

	public void testSceneCreationAndEntrance() throws LawException {

		DescriptorTable descriptorTable = new DescriptorTable();
		ExecutionManager manager = new ExecutionManager(descriptorTable);

		// organization Execution
		OrganizationDescriptor organizationDescriptor = new OrganizationDescriptor(
				new Id("theOrg"), "The world");
		descriptorTable.add(organizationDescriptor);

		OrganizationExecution organizationExecution = (OrganizationExecution) manager
				.newInstance(null, new Id("theOrg"), null);

		Id sceneId = new Id("theSceneId");
		SceneDescriptor sceneDescriptor = new SceneDescriptor(sceneId, -1);

		descriptorTable.add(sceneDescriptor);

		// Protocol
		ProtocolScenario protocolScenario = new ProtocolScenario();
		protocolScenario.mountScenario();
		ProtocolDescriptor protocolDescriptor = protocolScenario
				.getProtocolDescriptor();
		descriptorTable.add(protocolDescriptor);
		// Setting to the scene
		sceneDescriptor.setProtocolDescriptor(protocolDescriptor);

		// identifications and roles
		organizationDescriptor.addRoleDescriptors(new RoleDescriptor(new Id(
				"creator")));
		organizationDescriptor.addRoleDescriptors(new RoleDescriptor(new Id(
				"entrancer")));

		AgentIdentification agentCreatorId = new AgentIdentification(
				"creatorId");
		organizationExecution.addLogedAgent(agentCreatorId);
		organizationExecution.addRoleToAgent(agentCreatorId,
				new RoleDescriptor(new Id("creator")));

		AgentIdentification agentEntrancerId1 = new AgentIdentification(
				"entrancerId1");
		organizationExecution.addLogedAgent(agentEntrancerId1);

		// setting scene
		sceneDescriptor.addCreationRole(new RoleDescriptor(new Id("creator")));
		Set<State> states = new HashSet<State>();
		states.add(protocolScenario.getS1());
		sceneDescriptor.addEntranceRole(
				new RoleDescriptor(new Id("entrancer")), states, 1);

		assertTrue(sceneDescriptor.canCreateScene(agentCreatorId,
				organizationExecution));
		assertFalse(sceneDescriptor.canCreateScene(agentEntrancerId1,
				organizationExecution));

		SceneExecution sceneExecution = (SceneExecution) manager.newInstance(
				null, sceneDescriptor.getId(), null);

		// white box tests for scene entrance
		// #1 - agent does not perform the argued role
		assertFalse(sceneExecution.enterScene(agentEntrancerId1,
				new RoleDescriptor(new Id("entrancer")), organizationExecution));

		// #2 - scene does not has the entrance role but the agent performs it
		organizationExecution.addRoleToAgent(agentEntrancerId1,
				new RoleDescriptor(new Id("dummy")));
		assertFalse(sceneExecution.enterScene(agentEntrancerId1,
				new RoleDescriptor(new Id("dummy")), organizationExecution));

		// #3 - the agent perform the role and the scene has it, but the
		// entrance state is not valid
		organizationExecution.addRoleToAgent(agentEntrancerId1,
				new RoleDescriptor(new Id("entrancer")));
		assertFalse(sceneExecution.enterScene(agentEntrancerId1,
				new RoleDescriptor(new Id("entrancer")), organizationExecution));

		// #4 - the scene state chages to s1 allowing the agent to enter
		assertEquals(0, sceneExecution.getLoggedAgents().size());
		sceneExecution.getContext().fire(protocolScenario.getEvent());
		assertTrue(sceneExecution.enterScene(agentEntrancerId1,
				new RoleDescriptor(new Id("entrancer")), organizationExecution));
		assertEquals(1, sceneExecution.getLoggedAgents().size());

		// #5 - agent already logged, nothing change
		assertTrue(sceneExecution.enterScene(agentEntrancerId1,
				new RoleDescriptor(new Id("entrancer")), organizationExecution));
		assertEquals(1, sceneExecution.getLoggedAgents().size());

		// #6 - another agent try to enter but the limit was reached
		AgentIdentification agentEntrancerId2 = new AgentIdentification(
				"entrancerId2");
		organizationExecution.addLogedAgent(agentEntrancerId2);
		organizationExecution.addRoleToAgent(agentEntrancerId2,
				new RoleDescriptor(new Id("entrancer")));
		assertFalse(sceneExecution.enterScene(agentEntrancerId2,
				new RoleDescriptor(new Id("entrancer")), organizationExecution));
		assertEquals(1, sceneExecution.getLoggedAgents().size());

		// #7 - force the entrance of the previous agent and waits for an
		// exception when a third
		// agent tries to enter
		sceneExecution.forceSceneEntrance(agentEntrancerId2,
				new RoleDescriptor(new Id("entrancer")));
		AgentIdentification agentEntrancerId3 = new AgentIdentification(
				"entrancerId3");
		organizationExecution.addLogedAgent(agentEntrancerId3);
		organizationExecution.addRoleToAgent(agentEntrancerId3,
				new RoleDescriptor(new Id("entrancer")));
		try {
			sceneExecution.enterScene(agentEntrancerId3, new RoleDescriptor(
					new Id("entrancer")), organizationExecution);
			fail("Expected exception from enterScene");
		} catch (LawException le) {
			// exception was properly thrown
		}
	}

	public void testProtocol() throws LawException {
		Id sceneId = new Id("theSceneId");
		SceneDescriptor sceneDescriptor = new SceneDescriptor(sceneId, -1);

		DescriptorTable descriptorTable = new DescriptorTable();
		descriptorTable.add(sceneDescriptor);

		// Protocol
		ProtocolScenario protocolScenario = new ProtocolScenario();
		protocolScenario.mountScenario();
		ProtocolDescriptor protocolDescriptor = protocolScenario
				.getProtocolDescriptor();
		descriptorTable.add(protocolDescriptor);
		// Setting to the scene
		sceneDescriptor.setProtocolDescriptor(protocolDescriptor);

		ExecutionManager manager = new ExecutionManager(descriptorTable);

		IExecution sceneExecution = manager.newInstance(null, sceneDescriptor
				.getId(), null);

		List<IExecution> protocols = manager
				.getExecutionsByDescriptorIdAndContext(protocolDescriptor
						.getId(), sceneExecution.getContext());

		// Test if the protocol has been created with successful
		assertTrue(protocols.size() == 1);
	}

	/**
	 * Script: - generate event 1<br> - test if the transition 1 was activated
	 * and clock 1 was activated<br> - wait 1,3 seconds<br> - test if a clock
	 * tick was received<br> - generate event 2<br> - test if there is a clock
	 * deactivation, transition 2 was activated, and clock 2 was activated<br>
	 * 
	 * @throws LawException
	 * @throws InterruptedException
	 */
	public void testNormalSceneExecution() throws LawException,
			InterruptedException {
		SceneScenario scenario = new SceneScenario();
		scenario.mountScenario();

		ExecutionManager manager = new ExecutionManager(scenario.getTable());
		IExecution sceneExecution = manager.newInstance(null, scenario
				.getSceneDescriptorId(), null);

		Context sceneContext = sceneExecution.getContext();
		sceneContext.attachObserver(this, Masks.TRANSITION_ACTIVATION);
		sceneContext.attachObserver(this, Masks.CLOCK_ACTIVATION);
		sceneContext.fire(scenario.getEvent1());
		assertTrue(counter == 2);

		sceneContext.detachObserver(this, Masks.TRANSITION_ACTIVATION);
		sceneContext.detachObserver(this, Masks.CLOCK_ACTIVATION);
		counter = 0;
		sceneContext.attachObserver(this, Masks.CLOCKTICK);
		Thread.sleep(1300);
		assertTrue(counter == 1);

		sceneContext.detachObserver(this, Masks.CLOCKTICK);
		counter = 0;
		sceneContext.attachObserver(this, Masks.CLOCK_DEACTIVATION);
		sceneContext.attachObserver(this, Masks.TRANSITION_ACTIVATION);
		sceneContext.attachObserver(this, Masks.CLOCK_ACTIVATION);
		sceneContext.fire(scenario.getEvent2());
		Thread.sleep(1300);
		assertTrue(counter == 3);
	}

	public void update(Event event) throws LawException {
		counter++;
	}

	public void notifySubjectDeath(Subject subject) {
		// TODO Auto-generated method stub
		
	}
}

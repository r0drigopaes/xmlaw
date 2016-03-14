package br.pucrio.inf.les.law.component.organization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.component.ElementExecution;
import br.pucrio.inf.les.law.component.criticality.CriticalityAnalysisExecution;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.execution.AbstractExecution;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;

public class OrganizationExecution extends ElementExecution  {
	
	private static final Log LOG = LogFactory.getLog(OrganizationExecution.class);
	

	/**
	 * Each agent identification is mapped with a set of the roles it performs
	 * into this organization
	 */
	private Map<AgentIdentification, Set<RoleDescriptor>> loggedAgents;

	public OrganizationExecution(Context context,
								 CriticalityAnalysisExecution criticalityExecution, 
								 IDescriptor descriptor) {
		super(context, descriptor, criticalityExecution);
		this.loggedAgents = new HashMap<AgentIdentification, Set<RoleDescriptor>>();
		LOG.info("Contexto[" + context +"] da organização " + descriptor.getId());
	}

	public void stopExecution() {
	}

	public boolean shouldStop(Event event) {
		return false;
	}

	/**
	 * Returns the loged agents of this organization
	 * 
	 * @return - the loged agents
	 */
	public Set<AgentIdentification> getLogedAgents() {
		return loggedAgents.keySet();
	}

	/**
	 * Logs an agent into the organization, if it's alredy loged nothing is done
	 * 
	 * @param agentId -
	 *            the agent identification
	 * @return - true if the agent logs in the organization, false if its
	 *         already loged
	 */
	public boolean addLogedAgent(AgentIdentification agentId) {
		if (loggedAgents.containsKey(agentId)) {
			return false;
		}
		loggedAgents.put(agentId, new HashSet<RoleDescriptor>());
		return true;
	}

	public void removeLogedAgent(AgentIdentification agentId) {
		loggedAgents.remove(agentId);
	}

	public boolean isAgentLoged(AgentIdentification agentId) {
		return loggedAgents.containsKey(agentId);
	}

	/**
	 * The roles performed by the argued agent identification
	 * 
	 * @param aid -
	 *            the agent identification
	 * @return - a Set with all the roles performed by the agent, null if the
	 *         agent is not loged into the organization
	 */
	public Set<RoleDescriptor> getRolesPerformed(AgentIdentification aid) {
		return loggedAgents.get(aid);
	}

	/**
	 * Removes an role from an agent
	 * 
	 * @param aid -
	 *            the agent id
	 * @param role -
	 *            the role
	 * @return - true if the agent is loged in the organization and performs the
	 *         role argued. False if the agent is not loged or if it does not
	 *         perform the role argued
	 */
	public boolean removeRoleFromAgent(AgentIdentification aid,
			RoleDescriptor role) {
		Set<RoleDescriptor> roles = loggedAgents.get(aid);
		if (roles == null) {
			return false;
		}

		return roles.remove(role);

	}

	/**
	 * Adds a role to the agent
	 * 
	 * @param aid -
	 *            the agent id
	 * @param role -
	 *            the role to add
	 * @return - true if the agent is loged in the organization and does not
	 *         perform the role argued. False if the agent is not loged in the
	 *         organization, if it performs the role argued or if the role is
	 *         not declared in the organization
	 */
	public boolean addRoleToAgent(AgentIdentification aid, RoleDescriptor role) {

		if (((OrganizationDescriptor) getDescriptor()).hasRoleDescriptor(role
				.getId()) == false) {
			return false;
		}

		Set<RoleDescriptor> roles = loggedAgents.get(aid);
		if (roles == null) {
			return false;
		}

		return roles.add(role);
	}

	/**
	 * Inform if an agent performs a role in this organization. If the agent is
	 * not loged into this organization the return value will be false
	 * 
	 * @param agentId -
	 *            the agent identification
	 * @param role -
	 *            the role
	 * @return - true if the agent perform the role in this organization, false
	 *         if it does not perform or it is not loged in
	 */
	public boolean doesAgentPerformRole(AgentIdentification agentId,
			RoleDescriptor role) {
		Set<RoleDescriptor> roles = loggedAgents.get(agentId);
		if (roles == null) {
			return false;
		}

		return roles.contains(role);
	}
	
}

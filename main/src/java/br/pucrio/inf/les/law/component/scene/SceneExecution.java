package br.pucrio.inf.les.law.component.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.component.ElementExecution;
import br.pucrio.inf.les.law.component.criticality.CriticalityAnalysisExecution;
import br.pucrio.inf.les.law.component.criticality.ExternalObserver;
import br.pucrio.inf.les.law.component.message.RoleReference;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.protocol.ProtocolExecution;
import br.pucrio.inf.les.law.component.protocol.State;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.execution.IExecution.ExecutionState;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class SceneExecution extends ElementExecution {
	/**
	 * Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(SceneExecution.class);
	
	private ProtocolExecution protocol;
	
	protected Map<AgentIdentification, RoleDescriptor> loggedAgents;
	
	public SceneExecution(Context context, 
							ProtocolExecution protocolExecution,
							CriticalityAnalysisExecution criticalityExecution,
							IDescriptor descriptor) {
		super(context,descriptor,criticalityExecution);
        if (criticalityExecution!=null){
			criticalityExecution.setSceneExecution(this);
		}
		
		this.protocol = protocolExecution;
		this.loggedAgents = new HashMap<AgentIdentification, RoleDescriptor>();
		context.attachObserver(this,Masks.FAILURE_STATE_REACHED | Masks.SUCCESSFUL_STATE_REACHED | Masks.TIME_TO_LIVE_ELAPSED);
		
		LOG.info("Contexto[" + context +"] da scene " + descriptor.getId());
	}


	public boolean shouldStop(Event event) {
		return true;
	}

	public Map<AgentIdentification, RoleDescriptor> getLoggedAgents() {
		return this.loggedAgents;
	}

	/**
	 * Verifies if the agent represented by agentId with the role can enter this
	 * scene.<br>
	 * 
	 * @param agentId -
	 *            the agent identification
	 * @param role -
	 *            the role performed by the agent
	 * @param -
	 *            the organization execution where the agent perform the role
	 * @return - false<br>
	 *         if the agent does not perform the role, <br>
	 *         if it cannot loggin because all roles are being used, <br>
	 *         if the role is not avaiable for this scene <br>
	 *         or the current state is not valid<br>
	 *         true<br>
	 *         if the agent can enter the scene or <br>
	 *         if it is already inside it performing the argued role.
	 */
	public boolean canAgentEnter(AgentIdentification agentId,
			RoleDescriptor role, OrganizationExecution organizationExecution)
			throws LawException {
		

		// if the scene does not has the argued entrance role the agent will not enter
		SceneDescriptor sceneDescriptor = (SceneDescriptor) getDescriptor();
		if (sceneDescriptor.getEntranceRoles().containsKey(role) == false) {
			return false;
		}

		// if the agent has already entered the scene with this role nothing is
		// done
		if (loggedAgents.containsKey(agentId)
				&& loggedAgents.get(agentId).equals(role)) {
			return true;
		}


		// if the entrance role has reached its limmit, the agent will not enter
		// the scene
		int roleSceneLimit = sceneDescriptor.getRoleLimit(role);
		for (RoleDescriptor loggedRole : loggedAgents.values()) {
			if (role.equals(loggedRole)) {
				roleSceneLimit--;
			}
		}

		if (roleSceneLimit <= 0) {
			if (roleSceneLimit < 0) {
				String errorMsg = "The scene execution "
						+ this.getId()
						+ " from scene descriptor "
						+ sceneDescriptor.getId()
						+ " has execedded the entrance role limit for the role "
						+ role.getId();
				LOG.error(errorMsg);
				throw new LawException(errorMsg,
						LawException.INCONSISTENT_ROLES_LIMIT_IN_PROTOCOL_STATE);
			}
			return false;
		}

		// check if the scene current state allows the entrance of the agent
		Set<State> entranceStates = sceneDescriptor.getEntranceRoles()
				.get(role);
		for (State currentState : protocol.getCurrentState()) {
			if (entranceStates.contains(currentState)) {
				LOG.info("Acesso permitido no estado: "+currentState);
				return true;
			}
			LOG.info("Acesso negado no estado: "+currentState);
		}

		
		return false;

	}

	/**
	 * The agent enters the scene if the conditions of canEnterScene are true
	 * 
	 * @param agentId -
	 *            the agent Id
	 * @param role -
	 *            the role
	 * @return see canEnterScene
	 * @throws LawException
	 */
	public boolean enterScene(AgentIdentification agentId, RoleDescriptor role, OrganizationExecution organizationExecution)
			throws LawException {
		if (canAgentEnter(agentId, role,organizationExecution)) {
			loggedAgents.put(agentId, role);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Forces the entrace of an agent into the Scene. CAUTION! this method can
	 * lead the scene to inconsistent state, before its execution be sure the
	 * canAgentEnter method was properly verified
	 * 
	 * @param agentId -
	 *            the agent Id
	 * @param role -
	 *            the role the agent performs
	 */
	public void forceSceneEntrance(AgentIdentification agentId,
			RoleDescriptor role) {
		loggedAgents.put(agentId, role);
	}
	
	

	
	public void update(Event event) throws LawException {
		deactivateRoles();
		
		setExecutionState(ExecutionState.FINISHED);
		
		if (event.getType()==Masks.FAILURE_STATE_REACHED){
			context.fire(new Event(Masks.SCENE_FAILURE_COMPLETION,this.getId()));
			
		}else if(event.getType()==Masks.SUCCESSFUL_STATE_REACHED){
			context.fire(new Event(Masks.SCENE_SUCCESSFUL_COMPLETION,this.getId()));
		}else if(event.getType()==Masks.TIME_TO_LIVE_ELAPSED){
			context.fire(new Event(Masks.SCENE_TIME_TO_LIVE_ELAPSED,this.getId()));
		}
		context.stopExecution(this);
		
	}

	public void deactivateRoles(){
		for (Iterator i  = loggedAgents.keySet().iterator(); i.hasNext();) {
			AgentIdentification aid = (AgentIdentification) i.next();
			RoleDescriptor roleDescriptor = (RoleDescriptor)loggedAgents.get(aid);
			//firing role_deactivation event...
			RoleReference roleRef = new RoleReference();
			roleRef.setRoleDescriptor(roleDescriptor);
			roleRef.setRoleInstanceValue(aid);
			Event roleDeactivationEvent = new Event(roleDescriptor.getId(), Masks.ROLE_DEACTIVATION, new Id());
			roleDeactivationEvent.addEventContent(Event.ROLEREFERENCE, roleRef);
			LOG.info("Disparando evento role_deactivation para o agente [" + aid + "] na cena [" + this.getId() + "] para o papel [" + roleDescriptor.getId() + "].");
			context.fire(roleDeactivationEvent);
			//end firing.
		}
	}
	
	public boolean isAgentLoged(AgentIdentification agentId) {
		return loggedAgents.containsKey(agentId);
	}
	
	public ArrayList<AgentIdentification> getListAgentIdLoggedWithRole(String role){
		ArrayList<AgentIdentification> agents = new ArrayList<AgentIdentification>();
		AgentIdentification aid;
		RoleDescriptor roleDescriptor;
		for (Iterator i  = loggedAgents.keySet().iterator(); i.hasNext();) {
			aid = (AgentIdentification) i.next();
			roleDescriptor = (RoleDescriptor)loggedAgents.get(aid);
			if (roleDescriptor.getId().toString().equals(role)){
				agents.add(aid);
			}
		}
		return agents;
	}


}
package br.pucrio.inf.les.law.component.scene;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.component.ElementDescriptor;
import br.pucrio.inf.les.law.component.criticality.CriticalityAnalysisExecution;
import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.protocol.ProtocolDescriptor;
import br.pucrio.inf.les.law.component.protocol.ProtocolExecution;
import br.pucrio.inf.les.law.component.protocol.State;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

/**
 * Defines the structure of a scene.
 * 
 * @author Rodrigo
 */
public class SceneDescriptor extends ElementDescriptor {
	private static final Log LOG = LogFactory.getLog(SceneDescriptor.class);
	
    private ProtocolDescriptor protocolDescriptor;

    private long timeToLive;
    
    private Set<RoleDescriptor> creationRoles;
    
    private Map<RoleDescriptor, Set<State>> entranceRoles;
    
    private Map<RoleDescriptor, Integer> roleLimit;
    
    private OrganizationDescriptor organizationDescriptor;
    
   public SceneDescriptor(Id sceneId, long timeToLive) {
        super(sceneId);
        this.timeToLive = timeToLive;
        this.creationRoles = new HashSet<RoleDescriptor>();
        this.entranceRoles = new HashMap<RoleDescriptor, Set<State>>();
        this.roleLimit = new HashMap<RoleDescriptor, Integer>();
    }

    /**
     * @param protocolDescriptor
     *            The protocolDescriptor to set.
     */
    public void setProtocolDescriptor(ProtocolDescriptor protocolDescriptor) {
        this.protocolDescriptor = protocolDescriptor;
    }

    public IExecution createExecution(Context context,
            Map<String, Object> parameters) throws LawException {

        /*
         * Alhorithm: 1- Create all the instances that require to be running.
         * Example: Protocol 2- Enable all the triggers. Example: clocks and
         * norms. 3- Generate activation event 4- Return Execution instance.
         */

        // start the protocol execution
        ProtocolExecution protocolExecution = (ProtocolExecution) context
                .newInstance(protocolDescriptor.getId(), null);
        
        
        CriticalityAnalysisExecution criticalityExecution = null;
        if (getCriticalityAnalysisDescriptor() != null){
        	criticalityExecution = 
        		(CriticalityAnalysisExecution) context
        			.newInstance(getCriticalityAnalysisDescriptor().getId(), null);
        }

        enableTriggers(context);

        // Generate activation event
        context.fire(new Event(Masks.SCENE_CREATION, getId()));

        // return the scene execution
        return new SceneExecution(context, protocolExecution, criticalityExecution, this);
    }

    public boolean needContext() {
        return true;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public ProtocolDescriptor getProtocolDescriptor() {
        return protocolDescriptor;
    }
    
    public Set<RoleDescriptor> getCreationRoles()
    {
    	return this.creationRoles;
    }
    
    public void addCreationRole(RoleDescriptor role)
    {
    	this.creationRoles.add(role);
    }

    public boolean canCreateScene(AgentIdentification agentId, OrganizationExecution organizationExecution)
    {
    	if(!organizationExecution.isAgentLoged(agentId))
    	{
    		return false;
    	}
    	
    	for(RoleDescriptor role : organizationExecution.getRolesPerformed(agentId))
    	{
    		if(creationRoles.contains(role))
    		{
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Add a new role entrance set of states to the scene descriptor, if the scene already has the entrance role 
     * the states set is added to the previous set. No duplicated States will be added for the same roleDescriptor. 
     * @param role - the entrance role
     * @param states - the set of states to be add
     * @param limit - the limit of agents that can enter the scene with the argued role
     */     
    public void addEntranceRole(RoleDescriptor role, Set<State> states, Integer limit)
    {
    	Set<State> entranceRoleStates = entranceRoles.get(role);
    	if(entranceRoleStates == null)
    	{
    		entranceRoles.put(role,states);
    	}
    	else
    	{
    		entranceRoleStates.addAll(states);
    	}    	
    	setRoleLimit(role,limit);
    }
    
    public void addStateToEntranceRole(RoleDescriptor role, State state)
    {
    	Set<State> entranceRoleStates = entranceRoles.get(role);
    	if(entranceRoleStates == null)
    	{
    		LOG.warn("Someone attempted to add the State " + state.getId() + " to the entrance scene set for the " +
    				"role " + role.getId() + " while this role has no entry for the scene entrance of " + getId());
    		return;
    	}
    	
    	entranceRoleStates.add(state);
    }
    
    public Map<RoleDescriptor, Set<State>> getEntranceRoles()
    {
    	return this.entranceRoles;
    }
    
    public void setRoleLimit(RoleDescriptor role, Integer limit)
    {
    	this.roleLimit.put(role,limit);
    }
    
    public Integer getRoleLimit(RoleDescriptor role)
    {
    	return this.roleLimit.get(role);
    }

    public boolean shouldCreate(Context context, Map<String, Object> parameters) throws LawException {
        return true;
    }

	public OrganizationDescriptor getOrganizationDescriptor() {
		return organizationDescriptor;
	}

	public void setOrganizationDescriptor(
			OrganizationDescriptor organizationDescriptor) {
		this.organizationDescriptor = organizationDescriptor;
	}

}

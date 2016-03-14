package br.pucrio.inf.les.law.communication.asf;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import framework.FIPA.ElementID;
import framework.FIPA.communication.MTP;
import framework.agent.Agent;
import framework.agentRole.AgentRole;
import framework.environment.MTS_Environment;
import framework.mentalState.Plan;
import framework.mentalState.goal.Goal;
import framework.mentalState.goal.LeafGoal;
import framework.organization.MainOrganization;

public class AgentASF extends Agent
{
	private Vector messagesReceived = null;
	private Vector messagesToSend = new Vector();
	private int qtdReceived = 0;
	public static boolean mediator = false;
	private Vector auxSelecting = new Vector();
	public static int portClient;
	private boolean continueExecution = true;
	
	protected AgentASF(MTS_Environment theEnvironment, MainOrganization initialOrg, AgentRole initialRole, ElementID aid, MTP mtp) 
	{
		super(aid, mtp); 
        rolesBeingPlayed = new Vector ();
        organizations = new Vector ();
        goals = new Vector ();
        beliefs = new Vector ();
        plans = new Vector ();
        actions = new Vector ();
        inMessages = new Vector ();
        outMessages = new Vector ();
        
		this.environment = theEnvironment;
		this.environment.registerAgents(this);
		this.setRolesBeingPlayed(initialRole, initialOrg);
		messagesReceived = new Vector();
	}
	
	public void InformationToSend()
	{
		Iterator roles = rolesBeingPlayed.iterator();
	  	AgentRole role;
	  	
	  	while( roles.hasNext() )
	  	{
	  		role = ( AgentRole ) roles.next();
	  		
	  		if( role instanceof SenderRole )
	  		{	
	  			((SenderRole)role).setSenderGoal();
	  		}
	  	}
		LeafGoal objectGoal = null;
		Plan objectPlan = null;
		
		objectGoal = new LeafGoal ("boolean", "to_send_message", "true");
		objectGoal.setPriority(2);
		objectGoal.setGoalType("perform");
		this.goals.add (objectGoal);
						
		objectPlan = new  PlanToSend();
		objectPlan.setGoal (objectGoal);
		objectGoal.setPlan (objectPlan);
		this.plans.add (objectPlan);
		
	}
	
	public void InformationToReceive()
	{
		Iterator roles = rolesBeingPlayed.iterator();
	  	AgentRole role;
	  	while( roles.hasNext() )
	  	{
	  		role = ( AgentRole ) roles.next();
	  		
	  		if( role instanceof ReceiverRole )
	  		{
	  			((ReceiverRole)role).setReceiverGoal();
	  		}
	  	}
	  	
		//((Vector)goals).removeAllElements();
		//((Vector)plans).removeAllElements();
		LeafGoal objectGoal = null;
		Plan objectPlan = null;
		
		objectGoal = new LeafGoal ("boolean", "to_receive_message", "true");
		objectGoal.setPriority(2);
		objectGoal.setGoalType("perform");
		this.goals.add (objectGoal);
		
		objectPlan = new  PlanToReceive();
		objectPlan.setGoal (objectGoal);
		objectGoal.setPlan (objectPlan);
		this.plans.add (objectPlan);
		
	}
	
	protected boolean checkIfWillContinue() 
	{
		return continueExecution;
	}
	protected Plan selectingPlan(Collection vPlansExecuted, Goal goal) {
		Collection vAgentPlans = getPlans();
		Iterator enumAgentPlans = vAgentPlans.iterator();
		while (enumAgentPlans.hasNext()) {
			Plan agentPlan = (Plan)enumAgentPlans.next();
			Goal planGoal = agentPlan.getGoal();
			//um plano atinge um único goal
			//um goal pode ser atingido por vários planos
			if (goal.getName().equals(planGoal.getName()))
				if (!vPlansExecuted.contains(agentPlan))
					return agentPlan;
		}
		//All plans associated with the goal were executed or
		//there is not any plan associated with the goal
		return null;
	}
	protected void executingPlan(Plan plan) {
		  //The plan has to be executed in the context of the role being played
		  AgentRole role = getCurrentRole();
		  plan.execute(role);
	}
	
	protected synchronized Goal selectingGoalToAchieve() 
	{
		Goal goal;
		Goal goalRole;
		int num = 0;
		Iterator roles;
		AgentRole role;
		Iterator goalsRole;
		Vector goals = ( Vector ) this.goals;
		roles = this.rolesBeingPlayed.iterator();
		//	selected = ( String ) auxSelecting.get(0);
		
		while ( num < goals.size() )
		{
			goal = ( Goal ) goals.get( num );
			if( !goal.getAchieved() )
			{					
				while( roles.hasNext() )
				{
					role = ( AgentRole ) roles.next();
					
					if((Thread.currentThread().getName().equals( role.getRoleName() )))
					{
						goalsRole = role.getGoals().iterator();
						while(goalsRole.hasNext())
						{
							goalRole = ( Goal ) goalsRole.next();
							
							if( goal.getName().compareToIgnoreCase(goalRole.getName()) == 0 )
							{	
								goals.remove( goal );
								return goal;			
							}
							
						}
					}
				}
			}
			roles = this.rolesBeingPlayed.iterator();
			num++;
		}	
		
				
		return null;
	}
	private boolean allItsGoalsAchieved() 
	{
		return false;
	}
	
	private Collection verifyingAgentGoalsRoleGoals() 
	{
		//If not, the agent verifies if it is already playing a role that achieves one of its goals.
		Collection vAgentGoalsAssociatedWithRoles = new Vector();
		Collection vRoles = getRolesBeingPlayed();
		Iterator enumvRoles = vRoles.iterator();
		while (enumvRoles.hasNext()) {
			AgentRole roleAux = (AgentRole)enumvRoles.next();
			Collection vRoleGoals = roleAux.getGoals();
			Iterator enumvRoleGoals = vRoleGoals.iterator();
			while (enumvRoleGoals.hasNext()) {
				Goal roleGoal = (Goal)enumvRoleGoals.next();
				Collection vAgentGoals = getGoals();
				Iterator enumvAgentGoals = vAgentGoals.iterator();
				while (enumvAgentGoals.hasNext()) {
					Goal agentGoal = (Goal)enumvAgentGoals.next();
					if (!agentGoal.getAchieved())
						if (roleGoal.getName().equals(agentGoal.getName())) {
							//estes sao os goals que o agente nao atingiu e
							//que estao associados a papeis
							vAgentGoalsAssociatedWithRoles.add(agentGoal);
						}
				}
			}
		}
		Collection vAgentGoals = getGoals();
		Collection vAgentGoalsNotAssociatedWithRoles = new Vector();
		Iterator enumvAgentGoals = vAgentGoals.iterator();
		while (enumvAgentGoals.hasNext()) {
			Goal agentGoal = (Goal)enumvAgentGoals.next();
			if (!vAgentGoalsAssociatedWithRoles.contains(agentGoal) && !agentGoal.getAchieved()) {
				vAgentGoalsNotAssociatedWithRoles.add(agentGoal);
			}
		}
		//O vetor que sobra eh o vetor que tem os goals que nao estao relacionados aos roles
		return vAgentGoalsNotAssociatedWithRoles;
	}
	private boolean choosingAnotherRole(Collection vAgentGoals) 
	{
		//Changing the roles in order to try to achieve the goal not achieved
		return false;
	}

	public void setMessageToSend( framework.mentalState.Message msg )
	{
		this.messagesToSend.add( msg );
	}
	
	public Collection getMessagesToSend()
	{
		return this.messagesToSend;
	}
	
	public framework.mentalState.Message getnextMessageToSend() 
	{
		if ( this.messagesToSend.size() > 0 )
		{
			framework.mentalState.Message msg = ( framework.mentalState.Message ) this.messagesToSend.get( 0 );
			this.messagesToSend.remove( msg );
			return msg;
		}
		return null;
	}

	public Vector getMessagesReceived() 
	{
		return messagesReceived;
	}

	public void setMessagesReceived(Vector messagesReceived) {
		this.messagesReceived = messagesReceived;
	}
	
	public void addMessageReceived( framework.mentalState.Message message )
	{
		this.messagesReceived.add( message );
		this.qtdReceived++;
	}
	
	public int getQtdReceived()
	{
		return this.qtdReceived;
	}
	
	public void removeMessageReceived(framework.mentalState.Message message)
	{
		this.messagesReceived.remove( message );
	}
	
	public void removePlan( Plan plan )
	{
		this.plans.remove( plan );
	}

	public void setContinueExecution(boolean continueExecution) {
		this.continueExecution = continueExecution;
	}
	
	public void removeAll()
	{
		this.goals.removeAll( goals );
		this.plans.removeAll( plans );
		
	}
	
}

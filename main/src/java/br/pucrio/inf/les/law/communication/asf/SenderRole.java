package br.pucrio.inf.les.law.communication.asf;

import java.util.Vector;

import framework.agentRole.AgentRole;
import framework.mentalState.goal.LeafGoal;
import framework.organization.MainOrganization;

public class SenderRole extends AgentRole 
{
	public SenderRole(MainOrganization owner)
	{
		LeafGoal objectGoal = null;
		goals = new Vector ();
		
		super.setOwner (owner);
        owner.setAgentRole (this);
        /*
        objectGoal = new LeafGoal ("boolean", "to_send_message", "true");
        goals.add (objectGoal);
        objectGoal.setPriority(3);
        objectGoal.setGoalType("perform");
        */
	}
	
	public void setSenderGoal()
	{
		goals.removeAll( goals );
		LeafGoal objectGoal = null;
		
        objectGoal = new LeafGoal ("boolean", "to_send_message", "true");
        goals.add (objectGoal);
        objectGoal.setPriority(3);
        objectGoal.setGoalType("perform");
	}
}

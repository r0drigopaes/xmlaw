package br.pucrio.inf.les.law.communication.asf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import framework.FIPA.ElementID;
import framework.agent.Agent;
import framework.agentRole.AgentRole;
import framework.agentRole.Protocol;
import framework.mentalState.Message;
import framework.mentalState.Plan;
import framework.mentalState.goal.Goal;

public class PlanToSend extends Plan 
{
    public void execute (AgentRole role)
    {
		AgentASF agent = (AgentASF) role.getAgentPlayingRole();
		System.out.println(role.getRoleName()+"-> Executando plano "+this.getClass());
        
		int aux;
		boolean result = false;
		while( agent.checkIfWillContinue() )
		{
			if( ((AgentASF)agent).getMessagesToSend().size() > 0 )
			{
				ActionToSend action = new ActionToSend();
		        result = action.execute( agent );
		        setGoalAchieved(role, true);
		        break;
			}
			else
			{
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if( !agent.checkIfWillContinue() )
        {
        	agent.removeAll();
        }
    }
    
	private boolean checkIfStopped(AgentRole role)
	{
		return role.threadStopped();
	}
	private void checkIfSuspended(AgentRole role)
	{
		if (role.threadSuspended())
		{
			synchronized (this)	{
				while (role.threadSuspended())
					System.out.println("Suspended");
				System.out.println("Resumed");
			}
		}
	} 

	private boolean setGoalAchieved(AgentRole role, boolean achieved)
    {
   		//Conseguiu atingir o objetivo
        //Setando o goal do role para true
        goal.setAchieved(true); //goal associado ao plano
		Collection vGoals = role.getGoals();
		Iterator enumvGoals = vGoals.iterator();
		while (enumvGoals.hasNext()) {
			Goal roleGoal = (Goal)enumvGoals.next();
            Collection vRoleSubGoals = roleGoal.getSubGoals();
            if (vRoleSubGoals==null)
            {
				if (roleGoal.getName().equals(goal.getName())) {
					//setando o goal do role para achieved
					roleGoal.setAchieved(achieved);
					roleGoal.setTryedToAchieve(true);
                    return true;
				}
            }
            else
            {
				//GOAL COMPOSTO
				Iterator enumvSubGoals = vRoleSubGoals.iterator();
				while (enumvSubGoals.hasNext()) {
					Goal roleSubGoal = (Goal)enumvSubGoals.next();
					if (roleSubGoal.getName().equals(goal.getName())) {
						//setando o goal do role para achieved
						roleSubGoal.setAchieved(achieved);
						roleSubGoal.setTryedToAchieve(true);
                    }
                }
                //Verificando se todos os sub-goals foram atingidos ou tentou atingir
                //e setando o composite goal
                boolean allSubGoalsAchieved = true;
                boolean allSubGoalsTryedToAchieved = true;
                enumvSubGoals = vRoleSubGoals.iterator();
				while (enumvSubGoals.hasNext()) {
					Goal roleSubGoal = (Goal)enumvSubGoals.next();
					if (!roleSubGoal.getAchieved())
						allSubGoalsAchieved = false;
					if (!roleSubGoal.getTryedToAchieve())
						allSubGoalsAchieved = false;
                }
				roleGoal.setAchieved(allSubGoalsAchieved);
				roleGoal.setTryedToAchieve(allSubGoalsTryedToAchieved);
				return true;
            }
		}

		//Se tiver o goal do agente para true
		Agent agentPlayingRole = role.getAgentPlayingRole();
		Collection vAgentGoals = agentPlayingRole.getGoals();
		Iterator enumvAgentGoals = vAgentGoals.iterator();
		while (enumvAgentGoals.hasNext()) {
			Goal agentGoal = (Goal)enumvAgentGoals.next();
			Collection vAgentSubGoals = agentGoal.getSubGoals();
			if (vAgentSubGoals==null)
			{
				if (agentGoal.getName().equals(goal.getName())) {
					//setando o goal do agent para achieved
					agentGoal.setAchieved(achieved);
					agentGoal.setTryedToAchieve(true);
                    return true;
				}
			}
			else
			{
				//GOAL COMPOSTO
				Iterator enumvAgentSubGoals = vAgentSubGoals.iterator();
				while (enumvAgentSubGoals.hasNext()) {
					Goal agentSubGoal = (Goal)enumvAgentSubGoals.next();
					if (agentSubGoal.getName().equals(goal.getName())) {
						//setando o goal do role para achieved
						agentSubGoal.setAchieved(achieved);
						agentSubGoal.setTryedToAchieve(true);
                        return true;
					}
				}
                //Verificando se todos os sub-goals foram atingidos ou tentou atingir
                //e setando o composite goal
                boolean allSubGoalsAchieved = true;
                boolean allSubGoalsTryedToAchieved = true;
				enumvAgentSubGoals = vAgentSubGoals.iterator();
				while (enumvAgentSubGoals.hasNext()) {
					Goal agentSubGoal = (Goal)enumvAgentSubGoals.next();
					if (!agentSubGoal.getAchieved())
						allSubGoalsAchieved = false;
					if (!agentSubGoal.getTryedToAchieve())
						allSubGoalsAchieved = false;
                }
				agentGoal.setAchieved(allSubGoalsAchieved);
				agentGoal.setTryedToAchieve(allSubGoalsTryedToAchieved);
				return true;
			}
		}
        return false;
    }

    
}

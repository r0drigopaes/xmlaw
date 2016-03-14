package br.pucrio.inf.les.law.communication.asf;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import framework.FIPA.ElementID;
import framework.FIPA.communication.MTP;
import framework.environment.MTS_Environment;
import framework.mentalState.Plan;
import framework.mentalState.goal.Goal;
import framework.mentalState.goal.LeafGoal;
import framework.organization.MainOrganization;

public class Law_Organization extends MainOrganization
{
	
	public Law_Organization (MTS_Environment theEnvironment, ElementID aid, MTP mtp)
	{
		super(theEnvironment, aid, mtp);
		LeafGoal objectGoal = null;
		objectGoal = new LeafGoal ("boolean", "management_of_sellers", "true");
		objectGoal.setPriority(3);
		objectGoal.setGoalType("maintain");
		this.goals.add (objectGoal);
		
	}

	public boolean checkIfWillContinue ()
	{
		return false;
	}
	public Plan selectingPlan(Collection vPlansExecuted, Goal goal) {
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
	public void executingPlan(Plan plan) {
		 plan.execute(this);
	}
	public Goal selectingGoalToAchieve() {
		//STRATEGY:
		//Select its goals according to their priorities
		Goal goal = null;
		int priority = 0;
		Goal goalHighestPriority = null;
		//Selecting the higher priority goal of the role
		Collection vGoals = getGoals();
		Iterator enumvGoals = vGoals.iterator();
		while (enumvGoals.hasNext()) {
			goal = (Goal)enumvGoals.next();
			if ((!goal.getTryedToAchieve()) && (goal.getPriority() >= priority)) {
				goalHighestPriority = goal;
				priority = goal.getPriority();
			}
		}
		goal = goalHighestPriority;
		if (goal!=null)
		{
			//Verificando se o goal é composto
			//Selecionar um sub-goal de cada vez
			Collection vSubGoals = goal.getSubGoals();
			if (vSubGoals != null) {
				//GOAL COMPOSTO
				Iterator enumvSubGoals = vSubGoals.iterator();
				while (enumvSubGoals.hasNext()) {
					Goal subGoal = (Goal)enumvSubGoals.next();
					//só tenta atingir o goal uma única vez
					if ((!subGoal.getTryedToAchieve()) && (subGoal.getPriority() >= priority)) {
						goalHighestPriority = subGoal;
						priority = subGoal.getPriority();
					}
				}
			}
		}
		else
		{
			//Tentou atingir todos os goals
			//Verificar se tem algum goal que tentou atingir mas nao conseguiu
			vGoals = getGoals();
			enumvGoals = vGoals.iterator();
			while (enumvGoals.hasNext()) {
				goal = (Goal)enumvGoals.next();
				if ((!goal.getAchieved()))
					//Se tentou atingir mas nao conseguiu entao continuar tentando
					goal.setTryedToAchieve(false);
			}
			goalHighestPriority = selectingGoalToAchieve();
		}
		return goalHighestPriority;
	}
	public boolean allItsGoalsAchieved()
	{
		return false;
	}
	public Collection verifyingAgentGoalsRoleGoals()
	{
		//If not, the agent verifies if it is already playing a role that achieves one of its goals.
		Collection vAgentGoalsAssociatedWithRoles = new Vector ();
		Collection vAgentGoals = getGoals();
		Iterator enumvAgentGoals = vAgentGoals.iterator();
		while (enumvAgentGoals.hasNext()) {
			Goal agentGoal = (Goal)enumvAgentGoals.next();
			Iterator enumGoalAssociatedRoles = vAgentGoalsAssociatedWithRoles.iterator();
			while (enumGoalAssociatedRoles.hasNext()) {
				Goal agentGoalAssociatedRoles = (Goal)enumGoalAssociatedRoles.next();
				if (agentGoal.getName().equals(agentGoalAssociatedRoles.getName()))
				{
					vAgentGoalsAssociatedWithRoles.remove(agentGoalAssociatedRoles);
				}
			}
		}
		//O vetor que sobra eh o vetor que tem os goals que nao estao relacionados aos roles
		return vAgentGoalsAssociatedWithRoles;
	}
	public boolean choosingAnotherRole(Collection vAgentGoals)
	{
		//Changing the roles in order to try to achieve the goal not achieved
		return false;
	}
}

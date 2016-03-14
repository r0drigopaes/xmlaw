package br.pucrio.inf.les.law.communication.asf;

import framework.agent.Agent;
import framework.mentalState.Action;
import framework.mentalState.Message;

public class ActionToReceive extends Action 
{

	@Override
	public boolean execute() 
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean execute( Message msg, Agent agent ) 
	{
		String roleName = Thread.currentThread().getName();
	  	System.out.println(roleName+"-> Executando acao "+this.getClass());
	  	AgentASF agentAux = (AgentASF) agent;
	  	
	  	agentAux.addMessageReceived( msg );
	  	//((ReceiverRole)role).setReceiverGoal();
	  	agentAux.InformationToReceive();
	  	
	  	//System.err.println("Olha o id ai gente. " + msg.getConversationId());
	  	//System.err.println("Nome do receptor: " + ((ElementID) msg.getTo().get( 0 )).getName());
	  	/*
	  	Message msg2 = new Message( msg.getConversationId(), msg.getContent(), (ElementID) msg.getTo().get(0), aux );
    	msg2.setPerformative( msg.getPerformative() );
    	
	    MTP mtp = new MessageTransportProtocol();
		mtp.activateClient( 1500, msg2  );
		*/
		return true;
	}

}

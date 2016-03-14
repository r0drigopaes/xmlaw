package br.pucrio.inf.les.law.communication.asf;

import framework.FIPA.communication.MTP;
import framework.FIPA.communication.http.MessageTransportProtocol;
import framework.agent.Agent;
import framework.mentalState.Action;
import framework.mentalState.Message;

public class ActionToSend extends Action 
{
	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		return false;
	}
		
	public boolean execute( Agent agent ) 
	{
        String roleName = Thread.currentThread().getName();
  		System.out.println(roleName+"-> Executando acao "+this.getClass());
	    
  		Message msg = ( ( AgentASF )agent ).getnextMessageToSend();
  		//msg.setPerformative( "inform:message" );
	    
	    MTP mtp = new MessageTransportProtocol();
	    
	    if( ((AgentASF)agent).mediator )
	    {
	    	mtp.send( 1500, msg  );
	    }
	    else
	    {
	    	mtp.send( ASFCommunication.port, msg  );
	    }
	    ((AgentASF)agent).InformationToSend();
	    return true;
	}

}

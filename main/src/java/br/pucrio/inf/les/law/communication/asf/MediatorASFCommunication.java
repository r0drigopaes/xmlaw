package br.pucrio.inf.les.law.communication.asf;

import jade.core.Profile;

import java.util.Properties;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import framework.FIPA.communication.MTP;
import framework.FIPA.communication.http.MessageTransportProtocol;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.util.ConfigUtils;

public class MediatorASFCommunication extends ASFCommunication
{
	public Message waitRightMessage(String conversationId)
	{
		throw new NotImplementedException();	
	}
	
	@Override
	public void send(Message msg) 
	{
		framework.mentalState.Message message = this.messageWrapper.transform(msg);
		if( agent != null ) 
		{	
        	/*
			if ( agent.getMessagesToSend().size() == 0 )
			{
				((AgentASF)agent).InformationToSend();
			}
			*/
	        
	        agent.setMessageToSend( message );
	    }
	}
	
	public MediatorASFCommunication(String name)
	{
		super( true );	
		this.messageWrapper = new ASFMessageWrapper();
		createAgents( name );		
		
		MTP mtp = new MessageTransportProtocol();		
		mtp.activateServer( this.port );
	}

}

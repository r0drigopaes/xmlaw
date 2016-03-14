package br.pucrio.inf.les.law.communication.asf;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import framework.FIPA.communication.MTP;
import framework.FIPA.communication.http.MessageTransportProtocol;
import br.pucrio.inf.les.law.communication.Message;

public class AgentASFCommunication extends ASFCommunication 
{
	private ASFMessageWrapper aux = null;
	
	public Message waitRightMessage(String conversationId)
	{
		throw new NotImplementedException();	
	}
	
	public AgentASFCommunication( String name )
	{		
		super(false);
		createAgents( name );
		aux = ASFMessageWrapper.getInstance();
		
		if( !aux.isControl() )
		{
			MTP mtp = new MessageTransportProtocol();
			mtp.activateServer( portClient );
			aux.setControl( true );
		}
	}
	
	public synchronized void send(Message msg) 
	{		
		//	control = 3;			
		if( agent != null )
		{
			if ( agent.getMessagesToSend().size() == 0 )
			{
				((AgentASF)agent).InformationToSend();
			}
			
			aux.setNameSender( agent.getAgentName().getName() );
			agent.setMessageToSend( aux.transform( msg ) );	
						
		}
		//agent.InformationToSend();
		
	}

}

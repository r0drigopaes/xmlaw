package br.pucrio.inf.les.law.communication.asf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import sun.awt.windows.WWindowPeer;

import jade.lang.acl.ACLMessage;
import br.pucrio.inf.les.law.communication.ICommunication;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.util.ConfigUtils;
import framework.FIPA.AMS;
import framework.FIPA.AgentPlataformDescription;
import framework.FIPA.ElementID;
import framework.FIPA.communication.MTP;
import framework.FIPA.communication.http.MessageTransportProtocol;
import framework.agent.Agent;
import framework.agentRole.AgentRole;
import framework.environment.MTS_Environment;
import framework.organization.MainOrganization;

public abstract class ASFCommunication implements ICommunication
{
	private ElementID elementId = null;
	private static MainOrganization mainOrg = null;
	protected AgentASF agent = null;
	protected ASFMessageWrapper messageWrapper;
	protected static MTS_Environment env;
	protected int control = 0;
	protected int lap = 0;
	private static int numbers = 0;
	private static int num = 0;
	/*
	public static String host;
	public static String plataformId;
	public static String port;
	*/
	public static String hostMediator= "localhost";
	protected String plataformId;
	
	//Porta do mediador da aplicação
	public static int port = 1400;
	//porta do cliente que deseja acessar o mediador
	protected int portClient = 1500;
	public static String host= "localhost";
	
	
	private synchronized  int verifyNumbers()
	{
		if( numbers < 1 )
		{
			numbers = 1;
		}
		else
		{
			numbers = 2;
		}
		return numbers;	
	}
	
	protected ASFCommunication(boolean value)
	{	
		AgentASF.mediator = value;
		Properties properties = ConfigUtils.loadProperties(ConfigUtils.getConfigDir() + "ASFPlatform.properties");
		/*
		ASFCommunication.host = properties.getProperty("host");
		ASFCommunication.plataformId = properties.getProperty("plataform-id");
		ASFCommunication.port = properties.getProperty("port");
		*/
		hostMediator = properties.getProperty("host");
		plataformId = properties.getProperty("plataform-id");
		String port = properties.getProperty("port");
		String portClient = properties.getProperty("portClient");
		this.port = Integer.parseInt(port);
		if(!AgentASF.mediator)
		{
			this.portClient = Integer.parseInt(portClient);
			AgentASF.portClient = this.portClient;
		}
		
		if(AgentASF.mediator)
		{
			this.host = this.hostMediator;
		}
		
		if( verifyNumbers() == 1 )
		{
			AMS ams = AMS.getInstance();
			AgentPlataformDescription apDescription = AgentPlataformDescription.getInstance();
			apDescription.setName( this.plataformId );
			
			if (( elementId = ams.createEnvironmentElementId("Law_environment", true) ) != null )
		    {			
				env = new Law_Environment( elementId );
				ams.createDescription( env, elementId, "" );
				elementId.setAddress( host );	

				System.out.println("Ambiente " + elementId.getName() + " criado.");
			}
			
			//this.elementId = null;
			// Main-Organization
			if ( ( elementId = ams.createOrganizationElementId("Law_organization", true) ) != null )
			{
				MTP mtp = new MessageTransportProtocol();
				mainOrg = new Law_Organization (env, elementId, mtp);
				ams.createDescription( mainOrg, elementId, "" );
				elementId.setAddress( host );	
				Thread mainOrgThread = new Thread(mainOrg, "Law_organization");
				System.out.println("Organização " + elementId.getName() + " criada.");
				//mainOrgThread.start();
			}

		}
	}

	public static synchronized int num()
	{
		int control = num;
		
		num++;
		
		return control;
	}
	
	public void createAgents( String name )
	{
		if ( this.mainOrg != null )
		{
			AgentRole role = null;
			this.elementId = null;
			//int numberOfRoles;
			ElementID elementIdRole = null;
			AMS ams = AMS.getInstance();
			//int num = ASFCommunication.num();
			
			if ( ( elementId = ams.createAgentElementId( name, true ) ) != null )
			{	
				role = new SenderRole (mainOrg);
				//numberOfRoles = mainOrg.getAgentRoles().size();
				
				elementIdRole = new ElementID("Sender "+ name +"::Communicator", true);
				role.setRoleName(elementIdRole);
				elementId.setAddress( host );
				MTP mtp = new MessageTransportProtocol();
				agent = new AgentASF( env, mainOrg, role ,elementId, mtp );
				agent.createAgentRoleDescription( role, elementIdRole, "" );
				ams.createDescription( agent, elementId, "" );
											
				Thread agentSenderThread = new Thread(agent,role.getRoleName());
				role.setAgent (agent);
				agent.InformationToSend();
				agentSenderThread.start();
			
			}
			
			if ( ( elementIdRole = agent.createAgentRoleElementID( "Receiver " + name + "::Communicator", true ) ) != null )
			{	
				role = new ReceiverRole( mainOrg );
				role.setRoleName(elementIdRole);
				agent.setRolesBeingPlayed(role,mainOrg);
				agent.createAgentRoleDescription( role, elementIdRole, "" );
				
				elementIdRole.setAddress( host );
				
				Thread agentReceiverThread = new Thread(agent,role.getRoleName());
				role.setAgent (agent);
				agent.InformationToReceive();
				agentReceiverThread.start();
			}			
		}		
	}
	
	public abstract void send( Message msg );
	
	public Message waitForMessage()
	{
		framework.mentalState.Message msg = null;
        ASFMessageWrapper wrapper = ASFMessageWrapper.getInstance();
        while ( agent == null ) 
        {
            try 
            {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        msg = getMessage();
        Message realMessage = wrapper.transform( msg );

        return realMessage;
	}
	
	public synchronized framework.mentalState.Message getMessage()
	{
        Collection newMessages = agent.getMessagesReceived();
        //System.out.println("----------Qtd mensagens recebidas" + newMessages.size());
        //System.out.println("waitForMessage2");
        while (newMessages.size() == 0)
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            newMessages = agent.getMessagesReceived();
        }
        
        framework.mentalState.Message msg = (framework.mentalState.Message) ( ( Vector )newMessages).get( newMessages.size() - 1 );
        
        if(agent.mediator)
        {
        	agent.removeMessageReceived( msg );
        }
        
        return msg;
	}

	public synchronized void removeReceivedMessage( Message msg )
	{
		 Iterator i = agent.getMessagesReceived().iterator();
		 framework.mentalState.Message message;
		 while(i.hasNext())
		 {
			 message = (framework.mentalState.Message) i.next();
			 
			 if(	message.getConversationId() != null && 
					message.getConversationId().compareToIgnoreCase( msg.getConversationId() ) == 0 )
			 {
				 agent.removeMessageReceived( message );
				 break;
			 }
			 
		 }
		
	}
	
	public Message waitForMessage( long milliseconds ) 
	{
		framework.mentalState.Message msg = null;
        ASFMessageWrapper wrapper = ASFMessageWrapper.getInstance();
        while ( agent == null ) 
        {
            try 
            {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        msg = getMessageTime();
        Message realMessage = wrapper.transform( msg );

        return realMessage;
	}
	public synchronized framework.mentalState.Message getMessageTime()
	{
        int increment = 250;
        long elapsed = 0;
        Collection newMessages = agent.getMessagesReceived();
        //System.out.println("----------Qtd mensagens recebidas" + newMessages.size());
        //System.out.println("waitForMessage2");
        while (newMessages.size() == 0)
        {
            try {
                Thread.sleep(elapsed);
                elapsed += increment;
            } catch (InterruptedException e) {
            }
            newMessages = agent.getMessagesReceived();
        }
        
        framework.mentalState.Message msg = (framework.mentalState.Message) ( ( Vector )newMessages).get( newMessages.size() - 1 );
        
        if(agent.mediator)
        {
        	agent.removeMessageReceived( msg );
        }
        
        return msg;
	}
	
	private synchronized int controlIndex( Collection newMessages )
	{
        if ( lap < newMessages.size() )
        {
        	lap++;
        }
        else if( lap == newMessages.size() )
        {
        	lap = 0;
        	control++;
        }
        
        return control;
	}
	
	public Message nextMessage() 
	{
        framework.mentalState.Message msg = null;
        Collection newMessages = agent.getInMessages();
		
        msg = (framework.mentalState.Message) ( ( Vector )newMessages).get( 0 );
        
        ASFMessageWrapper wrapper = ASFMessageWrapper.getInstance();
        Message message = wrapper.transform( msg );
        
        return message;
		
	}

	public void close() 
	{
		agent.setContinueExecution( false );
	}
	
}

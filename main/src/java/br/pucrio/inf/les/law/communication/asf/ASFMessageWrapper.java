package br.pucrio.inf.les.law.communication.asf;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import br.pucrio.inf.les.law.communication.AgentIdentification;

import framework.FIPA.ElementID;
import framework.mentalState.Message;


public class ASFMessageWrapper 
{
	private static ASFMessageWrapper singletonInstance = null;
	private br.pucrio.inf.les.law.communication.Message messageASF;
	private boolean control = false;
	private String nameSender;

	public String getNameSender() {
		return nameSender;
	}

	public void setNameSender(String nameSender) {
		this.nameSender = nameSender;
	}

	public boolean isControl() {
		if(!control)
		{
			control = true;
			return false;
		}
		return control;
	}

	public void setControl(boolean control) {
		this.control = control;
	}

	public br.pucrio.inf.les.law.communication.Message getMessageASF() {
		return messageASF;
	}

	public void setMessageASF(br.pucrio.inf.les.law.communication.Message messageASF) {
		this.messageASF = messageASF;
	}

	public static ASFMessageWrapper getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new ASFMessageWrapper();
        }
        return singletonInstance;
    }
    
    public Message transform( br.pucrio.inf.les.law.communication.Message msg )
    {
    	String name= null;
    	ElementID receiver = null;
    	ElementID sender = null;
    	/*
    	if(msg.getSender().getName() == null)
    	{
    		name = "lucena";
    		receiver = new ElementID( msg.getReceiver().getName() );
    		receiver.setAllAddresses( msg.getReceiver().getAllAddresses() );
    		sender = new ElementID( name, true );
    		sender.setAddress( ASFCommunication.host );
        }
    	else 
    	*/
    	if(!AgentASF.mediator)
    	{
    		name = msg.getSender().getName();
    		sender = new ElementID( name, true );
    		sender.setAddress( ASFCommunication.host );
    		receiver = new ElementID("lucena", true );
    		receiver.setAddress( ASFCommunication.hostMediator );
    	}
    	else if(msg.getSender().getName() != null)
    	{
    		receiver = new ElementID( msg.getFirstReceiver().getName() );
    		receiver.setAllAddresses( msg.getFirstReceiver().getAllAddresses() );
    		sender = new ElementID( msg.getSender().getName() );
    		sender.setAllAddresses( msg.getSender().getAllAddresses() );
    		
    	}
    	
    	//ElementID id = new ElementID( msg.getReceiver().getName() );
    	Message message = new Message( msg.getConversationId(), msg.getContent(), sender, receiver );
    	if(msg.getFirstReceiver() != null)
    	{
    		String nameRealReceiver = msg.getFirstReceiver().getName();
        	Collection addresses = msg.getFirstReceiver().getAllAddresses();
        	
        	StringTokenizer aux = new StringTokenizer(nameRealReceiver, "@");
        	int num = 0;
        	while( aux.hasMoreTokens() )
        	{
        		aux.nextToken();
        		num++;
        	}
        	ElementID idRealReceiver;
        	if( num == 2 )
        	{
        		idRealReceiver = new ElementID( nameRealReceiver );
        	}
        	else
        	{
        		idRealReceiver = new ElementID( nameRealReceiver, true );
        	}
        	idRealReceiver.setAllAddresses( msg.getFirstReceiver().getAllAddresses() );
        	if(idRealReceiver.getAddresses().size() == 0)
        	{
        		idRealReceiver.setAddress( ASFCommunication.host );
        	}
        	message.setReplyTo( idRealReceiver );        	
    	}
    	
    	message.setProtocol( msg.getProtocol() );
    	//message.setPerformative( "inform:message" );
    	message.setPerformative( msg.getPerformative() );
    	
    	return message;
    }
    
    
    public br.pucrio.inf.les.law.communication.Message transform( Message msg )
    {
    	br.pucrio.inf.les.law.communication.Message message= new br.pucrio.inf.les.law.communication.Message(msg.getPerformative());
    	message.setConversationId( msg.getConversationId() );
    	message.setContent( (Map) msg.getContent() );
    	
    	List listId = msg.getReplyTo();
    	
    	ElementID replyTo = null;
    	if( listId.size() > 0 )
    	{
    		replyTo = (ElementID) listId.get( 0 );
    	}
    	
    	//AgentIdentification aid = new AgentIdentification("teste"elementId.getName());
    	AgentIdentification aid = new AgentIdentification(msg.getFrom().getName());
    	addAllAddresses( aid, msg.getFrom() );
    	message.setSender( aid );
    	if( replyTo == null )
    	{
    		replyTo =  (ElementID) msg.getTo().get(0);
    	}
    	aid = new AgentIdentification( replyTo.getName() );   	
    	addAllAddresses( aid, replyTo );
    	message.addReceiver( aid , "");
    	message.setProtocol( msg.getProtocol() );
    	
    	return message;
    }
    
    public void addAllAddresses( AgentIdentification aid, ElementID elementId )
    {
    	Iterator list = elementId.getAddresses().iterator();
    	
    	while( list.hasNext() )
    	{
    		aid.addAddresses( ( String ) list.next() );
    	}
    }
    
}

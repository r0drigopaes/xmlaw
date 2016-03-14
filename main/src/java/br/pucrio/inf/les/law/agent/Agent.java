package br.pucrio.inf.les.law.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.CommunicationProvider;
import br.pucrio.inf.les.law.communication.ICommunication;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;

public abstract class Agent extends Thread{
	
	private static final Log LOG = LogFactory.getLog(Agent.class);

    /**
	 * Camada de comunicação do agente.
	 * @label channel
	 */
	//protected static ICommunication communication;
	protected ICommunication communication;
	
	/**
	 * Identificador deste agente.
	 */
	protected AgentIdentification myAid;	
	
	protected LawFacade lawFacade;
	
	public Agent(String name) {
		try {
			/* instancia camada de comunicação */			
			
			this.communication = CommunicationProvider.agentCommunication(name);
			
			
			this.myAid 		= new AgentIdentification(name);
			this.lawFacade = new LawFacade(this);
			
		
		} catch (Exception e) {
			LOG.error("It was not possible create the agent ["+name+"]",e);
		}
		//Log.log.debug("Agent ["+name+"] created!");
	}	
	
	
	
	protected void finalize() throws Throwable {
		super.finalize();
		communication = null;
	}
	/**
	 * Faz o agente dormir por algum tempo.
	 * 
	 * @param millis o número de milisegundos que o agente irá dormir.
	 * 
	 */
	protected void sleepFor(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the agent identification.
	 * 
	 * @return
	 * @pre
	 * @pos
	 * @hipothesis
	 * @restrictions
	 * @author Rodrigo
	 */
	public AgentIdentification getAgentIdentification(){
		return myAid;
	}
	
	/**
	 * Returns the communication channel used by the agent.
	 * 
	 * @return
	 * @pre
	 * @pos
	 * @hipothesis
	 * @restrictions
	 * @author Rodrigo
	 */
	public ICommunication getCommunicationChannel(){
		return communication;
	}
	
	public void send(Message msg,String role){
        msg.setSender(myAid,role);
		communication.send(msg);		
	}
	
	public void send(Message msg){
		communication.send(msg);		
	}

	public Message nextMessage() {
		return communication.nextMessage();
	}
	public Message waitForMessage() {
		return communication.waitForMessage();
	}
	public Message waitForMessage(long milliseconds) {
		return communication.waitForMessage(milliseconds);
	}

	public LawFacade getMediatorFacade() {
		return this.lawFacade;
	}
	public AgentIdentification getMyAid() {
		return myAid;
	}

	public LawFacade getLawFacade() {
		return lawFacade;
	}
	
	public void analizeReply(Message reply)
    {
        if(reply.getPerformative().equals(Message.FAILURE))
        {
        	LOG.info(reply.getContentValue(MessageContentConstants.KEY_FAILURE));
        	LOG.info(reply.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));        	
        }
    }
}
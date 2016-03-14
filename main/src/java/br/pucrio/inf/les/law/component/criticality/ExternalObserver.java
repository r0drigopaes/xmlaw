package br.pucrio.inf.les.law.component.criticality;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.socket.SocketCommunication;
import br.pucrio.inf.les.law.component.ElementExecution;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IExecution.ExecutionState;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class ExternalObserver implements IObserver{
	
	private static final Log LOG = LogFactory.getLog(ExternalObserver.class);
	
	private AgentIdentification aid;
	private String agentName;
	private String address;
	private int eventType;
	private int portNumber;
	private SocketCommunication socket;
	private Context context;
	private ElementExecution execution;
	
	
	public ExternalObserver(AgentIdentification aid,
							String agentName,
							Context context,
							ElementExecution execution,
							int eventType,
							String address,
							String portNumber) throws LawException {
		
		this.aid = aid;
		this.agentName = agentName;
		this.address = address;
		this.eventType = eventType;
		this.portNumber = Integer.parseInt(portNumber);
		this.context = context;
		this.execution = execution;
		
		if ("".equals(Masks.getName(eventType))){
			throw new LawException("O tipo de evento a ser observado não existe.",LawException.EVENT_TYPE_DOESNT_EXIST);
		}else{
			context.attachObserver(this,eventType);
			LOG.info("ExternalObserver subcribed for events: " + Masks.getName(eventType));
		}
		
	}
	

	public void update(Event event) throws LawException {
		
		if (context!=null){
			
			if (event.getEventContent("agentId").equals(this.aid)){
				LOG.info("ExternalObserver receiving event " + event + " from infoevento["+ (String)event.getEventContent("infoEvento") +"] in the context " + this.context + " to agent[" + this.aid +"]");
				
				Message msg = new Message(Message.INFORM);
				msg.setContentValue("criticalityValue",String.valueOf(event.getEventContent("value")));
				msg.setContentValue("agentId",this.aid.toString());
				msg.setContentValue("agentName",this.agentName);
				msg.setContentValue("operation",(String)event.getEventContent("operation"));
				msg.setContentValue("infoEvento",(String)event.getEventContent("infoEvento"));
			
				socket = new SocketCommunication();
				//socket.send(msg,address,portNumber);
				socket.send(msg,"localhost",portNumber);
				LOG.info("ExternalObserver sent message " + msg + " through the socket [" + socket + "] to adress: "+ address + ", and port number: " + portNumber +"]");
				socket.stop();
			}
		}
		
	}


	public SocketCommunication getSocket() {
		return socket;
	}


	public void notifySubjectDeath(Subject subject) {
		LOG.info("O componente da maira nao tem mais contexto.");
		context=null;
		
	}

}

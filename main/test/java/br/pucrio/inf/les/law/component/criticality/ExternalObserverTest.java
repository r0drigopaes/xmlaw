package br.pucrio.inf.les.law.component.criticality;

import br.pucrio.inf.les.law.XMLawScenario;
import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.socket.SocketCommunication;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.model.Id;

public class ExternalObserverTest extends XMLawScenario{
	
	private ExternalObserver ext;
	private SocketCommunication remoteSocket;
	private AgentIdentification agentId;

	public void setUp() throws Exception{
		super.setUp();
		remoteSocket = new SocketCommunication(0);
		int portNumber = remoteSocket.getPort();
		agentId = new AgentIdentification("maira");
		ext = new ExternalObserver(agentId, "maira",
						sceneExecution.getContext(),
						sceneExecution,
						Masks.UPDATE_CRITICALITY,
						"localhost",String.valueOf(portNumber)); 
	}
	
	public void testExternalObserver(){
		Event event = new Event(Masks.UPDATE_CRITICALITY,new Id("criticalityAnalysis"));
		event.addEventContent("agentId",agentId);
		event.addEventContent("value",0.3);
		event.addEventContent("operation","+");
		
		sceneExecution.getContext().fireEventInContext(event);
		
		assertTrue( ext.getSocket().getPort() == 9999);
		
		Message msg = remoteSocket.receiveMessage();
		assertTrue( msg != null);
		
		assertTrue( msg.getContentValue("criticalityValue").equals("0.3"));
		assertTrue( msg.getContentValue("operation").equals("+"));
		
	}


}

package br.pucrio.inf.les.law.component.criticality;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import darx.MonitoringAgents.XMLawMonitoringAgents.XMLawCommunicationBasedMonitor;

import br.pucrio.inf.les.law.execution.AbstractExecution;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.criticality.EventDescriptor;
import br.pucrio.inf.les.law.component.message.RoleReference;
import br.pucrio.inf.les.law.component.scene.SceneExecution;


public class CriticalityAnalysisExecution extends AbstractExecution implements IObserver{
	private static final Log LOG = LogFactory.getLog(CriticalityAnalysisExecution.class);
	
	private ExecutionState executionState = ExecutionState.RUNNING ;
	
	private SceneExecution sceneExecution;
	
	private static final double MESSAGE = 0.05;
	private static final double ROLE = 0.1; 
	private static final double CLOCK = 0.4;
	private static final double NORM = 0.4;
	private static final double TRANSITION = 0.05;
	
	private int nbActiveRoles = 0;

	//Tabela com os pesos
	private List<Weight> weightList = null;
	// Lista de eventos que aumentam a criticalidade
	private List<EventDescriptor> increaseDescriptorList = null;
	// Lista de eventos que diminuem a criticalidade
	private List<EventDescriptor> decreaseDescriptorList = null;
	
	protected CriticalityAnalysisExecution(Context context, 
										   IDescriptor descriptor,
										   List<Weight> weightList,
										   List<EventDescriptor> increaseDescriptorList, 
										   List<EventDescriptor> decreaseDescriptorList) {
		super(context, descriptor);
		setDescriptor(descriptor);
		this.weightList = weightList;
		this.increaseDescriptorList = increaseDescriptorList;
		this.decreaseDescriptorList = decreaseDescriptorList;
		
		checkWeightList();
		
		for (int i = 0; i < increaseDescriptorList.size(); i++) {
			EventDescriptor incDesc = (EventDescriptor)increaseDescriptorList.get(i);
			context.attachObserver(this,incDesc.getEventType());
		}
		
		for (int i = 0; i < decreaseDescriptorList.size(); i++) {
			EventDescriptor decDesc = (EventDescriptor)decreaseDescriptorList.get(i);
			context.attachObserver(this,decDesc.getEventType());
		}
		
		//adicionando para escutar os eventos de message_arrival
		context.attachObserver(this,Masks.MESSAGE_ARRIVAL);
	}
	
	/**
	 * This method verifies if the Law Designer specified the values of the weights
	 * related to roles, clocks and norms. 
	 * If there is one missing, the system automatically include a default value.
	 * This method also includes the weight related to the message_arrival events.
	 * The Law Designer is not allowed to specify the weight related to the message_arrival events.
	 * @author mgatti
	 *
	 */
	public void checkWeightList(){
		if (this.weightList.size()<5){
			if (!checkWeightByRef("role")){
				Weight weight = new Weight("role",CriticalityAnalysisExecution.ROLE);
				this.weightList.add(weight);
			}
			if (!checkWeightByRef("clock")){
				Weight weight = new Weight("clock",CriticalityAnalysisExecution.CLOCK);
				this.weightList.add(weight);
			}
			if (!checkWeightByRef("norm")){
				Weight weight = new Weight("norm",CriticalityAnalysisExecution.NORM);
				this.weightList.add(weight);
			}
			if (!checkWeightByRef("transition")){
				Weight weight = new Weight("transition",CriticalityAnalysisExecution.TRANSITION);
				this.weightList.add(weight);
			}
			if (!checkWeightByRef("message")){
				Weight weight = new Weight("message",this.MESSAGE);
				this.weightList.add(weight);
			}
		}
	}
	
	public void update(Event event) throws LawException {
		
		int eventType = event.getType();
		Id eventId = event.getEventId();
		LOG.info("Recebendo evento: "+
				event+"id["+event.getEventId()+"], generatorId["+
				event.getEventGeneratorId()+"]");
		String infoEvento = "eventId[" + eventId.toString() + "],evenType["+Masks.getName(eventType)+"]";
		
		if (eventType == Masks.MESSAGE_ARRIVAL){
			
			updateAgentCriticalityThroughMessageArrival(event);
			
		}else{
			
			if (eventType == Masks.TRANSITION_ACTIVATION){
				int a = 0;
			}
			
			if (eventType == Masks.ROLE_ACTIVATION){
				this.nbActiveRoles += 1;
			}else if (eventType == Masks.ROLE_DEACTIVATION){
				this.nbActiveRoles -= 1;
			}
			
			AgentIdentification agentId = null;
			RoleReference roleRef = null;;
			if ((eventType == Masks.NORM_ACTIVATION)  	|| 
				(eventType == Masks.NORM_DEACTIVATION)  ||
				(eventType == Masks.CLOCK_ACTIVATION) ||
				(eventType == Masks.CLOCK_DEACTIVATION) ||
				(eventType == Masks.CLOCKTICK) 			||
				(eventType == Masks.CLOCK_TIMEOUT) ){
				agentId = (AgentIdentification) event.getContent().get("assigneeId");
			}else{
				roleRef = (RoleReference) event.getContent().get(
						Event.ROLEREFERENCE);
				if (roleRef!=null){
					agentId = roleRef.getRoleInstanceValue();
				}
			}
			
			if ((roleRef == null )&& (agentId == null)){
				double weightValue = getWeightByEventType(eventType);
				String role;
				ArrayList<AgentIdentification> agents;
				for (EventDescriptor incDesc : increaseDescriptorList) {
					if (incDesc.isListening(eventType, eventId)) {
						for (RoleReference assignee : incDesc.getAssignees()) {
							role = assignee.getRoleDescriptor().getId().toString();
							agents = this.getSceneExecution().getListAgentIdLoggedWithRole(role);
							if (agents!=null){
								for (AgentIdentification agentIdentification : agents) {
									LOG.info("Updating the criticality of agent ["+ agentIdentification+ "] through the event["+event.getEventId()+"] type[" + eventType + "] which weight is " + weightValue + " and the value associated is " + incDesc.getValue());
									updateAgentCriticality(agentIdentification, weightValue, incDesc.getValue(), "+",infoEvento);
								}	
							}else{
								throw new LawException("Não existe nenhum agente logado na cena com o papel " + role + 
												" para que a criticalidade seja alterada.",LawException.MISSING_ROLEREFERENCE_IN_EVENT_CONTENT);
							}
							agents = null;
						}
					}
				}//fim for
				
				for (EventDescriptor decDesc : decreaseDescriptorList) {
					if (decDesc.isListening(eventType, eventId)) {
						for (RoleReference assignee : decDesc.getAssignees()) {
							role = assignee.getRoleDescriptor().getId().toString();
							agents = this.getSceneExecution().getListAgentIdLoggedWithRole(role);
							if (agents!=null){
								for (AgentIdentification agentIdentification : agents) {
									LOG.info("Updating the criticality of agent ["+ agentIdentification+ "] through the event["+event.getEventId()+"] type[" + eventType + "] which weight is " + weightValue + " and the value associated is " + decDesc.getValue());
									updateAgentCriticality(agentIdentification, weightValue, decDesc.getValue(), "-",infoEvento);
								}
							}else{
								throw new LawException("Não existe nenhum agente logado na cena com o papel " + role + 
										" para que a criticalidade seja alterada.",LawException.MISSING_ROLEREFERENCE_IN_EVENT_CONTENT);
							}
							agents = null;
						}
					}
				}//fim for	
			}else if (agentId != null){
			
				double weightValue = getWeightByEventType(eventType);
				
				for (EventDescriptor incDesc : increaseDescriptorList) {
					if (incDesc.isListening(eventType, eventId)) {
						LOG.info("Updating the criticality of agent ["+ agentId+ "] through the event["+event.getEventId()+"] type[" + eventType + "] which weight is " + weightValue + " and the value associated is " + incDesc.getValue());
						updateAgentCriticality(agentId, weightValue, incDesc.getValue(), "+",infoEvento);
					}
				}//fim for
				
				for (EventDescriptor decDesc : decreaseDescriptorList) {
					if (decDesc.isListening(eventType, eventId)) {
						LOG.info("Updating the criticality of agent ["+ agentId+ "] through the event["+event.getEventId()+"] type[" + eventType + "] which weight is " + weightValue + " and the value associated is " + decDesc.getValue());
						updateAgentCriticality(agentId, weightValue, decDesc.getValue(), "-",infoEvento);
					}
				}//fim for			
			}else{
				LOG.info("The agentId could not be found in order to update the agent criticality.");
			}
		}//fim if-else

		
	}
	
	public double getWeightByEventType(int eventType){

		if (eventType == Masks.MESSAGE_ARRIVAL){
			return getWeightByRef("message");
		}else if ( (eventType == Masks.CLOCK_ACTIVATION)   ||
				   (eventType == Masks.CLOCK_DEACTIVATION) ) {
			return getWeightByRef("clock");
		}else if ( (eventType == Masks.NORM_ACTIVATION)   ||
				   (eventType == Masks.NORM_DEACTIVATION) ) {
			return getWeightByRef("norm");
		}else if ( (eventType == Masks.ROLE_ACTIVATION)   ||
				   (eventType == Masks.ROLE_DEACTIVATION) ) {
			return getWeightByRef("role");
		}else if (eventType == Masks.TRANSITION_ACTIVATION){
			return getWeightByRef("transition");
		}else{
			return 0;
		}

	}
	
	public double getWeightByRef(String ref){
		for (Weight weight : weightList) {
			if (weight.getRef().equals(ref)){
				return weight.getValue();
			}
		}
		return 0;
	}
	
	public boolean updateAgentCriticality(AgentIdentification agentId, 
										  double weightValue, 
										  double value, 
										  String operation,
										  String infoEvento){
		
		double w = 0;
		w = weightValue*value;
		
		if (w != 0){
		
			if ("+".equals(operation)){
				LOG.info("Increasing criticality of agent ["+ agentId+ "] by "+ w);
			}else if ("-".equals(operation)){
				LOG.info("Decreasing criticality of agent ["+ agentId+ "] by "+ w);
			}else{
				LOG.info("It is not possible to update the criticality of agent ["+ agentId+ "] without specifying the operation.");
			}
			
			LOG.info("Firing event [UPDATE_CRITICALITY] to be sensed by DimaX...");
			Event event =  new Event(Masks.UPDATE_CRITICALITY,getId());
			event.addEventContent("agentId",agentId);
			event.addEventContent("value",w);
			event.addEventContent("operation",operation);
			event.addEventContent("infoEvento",infoEvento);
			getContext().fire(event);
		}else{
			LOG.info("Criticality value = 0: does nothing.");
		}
		return true;
	}
	
	public boolean checkWeightByRef(String ref){
		for (Weight weight : weightList) {
			if (weight.getRef().equals(ref)){
				return true;
			}
		}
		return false;
	}
	
	public void updateAgentCriticalityThroughMessageArrival(Event event) throws LawException{
		String infoEvento = "eventId[" + event.getEventId().toString() + "],evenType["+Masks.getName(event.getType())+"]";
		
		double weightValue = getWeightByRef("message");
		
		Message msg = (Message) event.getContent().get(Event.MESSAGE);
		
		if (msg == null){
			throw new LawException("É necessário passar a mensagem no conteúdo do evento " 
					+ event.getEventId() + " para que o módulo de criticalidade funcione corretamente.",LawException.MISSING_MESSAGE_IN_EVENT_CONTENT);
		}
		
		AgentIdentification receiverId = msg.getAllReceivers().keySet().iterator().next();
		AgentIdentification senderId = msg.getSender();
		/**
		 *	From DimaX:
		 *  Low: [0,0.35] – classes 4,6
		 *	Medium: ]0.30,0.65] – classes 2,3,5
		 *	High: ]0.60,1] – class 1
		 *	– class 1 =request, request-whenever, query-if, query-ref, subscribe
		 *	– class 2 = inform, inform-done, inform-ref
		 *	– class 3 = cfp, propose
		 *	– class 4 = reject-proposal, refuse, cancel
		 *	– class 5 = accept-proposal, agree
		 *	– class 6 = not-understood, failure.
		 */
		if (msg.getPerformative().equals(Message.ACCEPT_PROPOSAL)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(receiverId,weightValue,0.5,"+",infoEvento);
			logCriticalityUpdating(senderId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(senderId,weightValue,0.5,"+",infoEvento);
		}else if (msg.getPerformative().equals(Message.CALL_FOR_PROPOSAL)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(receiverId,weightValue,0.5,"+",infoEvento);
		}else if (msg.getPerformative().equals(Message.CANCEL)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(receiverId,weightValue,0.5,"-",infoEvento);
			logCriticalityUpdating(senderId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(senderId,weightValue,0.5,"-",infoEvento);
		}else if (msg.getPerformative().equals(Message.CONFIRM)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.3);
			updateAgentCriticality(receiverId,weightValue,0.3,"+",infoEvento);
		}else if (msg.getPerformative().equals(Message.FAILURE)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.3);
			updateAgentCriticality(receiverId,weightValue,0.3,"-",infoEvento);
			logCriticalityUpdating(senderId,msg.getPerformative(),weightValue,0.3);
			updateAgentCriticality(senderId,weightValue,0.3,"-",infoEvento);
		}else if (msg.getPerformative().equals(Message.INFORM)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.3);
			updateAgentCriticality(receiverId,weightValue,0.3,"+",infoEvento);
			logCriticalityUpdating(senderId,msg.getPerformative(),weightValue,0.3);
			updateAgentCriticality(senderId,weightValue,0.3,"+",infoEvento);
		}else if (msg.getPerformative().equals(Message.NOT_UNDERSTOOD)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.1);
			updateAgentCriticality(receiverId,weightValue,0.1,"+",infoEvento);
			logCriticalityUpdating(senderId,msg.getPerformative(),weightValue,0.1);
			updateAgentCriticality(senderId,weightValue,0.1,"+",infoEvento);
		}else if (msg.getPerformative().equals(Message.PROPOSE)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(receiverId,weightValue,0.5,"+",infoEvento);
			logCriticalityUpdating(senderId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(senderId,weightValue,0.5,"+",infoEvento);
		}else if (msg.getPerformative().equals(Message.REFUSE)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(receiverId,weightValue,0.5,"-",infoEvento);
			logCriticalityUpdating(senderId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(senderId,weightValue,0.5,"-",infoEvento);
		}else if (msg.getPerformative().equals(Message.REJECT_PROPOSAL)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(receiverId,weightValue,0.5,"-",infoEvento);
			logCriticalityUpdating(senderId,msg.getPerformative(),weightValue,0.5);
			updateAgentCriticality(senderId,weightValue,0.5,"-",infoEvento);
		}else if (msg.getPerformative().equals(Message.REQUEST)){
			logCriticalityUpdating(receiverId,msg.getPerformative(),weightValue,0.8);
			updateAgentCriticality(receiverId,weightValue,0.8,"+",infoEvento);
			logCriticalityUpdating(senderId,msg.getPerformative(),weightValue,0.8);
			updateAgentCriticality(senderId,weightValue,0.8,"+",infoEvento);
		}
	}

	public void setExecutionState(ExecutionState state) {
		executionState = state;
		
	}

	public ExecutionState getExecutionState() {
		if (this.nbActiveRoles>0){
			return ExecutionState.RUNNING;
		}else{
			return ExecutionState.FINISHED;
		}
	}

	public SceneExecution getSceneExecution() {
		return sceneExecution;
	}

	public void setSceneExecution(SceneExecution sceneExecution) {
		this.sceneExecution = sceneExecution;
	}
	
	private void logCriticalityUpdating(AgentIdentification agentId, 
										String performative, 
										double weightValue, 
										double valueAssoc){
		LOG.info("Updating the criticality of agent ["+ agentId+ "] through the event type message_arrival " 
				+ " which performative is " + performative + " and "
				+ " the weight is " + weightValue 
				+ "and the value associated is " + valueAssoc);
	}

	public void notifySubjectDeath(Subject subject) {
		// TODO Auto-generated method stub
		
	}
	
}

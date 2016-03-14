package br.pucrio.inf.les.law;

import java.util.HashMap;
import java.util.Map;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.organization.OrganizationExecution;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;

public abstract class XMLawScenario extends XMLawParserScenario 
{
	protected ExecutionManager manager;
	
	protected OrganizationExecution orgExecution;
	protected SceneExecution sceneExecution;
	
	protected Message requestMessage;
	protected AgentIdentification senderAgent;
	
	protected Map<MessageValidator.Component,Object> components;
	
	public void setUp() throws Exception 
	{	
		super.setUp();
		
		manager = new ExecutionManager(table);
		orgExecution = (OrganizationExecution)manager.newInstance(null,new Id("auction"),null);
		sceneExecution = (SceneExecution)manager.newInstance(orgExecution.getId(),new Id("game"),null);
		
		senderAgent = new AgentIdentification("sender");
		requestMessage = new Message(Message.REQUEST);
		requestMessage.setSender(senderAgent);
		
		components = new HashMap<MessageValidator.Component,Object>();
		components.put(MessageValidator.Component.ORGANIZATION_EXECUTION,orgExecution);
		components.put(MessageValidator.Component.ORGANIZATION_DESCRIPTOR,manager.getDescriptorByDescriptorId(new Id("auction")));
		components.put(MessageValidator.Component.SCENE_EXECUTION,sceneExecution);
		components.put(MessageValidator.Component.SCENE_DESCRIPTOR,manager.getDescriptorByDescriptorId(new Id("game")));
	}
}
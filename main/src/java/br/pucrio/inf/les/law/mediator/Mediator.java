package br.pucrio.inf.les.law.mediator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.CommunicationProvider;
import br.pucrio.inf.les.law.communication.ICommunication;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.communication.jade.AgentJadeCommunication;
import br.pucrio.inf.les.law.communication.jade.JadeCommunication;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.mediator.command.AddLawCommand;
import br.pucrio.inf.les.law.mediator.command.Command;
import br.pucrio.inf.les.law.mediator.command.EnterInOrganizationCommand;
import br.pucrio.inf.les.law.mediator.command.EnterInSceneCommand;
import br.pucrio.inf.les.law.mediator.command.ListAvailableRolesCommand;
import br.pucrio.inf.les.law.mediator.command.ListRunningScenesCommand;
import br.pucrio.inf.les.law.mediator.command.PerformRoleCommand;
import br.pucrio.inf.les.law.mediator.command.QuitOrganizationCommand;
import br.pucrio.inf.les.law.mediator.command.StartExternalObserverCommand;
import br.pucrio.inf.les.law.mediator.command.StartSceneCommand;
import br.pucrio.inf.les.law.mediator.command.validation.EnteredOrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.EnteredSceneValidator;
import br.pucrio.inf.les.law.mediator.command.validation.ExecutionManagerValidator;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.mediator.command.validation.OrganizationValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SceneExecutionValidator;
import br.pucrio.inf.les.law.mediator.command.validation.SenderValidator;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.util.Constants;

public class Mediator extends Thread implements IObserver {
	/**
	 * Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(Mediator.class);

	private ICommunication communication;

	protected AgentIdentification myAid;

	private boolean isRunning = false;

	private Map<String, Command> commands;

	private Map<String, ExecutionManager> executionManagers;

	private MessageValidator mediatorValidatorChain;

	private Context rootContext;

	/**
	 * Creates a mediator agent.
	 * 
	 * @throws LawException
	 */
	public Mediator() throws LawException {
		commands = new HashMap<String, Command>();

		this.myAid = new AgentIdentification(Constants.MEDIATOR_ID.getValue());
		executionManagers = new HashMap<String, ExecutionManager>();

		rootContext = new Context(null, null);
		rootContext.attachObserver(this, Masks.COMPLIANT_MESSAGE);

		communication = CommunicationProvider.mediatorCommunication();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating commands for mediator-protocol");
		}

		addCommand(new AddLawCommand(this));
		addCommand(new EnterInOrganizationCommand());
		addCommand(new EnterInSceneCommand());
		addCommand(new ListAvailableRolesCommand());
		addCommand(new ListRunningScenesCommand());
		addCommand(new PerformRoleCommand());
		addCommand(new QuitOrganizationCommand());
		addCommand(new StartSceneCommand());
		addCommand(new StartExternalObserverCommand(this));

		if (LOG.isDebugEnabled()) {
			LOG.debug("Commands for mediator-protocol created");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating message validator for enforcement");
		}

		MessageValidator execMan = new ExecutionManagerValidator();
		MessageValidator sender = new SenderValidator();
		MessageValidator org = new OrganizationValidator();
		MessageValidator enterOrg = new EnteredOrganizationValidator();
		MessageValidator sceneExec = new SceneExecutionValidator();
		MessageValidator enteredScene = new EnteredSceneValidator();

		execMan.setSuccesor(sender);
		sender.setSuccesor(org);
		org.setSuccesor(enterOrg);
		enterOrg.setSuccesor(sceneExec);
		sceneExec.setSuccesor(enteredScene);

		mediatorValidatorChain = execMan;

		if (LOG.isDebugEnabled()) {
			LOG.debug("Validator message validators created");
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Mediator is alive.");
		}
	}

	public void addCommand(Command command) {
		commands.put(command.name(), command);
	}
	
	public AgentIdentification getMyAid()
	{
		return myAid;
	}

	public ExecutionManager getExecutionManager(String organization) {
		return executionManagers.get(organization);
	}

	public void addExecutionManager(String organization,
			ExecutionManager executionManager) {
		executionManagers.put(organization, executionManager);
	}

	public void startMediator() {
		isRunning = true;
		start();
	}

	public void stopMediator() {
		isRunning = false;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void run() {
		while (isRunning) {
			try {
				// wait for income messages
				Message message = communication.waitForMessage();
				//TODO implementar troca de receptor na camada de comunicacao do mediador (waitForMessage e cia)
				
				if (LOG.isInfoEnabled()) {
					//LOG.info("Message received: " + message);
				}

				String organizationExec = message
						.getContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID);
				ExecutionManager executionManager = executionManagers
						.get(organizationExec);

				if (!MessageContentConstants.MEDIATOR_PROTOCOL.equals(message
						.getProtocol())) {
					// enforces non meditor-protocol messages
					LOG.info("Antes do enforcement");
					doEnforcement(message, executionManager);
					LOG.info("Depois do enforcement");
				} else {
					// process mediator-protocol messages					
					processMediatorProtocol(message, executionManager);
				}
				LOG.info("mediator running.");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}		
	}

	private void doEnforcement(Message message,
			ExecutionManager executionManager) {
		// check if it is a valid message

		Map<MessageValidator.Component, Object> components = new HashMap<MessageValidator.Component, Object>();
		Message failureReply = mediatorValidatorChain.handleMessage(message,
				executionManager, components);
		// something wrong with the message
		if (failureReply != null) {
			LOG.info("something wrong with the message: "+failureReply);
			communication.send(failureReply);
			return;
		}

		// message ok
		LOG.info("Message " + message + " is ok for enforcement");

		SceneExecution sceneExecution = (SceneExecution) components
				.get(MessageValidator.Component.SCENE_EXECUTION);

		// enforcing message
		Event messageArrivalEvent = new Event(Masks.MESSAGE_ARRIVAL, new Id());
		messageArrivalEvent.addEventContent(Event.MESSAGE, message);
		sceneExecution.getContext().fire(messageArrivalEvent);
	}

	private void processMediatorProtocol(Message message,
			ExecutionManager executionManager) {
		// TODO: verificar quando nao existir comando mapeado para evitar
		// nullPointer
		Command command = commands.get(message
				.getContentValue(MessageContentConstants.KEY_COMMAND));
		
		LOG.info("Executando comando: "+command.name());
		Message reply = command.execute(message, executionManager);
		LOG.info("Depois do comando: "+command.name());
		if (reply != null) {
			communication.send(reply);
		}
	}

	public void update(Event event) throws LawException {
		Message message = (Message) event.getEventContent(Event.MESSAGE);
		LOG.info("Redirecting message " + message);
		communication.send(message);
	}

	public Context getRootContext() {
		return rootContext;
	}

	public Map<String, ExecutionManager> getExecutionManagers() {
		return executionManagers;
	}
	
	public ICommunication getCommunication()
	{
		return communication;
	}

	public void notifySubjectDeath(Subject subject) {
		// TODO Auto-generated method stub
		
	}

}

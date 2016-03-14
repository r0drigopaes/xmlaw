package br.pucrio.inf.les.law.execution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.execution.IExecution.ExecutionState;
import br.pucrio.inf.les.law.model.LawException;

public class TriggerObserver implements IObserver {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(TriggerObserver.class);

    private Trigger trigger;

    private IExecution execution;

    private IDescriptor descriptor;

    // TODO Remover TriggerManager
    private TriggerManager triggerManager;

    private Context context;

    private boolean activateType = false;

    /**
     * Use this contructor when this observer is used to deactivate an
     * execution.
     * 
     * @param trigger
     * @param execution
     * @param manager
     */
    public TriggerObserver(Trigger trigger, IExecution execution,
            TriggerManager manager) {
        this.trigger = trigger;
        // TODO Rever bacalhau maira guga
        this.descriptor = execution.getDescriptor();
        this.execution = execution;
        this.triggerManager = manager;
        this.context = execution.getContext();

        activateType = false;

        // Attaches itself as an observer
        context.attachObserver(this, trigger.getDeactivationMask());
    }

    /**
     * Use this constructor when this observer is used to activate a descriptor
     * 
     * @param trigger
     * @param descriptor
     * @param manager
     */
    public TriggerObserver(Trigger trigger, IDescriptor descriptor,
            TriggerManager manager, Context context) {
        this.trigger = trigger;
        this.descriptor = descriptor;
        this.triggerManager = manager;
        this.context = context;

        activateType = true;
        
        // Attaches itself as an observer        
        context.attachObserver(this, trigger.getActivationMask());
    }

    public void update(Event event) throws LawException {
        if (activateType) {
            if (LOG.isDebugEnabled()){
                LOG.debug("Trigger " + descriptor.getId() + " (ACTIVATE) observer receiving event: "+event+" contexto: "+context);
            }
            if (trigger.activates(event)) {
            	//TODO: Adicionei este if porque durante a propagação dos eventos
            	//no caso da norma, quando chegava na raiz da árvore
            	//se o executionmanager estiver vazio, dá erro.
            	if (context.getExecutionManager()!=null){
            		LOG.debug("Trigger " + descriptor.getId() + " activates the execution");
            		context.newInstance(descriptor.getId(), event.getContent());
            	}
            }
        } else {
            if (LOG.isDebugEnabled()){
                LOG.debug("Trigger " + descriptor.getId() + " (DEACTIVATE) observer receiving event: "+event);
            }
            // Verifies if the trigger deactivation conditions are satisfied
            if (trigger.deactivates(event)) {
                if (LOG.isDebugEnabled()){
                    LOG.debug("Trigger " + descriptor.getId() + " deactivates the execution, asking the execution ");
                }
                // If it is, then ask the execution if it should stop. This is
                // because the
                // execution may require some execution sensitive information to
                // stop.
                // such example of this use are the norms.
                if (execution.shouldStop(event)) {
                    if (LOG.isDebugEnabled()){
                        LOG.debug("Execution said yes, stopping the execution");
                    }
                    execution.setExecutionState(ExecutionState.FINISHED);
                    // Really stops the execution
                   execution.stopExecution();
                    // kills itself
                    triggerManager.removeTrigger(this);
                }
            }
        }
    }

	public void notifySubjectDeath(Subject subject) {
		// TODO Auto-generated method stub
		
	}

}

package br.pucrio.inf.les.law.execution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.util.IdGenerator;

/**
 * Manages the execution of instances through descritors.
 * 
 * @author Rodrigo
 */
public class ExecutionManager extends Thread {

    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ExecutionManager.class);

    /**
     * Execution table that keeps all the execution (IExecution) instances
     * running.
     */
    private ExecutionTable executionTable = new ExecutionTable();

    /**
     * Keeps all the descriptors
     */
    private DescriptorTable descriptorTable = null;

    /**
     * Manages the deactivation of IExecution through the triggers
     */
    private TriggerManager triggerManager = new TriggerManager();

    public ExecutionManager(DescriptorTable table) {
        this.descriptorTable = table;
        // starts the clean up behavior
        start();
    }

    /**
     * Creates a new instance of an object given a descriptor id.
     * 
     * @param parentExecutionId -
     *            each execution (IExecution) has an Id. If the new instance
     *            should be created in the context of a certain execution, then
     *            the id of this execution should be used in this parameter. For
     *            example, if a scene has to be created in the context of an
     *            organization, then the organization id should be passed here.<br>
     *            If no parent is required use null.
     * @param descriptorId -
     *            the id of the element that will be instantiated.
     * @param parameters -
     *            some parameters need to the creation of this specific
     *            execution.
     * @return the execution just created
     * @throws LawException -
     *             Case the parentExecutionId specified does not have any
     *             execution running.
     */
    public IExecution newInstance(Id parentExecutionId, Id descriptorId,
            Map<String, Object> parameters) throws LawException {
        assert (descriptorId != null);

        IExecution parentExecution = null;
        Context parentContext = null;

        if (parentExecutionId != null) {
            parentExecution = this.executionTable
                    .getByExecutionId(parentExecutionId);
            if (parentExecution == null) {
                throw new LawException("There is no instance running with id: "
                        + parentExecutionId,
                        LawException.PARENT_EXECUTION_NOT_FOUND);
            }
            parentContext = parentExecution.getContext();
            assert (parentContext != null); // At this point parentContext
            // cannot be null
        }

        IDescriptor descriptor = this.descriptorTable.get(descriptorId);

        // BEGIN Context creation decision
        Context theContext = null;
        if (descriptor.needContext()) {
            // At this point parentContext can be null. It happens when
            // parentExecutionId == null
            theContext = new Context(this, parentContext);
        } else {
            if (parentExecution == null) {
                throw new LawException(
                        "If the descriptor does not need a context, then its parent cannot be null",
                        LawException.PARENT_EXECUTION_NOT_FOUND);
            }
            // Creates the execution in the parent context.
            theContext = parentExecution.getContext();
        }
        // END context creation decision
        return this.newInstanceInContext(theContext, descriptorId, parameters);

    }

    public IExecution newInstanceInContext(Context theContext, Id descriptorId,
            Map<String, Object> parameters) throws LawException {

        if (theContext==null){
            throw new LawException("Attempt to create an execution in a null context. Context cannot be null",LawException.MISSING_EXECUTION_CONTEXT);
        }
        
        IDescriptor descriptor = this.descriptorTable.get(descriptorId);
        if (descriptor==null){
            throw new LawException("There is no descriptor with id=["+descriptorId+"] in the descriptor table",LawException.MISSING_DESCRIPTOR);
        }

        if ( ! descriptor.shouldCreate(theContext,parameters)){
            LOG.info("The descriptor ["+descriptor+"] will not create an execution for parameters ["+parameters+"]");
            return null;
        }
        
        IExecution execution = descriptor.createExecution(theContext,
                parameters);
        // Verify if the execution has kept its context
        if (execution.getContext()==null){
            throw new LawException("Execution ["+execution+"] has to keep its context. Context cannot be null",LawException.MISSING_EXECUTION_CONTEXT);
        }
        
        Id newId = new Id(IdGenerator.getInstance().getNewId());
        execution.setId(newId);

        this.executionTable.addEntry(newId, descriptor.getId(), execution,
                theContext);
        
        // Verifies if this execution has a trigger that may be used to
        // deactivate this execution.
        // Examples of such elements that have trigger are norms
        // and clocks.
        Trigger trigger = descriptor.getTrigger();
        if (trigger != null) {
            if (LOG.isDebugEnabled()) {
                LOG
                        .debug("Adding trigger deactivation conditions for execution: "
                                + execution);
            }
            triggerManager.addExecution(execution, trigger);
        }
       

        // Finnaly, return the execution just created
        return execution;
    }

    /**
     * One descriptor may have many execution instances. For example, we can
     * have three instances of the same norm.<br>
     * This method returns a set of instances for a given descriptor Id in a
     * given context.
     * 
     * @param id
     * @return
     */
    public List<IExecution> getExecutionsByDescriptorIdAndContext(Id descriptorId,
            Context context) {
        return executionTable.getByDefinitionIdAndContext(descriptorId, context);
    }
    
    public List<IExecution> getExecutionsByDescriptorId(Id descriptorId)
    {
    	return executionTable.getByDefinitionId(descriptorId);
    }
    
    public List<IDescriptor> getDescriptorsByDescriptorClass(Class descriptorClass)
    {
    	return getComponentsByClass(descriptorClass,descriptorTable.getAllDescriptors());
    }
    
    public IDescriptor getDescriptorByDescriptorId(Id descriptorId){
    	return descriptorTable.get(descriptorId);
    }
    
    public List<IExecution> getExecutionsByExecutionClass(Class executionClass)
    {
    	return getComponentsByClass(executionClass,executionTable.getAllExecutions());
    }
    
    private <T> List<T> getComponentsByClass(Class clazz, List<T> allComponents)
    {
    	List<T> components = new LinkedList<T>();
    	for(T obj : allComponents)
    	{
    		if(clazz.isInstance(obj))
    		{
    			components.add(obj);    			
    		}
    	}
    	return components;
    }

    public IExecution getExecutionById(Id executionId) {
        return executionTable.getByExecutionId(executionId);
    }

    public void enableTrigger(IDescriptor descriptor, Context context) {
        // Enable the activation conditions
        triggerManager.addDescriptor(descriptor, context);
    }

    // Clean up behavior
    public void run() {
        while (true) {
            try {
                // Each 0,05 seconds
                Thread.sleep(50);
               
                
	                List<IExecution> running = executionTable.getAllExecutions();
	                List<IExecution> toRemove = new ArrayList<IExecution>();
	                
	                // Esse loop nao pode acontecer atraves de iterators 
	                // pq a lista de IExecution pode ser modificada fora
	                // desta thread, e um iterator nao permite modificações
	                // enquanto se itera.	                
	                // for (IExecution execution : running) { <-- ISSO tbm não pode
	                int size = running.size();
	                for (int i = 0; i< size; i++){
	                	IExecution execution = running.get(i);
	                    // removes every one that has already finished
	                    if (execution.getExecutionState().equals(
	                            IExecution.ExecutionState.FINISHED)) {

	                    	if (LOG.isDebugEnabled()){
	                    		LOG.debug("Stopping: "+execution);
	                    	}
	                    	execution.stopExecution();
	                    	toRemove.add(execution);
//	                    	killExecution(execution, toRemove);
//	                    	break; //TODO
	                    }
	                }
	                for (IExecution execution : toRemove) {
	                	executionTable.removeExecution(execution);
					}
            } catch (Exception e) {
                LOG.error(e, e);
            }
        } //end while
    }
    
    public void stopExecution(IExecution execution){
    	List<IExecution> toRemove = new ArrayList<IExecution>();
    	killExecution(execution,toRemove);
    	for (IExecution aExecution : toRemove) {
        	executionTable.removeExecution(aExecution );
		}
    }
    
    private void killContext(Context contextToBeKilled, IExecution parent, List<IExecution> toRemove){
    	List<Context> children = contextToBeKilled.getChildren();
		for (Context context : children) {
			killContext(context, parent, toRemove);
		}
		List<IObserver> observers = contextToBeKilled.getSubject().getObservers();
		for (IObserver observer : observers) {
			// Como todo execution se cadastra no seu prõprio contexto, 
			// para evitar um loop, Evita que repita o processo (kill execution) no execution do pai.
			if (!observer.equals(parent)){
				observer.notifySubjectDeath(contextToBeKilled.getSubject());
				if (observer instanceof IExecution){				
					killExecution((IExecution)observer, toRemove);
				}
			}
		}
		contextToBeKilled.stop();
    }
    
    
    private void killExecution(IExecution execution,List<IExecution> toRemove){
    	// Para a execução do IExecution
    	LOG.info("Parando: "+execution);
    	execution.stopExecution();    	
    	Context contextToBeKilled = execution.getContext();
//    	contextToBeKilled.detachObserver()
    	
    	// Verifica se o execution foi quem criou o contexto. Se foi ele, então
    	// o ciclo de vida do contexto também deve ser interrompido.
    	// Se não foi ele, entao significa que o execution está usando o contexto
    	// criado por outro objeto e, portanto, o contexto não deve ser destruído.
    	if (execution.getDescriptor().needContext()){
    		
    		killContext(contextToBeKilled,execution, toRemove);
    	}
    	// Adiciona o IExecution para que ele possa ser removido da tabela
    	// de elementos em execução.
    	toRemove.add(execution);
    }

}
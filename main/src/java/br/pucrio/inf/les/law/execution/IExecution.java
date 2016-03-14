package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public interface IExecution {
    
    public enum ExecutionState{RUNNING,FINISHED};

    public Context getContext();

    public void setId(Id newId);

    public Id getId();

    public void stopExecution();
    
    public IDescriptor getDescriptor();
    
    public void setDescriptor(IDescriptor descriptor);
    

    /**
     * If this element has a trigger assotiated with it, then this method is
     * only called after the trigger has indicated that this execution should
     * stop due to its deactivation conditions. However in some cases, the
     * execution needs context-sensitive information in order to stop. For
     * example, a norm has to verify if the owner of the norm (execution) is the
     * one who is advocating the deactivation.
     * 
     * @param event
     * @return true either if the execution has no trigger associated or the
     *         execution does not need any context-sentitive information.
     */
    public boolean shouldStop(Event event) throws LawException;
    
    /**
     * This method allows the execution manager ask for the 
     * @return
     */
    public ExecutionState getExecutionState();

	public void setExecutionState(ExecutionState state);

}

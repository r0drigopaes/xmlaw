package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public abstract class AbstractExecution implements IExecution{

    private Id id;
    protected Context context;
    protected IDescriptor descriptor;
    
    protected AbstractExecution(Context context, IDescriptor descriptor){
        this.context = context;
        this.descriptor = descriptor;
    }
    
    public Context getContext() {
        return this.context;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Id getId() {
        // TODO Auto-generated method stub
        return this.id;
    }

    public void stopExecution() {
        // TODO Auto-generated method stub
        
    }

    public boolean shouldStop(Event event) throws LawException{
        // TODO Auto-generated method stub
        return false;
    }

    public ExecutionState getExecutionState() {
    	return ExecutionState.RUNNING;
    }

	public IDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(IDescriptor descriptor) {
		this.descriptor = descriptor;
	}
    
    public String toString(){
        return "ExecutionId ["+id+"] , Descriptor ["+descriptor+"]";
    }
}

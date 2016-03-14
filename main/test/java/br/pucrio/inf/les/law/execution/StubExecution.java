package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.model.Id;

public class StubExecution implements IExecution {

    private Context myContext = null;

    private Id id = null;
    
    private ExecutionState executionState  = ExecutionState.RUNNING;
    
    private IDescriptor descriptor;

    public StubExecution(Context myContext, IDescriptor descriptor) {
        this.myContext = myContext;
        setDescriptor(descriptor);
    }

    public Context getContext() {
        return myContext;
    }

    public void setId(Id newId) {
        this.id = newId;
    }

    /**
     * @return Returns the id.
     */
    public Id getId() {
        return id;
    }

    public void stopExecution() {
    	myContext.fire(new Event(Masks.STUB_DEACTIVATION, id));
        executionState = ExecutionState.FINISHED;
    }

    public boolean shouldStop(Event event) {
        return true;
    }

    public ExecutionState getExecutionState() {
        return executionState;
    }

	public IDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(IDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public void setExecutionState(ExecutionState state) {
		executionState = ExecutionState.FINISHED;
		
	}

}

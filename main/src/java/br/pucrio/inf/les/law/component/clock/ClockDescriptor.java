package br.pucrio.inf.les.law.component.clock;

import br.pucrio.inf.les.law.component.ElementTriggerDescriptor;
import br.pucrio.inf.les.law.component.clock.ClockExecution.Type;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.util.Map;

public class ClockDescriptor extends ElementTriggerDescriptor {

    private long timeout;

    private ClockExecution.Type type;

    public ClockDescriptor(Id id, long timeout, Type type) {
    	super(id);
        this.timeout = timeout;
        this.type = type;
    }

    public IExecution createExecution(Context context,
            Map<String, Object> parameters) throws LawException {
        
        // first fire the event
    	Event event = new Event(getId(), Masks.CLOCK_ACTIVATION, getId());
    	event.setContent(parameters);
        context.fire(event);
        
        // return the ClockExecution
        return new ClockExecution(context, timeout, type,this,parameters);
    }


    public long getTimeout() {
        return timeout;
    }

    public ClockExecution.Type getType() {
        return type;
    }

	public boolean needContext() {
		// TODO Auto-generated method stub
		return false;
	}

    public boolean shouldCreate(Context context, Map<String, Object> parameters) throws LawException {
        return true;
    }
}

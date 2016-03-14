package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.util.Map;

public class StubDescriptor implements IDescriptor {

    private Id id = new Id();

    private Trigger defaultTrigger = new Trigger();

    public IExecution createExecution(Context context,
            Map<String, Object> parameters) throws LawException {
        context.fire(new Event(Masks.STUB_ACTIVATION, id));
        return new StubExecution(context,this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.pucrio.inf.les.law.execution.Trigger#addActivation(br.pucrio.inf.les.law.model.Id,
     *      int)
     */
    public void addActivation(Id elementId, int eventType) {
        defaultTrigger.addActivation(elementId, eventType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.pucrio.inf.les.law.execution.Trigger#addDeactivation(br.pucrio.inf.les.law.model.Id,
     *      int)
     */
    public void addDeactivation(Id elementId, int eventType) {
        defaultTrigger.addDeactivation(elementId, eventType);
    }

    public Id getId() {
        return id;
    }

    public Trigger getTrigger() {
        return defaultTrigger;
    }

    public boolean needContext() {
        return true;
    }

    public boolean shouldCreate(Context context, Map<String, Object> parameters) throws LawException {
        return true;
    }

}

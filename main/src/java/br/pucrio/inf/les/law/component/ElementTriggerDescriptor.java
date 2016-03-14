package br.pucrio.inf.les.law.component;

import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.execution.Trigger;
import br.pucrio.inf.les.law.model.Id;

public abstract class ElementTriggerDescriptor implements IDescriptor {

    protected Id id;

    /**
     * Id - Identifier of the element which activates the clock<br>
     * Integer - The type of event.
     */
    private Trigger trigger = new Trigger();

    public ElementTriggerDescriptor(Id id) {
        this.id = id;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.pucrio.inf.les.law.execution.Trigger#addActivation(br.pucrio.inf.les.law.model.Id,
     *      int)
     */
    public void addActivation(Id elementId, int eventType) {
        trigger.addActivation(elementId, eventType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.pucrio.inf.les.law.execution.Trigger#addDeactivation(br.pucrio.inf.les.law.model.Id,
     *      int)
     */
    public void addDeactivation(Id elementId, int eventType) {
        trigger.addDeactivation(elementId, eventType);
    }

    public int getActivationEventType(Id elementId) {
        return trigger.getActivationEventType(elementId);
    }

    public int getDeactivationEventType(Id elementId) {
        return trigger.getDeactivationEventType(elementId);
    }

    public Id getId() {
        return id;
    }
    
    public String toString(){
        return id.toString();
    }

}

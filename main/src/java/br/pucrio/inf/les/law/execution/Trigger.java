package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.model.Id;

import java.util.Collection;
import java.util.Hashtable;

import rodrigopaes.utils.binary.BinaryOperations;

public class Trigger {

	public Trigger(){
		
	}
    /**
     * Id - Identifier of the element which activates the clock<br>
     * Integer - The type of event.
     */
    private Hashtable<Id, Integer> activations = new Hashtable<Id, Integer>();

    private Hashtable<Id, Integer> deactivations = new Hashtable<Id, Integer>();

    public void addActivation(Id elementId, int eventType) {
        addCondition(elementId, eventType, activations);
    }

    public void addDeactivation(Id elementId, int eventType) {
        addCondition(elementId, eventType, deactivations);
    }
    
    public int getActivationEventType(Id elementId){
        return activations.get(elementId);
    }
    
    public int getDeactivationEventType(Id elementId){
        return deactivations.get(elementId);
    }

    public boolean activates(Event event) {
        return triggerIn(event, activations);
    }

    public boolean deactivates(Event event) {
        return triggerIn(event, deactivations);
    }

    public int getActivationMask() {
        return getMask(activations);
    }

    public int getDeactivationMask() {
        return getMask(deactivations);
    }

    private int getMask(Hashtable<Id, Integer> hash) {
        int mask = 0;
        Collection<Integer> values = hash.values();
        for (Integer value : values) {
            if (!BinaryOperations.contains(value, mask)) {
                mask = BinaryOperations.add(value, mask);
            }
        }
        return mask;
    }

    private void addCondition(Id elementId, int eventType,
            Hashtable<Id, Integer> destination) {
        Integer mask = destination.get(elementId);
        if (mask == null) {
            mask = new Integer(0);
        }
        // Verifies if the mask is not already in the table
        if (!BinaryOperations.contains(eventType, mask.intValue())) {
            // Add
            int newMask = BinaryOperations.add(mask.intValue(), eventType);
            destination.put(elementId, newMask);
        }
    }

    private boolean triggerIn(Event event, Hashtable<Id, Integer> destination) {
        int eventType = event.getType();
        Id generatorId = event.getEventGeneratorId();

        Integer type = destination.get(generatorId);

        if (type == null) {
            return false;
        }

        if (BinaryOperations.contains(eventType, type.intValue())) {
            return true;
        }
        return false;
    }

}

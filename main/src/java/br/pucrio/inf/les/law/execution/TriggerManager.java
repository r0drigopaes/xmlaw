package br.pucrio.inf.les.law.execution;


import java.util.ArrayList;
import java.util.List;

public class TriggerManager {

    private List<TriggerObserver> observers = new ArrayList<TriggerObserver>();

    public TriggerManager() {
    }

    public void addDescriptor(IDescriptor descriptor,  Context context) {
        observers.add(new TriggerObserver(descriptor.getTrigger(), descriptor, this,context));
    }
        
    public void addExecution(IExecution execution, Trigger trigger) {
        observers.add(new TriggerObserver(trigger, execution, this));
    }

    public void removeTrigger(TriggerObserver observer) {
        observers.remove(observer);
    }

}

package br.pucrio.inf.les.law.component.clock;

import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.StubEvent;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.StubDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class ClockScenario {

    private ExecutionManager executionManager;

    private ClockDescriptor periodicClockDescriptor;

    private ClockDescriptor onceClockDescriptor;

    private StubEvent activationEvent;

    private StubEvent deactivationEvent;
    
    private Context context;

    public void umountScenario() {
        executionManager = null;
        periodicClockDescriptor = null;
        onceClockDescriptor = null;
        activationEvent = null;
        deactivationEvent = null;
        context = null;
    }
    
    public void mountScenario() throws LawException {
        // Setting up a fictitious event generator
        Id generatorId = new Id("theGenerator");
        activationEvent = new StubEvent();
        activationEvent.setEventGeneratorId(generatorId);

        // Setting up another fictitious event generator
        Id generatorId2 = new Id("theGenerator2");
        deactivationEvent = new StubEvent();
        deactivationEvent.setEventGeneratorId(generatorId2);

        // Creates a descriptor table
        DescriptorTable descriptorTable = new DescriptorTable();
        
        // Clock descriptor that generates only one tick
        onceClockDescriptor = new ClockDescriptor(new Id("onceClockTest"),
                1000, ClockExecution.Type.ONCE);

        // Sets activation and deactivation conditions
        onceClockDescriptor.addActivation(generatorId, Masks.STUB_EVENT);
        onceClockDescriptor.addDeactivation(generatorId2, Masks.STUB_EVENT);

        // This one generates periodic ticks
        periodicClockDescriptor = new ClockDescriptor(new Id(
                "periodicClockTest"), 1000, ClockExecution.Type.PERIODIC);

        // Sets activation and deactivation conditions
        periodicClockDescriptor.addActivation(generatorId, Masks.STUB_EVENT);
        periodicClockDescriptor.addDeactivation(generatorId2, Masks.STUB_EVENT);

        // Add both of them to the descriptor table
        descriptorTable.add(onceClockDescriptor);
        descriptorTable.add(periodicClockDescriptor);

        // creates an execution manager passing the descriptor table
        executionManager = new ExecutionManager(descriptorTable);
        
        // adds stubs
        StubDescriptor descriptor = new StubDescriptor();
        descriptorTable.add(descriptor);
        context = executionManager.newInstance(null,descriptor.getId(),null).getContext();
    }

    /**
     * @return Returns the executionManager.
     */
    public ExecutionManager getExecutionManager() {
        return executionManager;
    }

    /**
     * @return Returns the onceClockDescriptor.
     */
    public ClockDescriptor getOnceClockDescriptor() {
        return onceClockDescriptor;
    }

    /**
     * @return Returns the periodicClockDescriptor.
     */
    public ClockDescriptor getPeriodicClockDescriptor() {
        return periodicClockDescriptor;
    }

    

    /**
     * @return Returns the activationEvent.
     */
    public StubEvent getActivationEvent() {
        return activationEvent;
    }

    /**
     * @return Returns the deactivationEvent.
     */
    public StubEvent getDeactivationEvent() {
        return deactivationEvent;
    }

    /**
     * @return Returns the context.
     */
    public Context getContext() {
        return context;
    }
}

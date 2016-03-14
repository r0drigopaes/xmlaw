package br.pucrio.inf.les.law.component.protocol;

import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.StubEvent;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.StubDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class ProtocolScenario {

    private StubEvent event;

    private State s0;

    private State s1;

    private State s2;

    private State s3;

    private State s4;

    private StubEvent event2;

    private ProtocolDescriptor protocolDescriptor;

    private ExecutionManager executionManager;

    private Context context;

    public void mountScenario() throws LawException {
        // Setting up a fictitious event generator
        Id generatorId = new Id("theGenerator");
        event = new StubEvent();
        event.setEventGeneratorId(generatorId);

        // Setting up another fictitious event generator
        Id generatorId2 = new Id("theGenerator2");
        event2 = new StubEvent();
        event2.setEventGeneratorId(generatorId2);

        // Creating the states
        s0 = new State(new Id("s0"), "Initial s0", State.Type.INITIAL);
        s1 = new State(new Id("s1"), "Execution s1", State.Type.EXECUTION);
        s2 = new State(new Id("s2"), "Execution s2", State.Type.EXECUTION);
        s3 = new State(new Id("s3"), "Failure s3", State.Type.FAILURE);
        s4 = new State(new Id("s4"), "Success s4", State.Type.SUCCESS);

        // Connecting the states
        Transition t1 = new Transition(new Id("t1"), s0, s1, protocolDescriptor);
        t1.addActivationCondition(generatorId, Masks.STUB_EVENT);

        Transition t2 = new Transition(new Id("t2"), s1, s1, protocolDescriptor);
        t2.addActivationCondition(generatorId, Masks.STUB_EVENT);

        Transition t3 = new Transition(new Id("t3"), s1, s2, protocolDescriptor);
        t3.addActivationCondition(generatorId, Masks.STUB_EVENT);

        Transition t4 = new Transition(new Id("t4"), s2, s3, protocolDescriptor);
        t4.addActivationCondition(generatorId, Masks.STUB_EVENT);

        Transition t5 = new Transition(new Id("t5"), s2, s4, protocolDescriptor);
        // this one is activated by the generator 2
        t5.addActivationCondition(generatorId2, Masks.STUB_EVENT);

        protocolDescriptor = new ProtocolDescriptor(new Id("Custom Protocol"),
                s0);

        DescriptorTable descriptorTable = new DescriptorTable();
        descriptorTable.add(protocolDescriptor);

        executionManager = new ExecutionManager(descriptorTable);

        // adds stubs
        StubDescriptor descriptor = new StubDescriptor();
        descriptorTable.add(descriptor);
        context = executionManager.newInstance(null, descriptor.getId(), null)
                .getContext();
    }

    /**
     * @return Returns the context.
     */
    public Context getContext() {
        return context;
    }

    /**
     * @return Returns the event.
     */
    public StubEvent getEvent() {
        return event;
    }

    /**
     * @return Returns the event2.
     */
    public StubEvent getEvent2() {
        return event2;
    }

    /**
     * @return Returns the s0.
     */
    public State getS0() {
        return s0;
    }

    /**
     * @return Returns the s1.
     */
    public State getS1() {
        return s1;
    }

    /**
     * @return Returns the s2.
     */
    public State getS2() {
        return s2;
    }

    /**
     * @return Returns the s3.
     */
    public State getS3() {
        return s3;
    }

    /**
     * @return Returns the s4.
     */
    public State getS4() {
        return s4;
    }

    public void umountScenario() {
        // TODO Auto-generated method stub

    }

    /**
     * @return Returns the protocolDescriptor.
     */
    public ProtocolDescriptor getProtocolDescriptor() {
        return protocolDescriptor;
    }

    /**
     * @return Returns the executionManager.
     */
    public ExecutionManager getExecutionManager() {
        return executionManager;
    }
}

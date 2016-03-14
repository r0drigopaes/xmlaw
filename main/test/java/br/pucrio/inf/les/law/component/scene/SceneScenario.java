package br.pucrio.inf.les.law.component.scene;

import br.pucrio.inf.les.law.component.clock.ClockDescriptor;
import br.pucrio.inf.les.law.component.clock.ClockExecution;
import br.pucrio.inf.les.law.component.protocol.ProtocolDescriptor;
import br.pucrio.inf.les.law.component.protocol.State;
import br.pucrio.inf.les.law.component.protocol.Transition;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.StubEvent;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

/**
 * This class creates: 1 scene, which contains 1 protocol and 2 clocks
 * 
 * @author Rodrigo
 */
public class SceneScenario {

    private DescriptorTable table;

    private SceneDescriptor sceneDescriptor;

    private StubEvent event1;

    private StubEvent event2;

    public void umountScenario() {
        table = null;
        sceneDescriptor = null;
        event1 = null;
        event2 = null;
    }

    public void mountScenario() throws LawException {
        // Events
        Id event1Generator = new Id("event1Generator");
        event1 = new StubEvent(event1Generator);

        Id event2Generator = new Id("event2Generator");
        event2 = new StubEvent(event2Generator);

        // Descriptor table
        table = new DescriptorTable();

        // Descriptors

        // Protocol
        State s0 = new State(new Id("s0"), "Waiting for start",
                State.Type.INITIAL);
        State s1 = new State(new Id("s1"), "Executing", State.Type.EXECUTION);
        State s2 = new State(new Id("s2"), "Finishing", State.Type.SUCCESS);
        ProtocolDescriptor protocolDescriptor = new ProtocolDescriptor(new Id(
                "Test protocol"), s0);
        Transition t1 = new Transition(new Id("t1"), s0, s1, protocolDescriptor);
        t1.addActivationCondition(event1Generator, Masks.STUB_EVENT);
        Transition t2 = new Transition(new Id("t2"), s1, s2, protocolDescriptor);
        t2.addActivationCondition(event2Generator, Masks.STUB_EVENT);
        
        table.add(protocolDescriptor);

        // Clocks
        ClockDescriptor clockDescriptor1 = new ClockDescriptor(new Id(
                "clock one"), 1000, ClockExecution.Type.PERIODIC);
        clockDescriptor1.addActivation(t1.getId(), Masks.TRANSITION_ACTIVATION);
        clockDescriptor1.addDeactivation(event2Generator, Masks.STUB_EVENT);
        ClockDescriptor clockDescriptor2 = new ClockDescriptor(new Id(
                "clock two"), 1000, ClockExecution.Type.PERIODIC);
        clockDescriptor2.addActivation(t2.getId(), Masks.TRANSITION_ACTIVATION);
        table.add(clockDescriptor1);
        table.add(clockDescriptor2);

        // Scene
        sceneDescriptor = new SceneDescriptor(new Id("The Scene"),-1);
        sceneDescriptor.setProtocolDescriptor(protocolDescriptor);
        sceneDescriptor.addClockDescriptor(clockDescriptor1);
        sceneDescriptor.addClockDescriptor(clockDescriptor2);
        table.add(sceneDescriptor);
    }

    /**
     * @return Returns the table.
     */
    public DescriptorTable getTable() {
        return table;
    }

    public Id getSceneDescriptorId() {
        return sceneDescriptor.getId();
    }

    /**
     * @return Returns the event1.
     */
    public StubEvent getEvent1() {
        return event1;
    }

    /**
     * @return Returns the event2.
     */
    public StubEvent getEvent2() {
        return event2;
    }
}

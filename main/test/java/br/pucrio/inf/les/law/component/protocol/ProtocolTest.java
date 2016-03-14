package br.pucrio.inf.les.law.component.protocol;

import br.pucrio.inf.les.law.model.LawException;

import java.util.List;

import junit.framework.TestCase;

public class ProtocolTest extends TestCase {

    private ProtocolScenario scenario;

    public void setUp() throws LawException{
        scenario = new ProtocolScenario();
        scenario.mountScenario();
    }
    
    public void tearDown(){
        scenario.umountScenario();
    }
    
    public void testProtocolExecution() throws LawException {
        
        ProtocolExecution protocolExecution = (ProtocolExecution) scenario
                .getExecutionManager().newInstanceInContext(scenario.getContext(),
                        scenario.getProtocolDescriptor().getId(), null);

        // Test state S0
        List<State> states = protocolExecution.getCurrentState();
        assertTrue(states.size() == 1);
        assertEquals(scenario.getS0(),states.get(0));

        // Fire an event and test for state s1
        protocolExecution.getContext().fireEventInContext(scenario.getEvent());
        states = protocolExecution.getCurrentState();
        assertTrue(states.size() == 1);
        assertEquals(scenario.getS1(), states.get(0));

        // Fire again and test for states s1 and s2
        protocolExecution.getContext().fireEventInContext(scenario.getEvent());
        states = protocolExecution.getCurrentState();
        assertTrue(states.size() == 2);
        assertEquals(scenario.getS1(), states.get(0));
        assertEquals(scenario.getS2(), states.get(1));

        // Fire and test for states s1, s2, s3
        protocolExecution.getContext().fireEventInContext(scenario.getEvent());
        states = protocolExecution.getCurrentState();
        assertTrue(states.size() == 3);

        assertEquals(scenario.getS1(), states.get(0));
        assertEquals(scenario.getS2(), states.get(1));
        assertEquals(scenario.getS3(), states.get(2));

        // Fire the event2 and test for state s4
        protocolExecution.getContext().fireEventInContext(scenario.getEvent2());
        states = protocolExecution.getCurrentState();
        assertTrue(states.size() == 1);
        assertEquals(scenario.getS4(), states.get(0));

    }

}

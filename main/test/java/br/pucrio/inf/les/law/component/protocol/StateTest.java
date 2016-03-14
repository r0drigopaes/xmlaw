package br.pucrio.inf.les.law.component.protocol;

import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.model.LawException;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StateTest extends TestCase {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(StateTest.class);

    private ProtocolScenario scenario;

    private Context executionContext;

    public void setUp() throws LawException {
        scenario = new ProtocolScenario();
        scenario.mountScenario();
        IExecution protocolExecution = scenario.getExecutionManager().newInstanceInContext(scenario.getContext(),scenario.getProtocolDescriptor().getId(),null);
        executionContext = protocolExecution.getContext();
    }

    public void tearDown() {
        scenario.umountScenario();
    }

    public void testSimpleStep() throws LawException {
        // Going from s0 to s1
        List<State> step = scenario.getS0().step(scenario.getEvent(),executionContext);
        assertTrue(step.size() == 1);
        assertEquals(step.get(0), scenario.getS1());
    }

    public void testSelfNonDeterministicStep() throws LawException {
        List<State> step = scenario.getS1().step(scenario.getEvent(),executionContext);
        assertTrue(step.size() == 2);

        boolean found1 = false;
        boolean found2 = false;
        for (State state : step) {
            if (state.equals(scenario.getS1())) {
                found1 = true;
            } else if (state.equals(scenario.getS2())) {
                found2 = true;
            }
        }
        assertTrue(found1 && found2);
    }

    public void testDifferentActivationConditions() throws LawException {
        List<State> step = scenario.getS2().step(scenario.getEvent(),executionContext);
        assertTrue(step.size() == 1);
        assertEquals(step.get(0), scenario.getS3());

        step = scenario.getS2().step(scenario.getEvent2(),executionContext);
        assertTrue(step.size() == 1);
        assertEquals(step.get(0), scenario.getS4());
    }
}

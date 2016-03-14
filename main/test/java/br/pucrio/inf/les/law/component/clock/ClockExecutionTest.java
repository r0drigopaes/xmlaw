package br.pucrio.inf.les.law.component.clock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.model.LawException;
import junit.framework.TestCase;

// TODO Renomear para tirar o OLD
public class ClockExecutionTest extends TestCase implements IObserver {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ClockExecutionTest.class);

    private ClockScenario scenario;

    private int counter;

    public void setUp() throws LawException {
        scenario = new ClockScenario();
        scenario.mountScenario();
        counter = 0;
    }

    public void tearDown() {
        scenario.umountScenario();
    }

    public void testOnceTickGeneration() throws LawException,
            InterruptedException {
        ExecutionManager manager = scenario.getExecutionManager();
        IExecution onceClock = manager.newInstanceInContext(scenario
                .getContext(), scenario.getOnceClockDescriptor().getId(), null);
        onceClock.getContext().attachObserver(this, Masks.CLOCKTICK);
        Thread.sleep(1500);
        assertTrue(counter == 1);
    }

    public void testPeriodicTickGeneration() throws LawException,
            InterruptedException {
        ExecutionManager manager = scenario.getExecutionManager();
        IExecution onceClock = manager.newInstanceInContext(scenario
                .getContext(), scenario.getPeriodicClockDescriptor().getId(),
                null);
        onceClock.getContext().attachObserver(this, Masks.CLOCKTICK);
        Thread.sleep(1500);
        assertTrue(counter == 1);
        Thread.sleep(1000);
        assertTrue(counter == 2);
        Thread.sleep(1000);
        assertTrue(counter == 3);
    }

    public void testActivationCondition() throws InterruptedException {
        ExecutionManager manager = scenario.getExecutionManager();
        Context context = scenario.getContext();

        // Enables for listen to activation conditions
        manager.enableTrigger(scenario.getOnceClockDescriptor(), context);

        context.attachObserver(this, Masks.CLOCK_ACTIVATION);

        // fire the activation condition
        context.fireEventInContext(scenario.getActivationEvent());

        // Verifies if a clock activation has occured
        assertTrue(counter == 1);
    }

    public void testDeactivationCondition() throws LawException, InterruptedException {
        
        ExecutionManager manager = scenario.getExecutionManager();
        Context context = scenario.getContext();
        IExecution clockExecution = context.newInstance(scenario
                .getPeriodicClockDescriptor().getId(), null);
        
        context.attachObserver(this,Masks.CLOCK_DEACTIVATION);
        context.fire(scenario.getDeactivationEvent());
        Thread.sleep(5000);
        assertTrue (counter == 1);
    }
    
    public void testMultipleClocks() throws LawException, InterruptedException{
        ExecutionManager manager = scenario.getExecutionManager();
        Context context = scenario.getContext();
        context.attachObserver(this,Masks.CLOCKTICK);
        
        IExecution clockExecution1 = context.newInstance(scenario
                .getOnceClockDescriptor().getId(), null);
        IExecution clockExecution2 = context.newInstance(scenario
                .getOnceClockDescriptor().getId(), null);
        IExecution clockExecution3 = context.newInstance(scenario
                .getOnceClockDescriptor().getId(), null);
        IExecution clockExecution4 = context.newInstance(scenario
                .getOnceClockDescriptor().getId(), null);
        
        Thread.sleep(2000);
        assertTrue( counter == 4 );
        
    }

    public void update(Event event) throws LawException {
        counter++;
    }

	public void notifySubjectDeath(Subject subject) {
		// TODO Auto-generated method stub
		
	}

}

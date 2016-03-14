package br.pucrio.inf.les.law.execution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.StubEvent;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.util.List;

import junit.framework.TestCase;

public class ExecutionManagerTest extends TestCase implements IObserver {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory
            .getLog(ExecutionManagerTest.class);

    private DescriptorTable descriptorTable;

    private StubDescriptor descriptor;

    private ExecutionManager manager;

    private int counter;

    private StubEvent activationEvent;

    public void setUp() {
        // Set Up
        descriptorTable = new DescriptorTable();

        Id generatorId = new Id("aGeneratorId");
        activationEvent = new StubEvent();
        activationEvent.setEventGeneratorId(generatorId);

        Id generatorId2 = new Id("anotherGeneratorId");
        StubEvent deactivationEvent = new StubEvent();
        deactivationEvent.setEventGeneratorId(generatorId2);

        descriptor = new StubDescriptor();
        descriptor.addActivation(generatorId, Masks.STUB_EVENT);
        descriptor.addDeactivation(generatorId2, Masks.STUB_EVENT);

        descriptorTable.add(descriptor);
        manager = new ExecutionManager(descriptorTable);

        // reset the counter
        counter = 0;
    }

    public void testNewInstance() throws LawException {

        // Creates an instance
        IExecution execution = manager.newInstance(null, descriptor.getId(),
                null);
        assertNotNull(execution);
        assertNotNull(execution.getContext());
        assertTrue(execution instanceof StubExecution);

        // Test if the instance just created is returned
        List<IExecution> executions = manager.getExecutionsByDescriptorIdAndContext(
                descriptor.getId(), execution.getContext());
        assertTrue(executions.size() == 1);

        // Creates another instance
        IExecution execution2 = manager.newInstance(null, descriptor.getId(),
                null);

        // Test if the new instance and the old instance are still there.
        // the old one (with its context)
        executions = manager.getExecutionsByDescriptorIdAndContext(descriptor.getId(),
                execution.getContext());
        assertTrue(executions.size() == 1);
        // the new one
        executions = manager.getExecutionsByDescriptorIdAndContext(descriptor.getId(),
                execution2.getContext());
        assertTrue(executions.size() == 1);

        // now creates a new one using a parent
        IExecution execution3 = manager.newInstance(execution.getId(),
                descriptor.getId(), null);

        executions = manager.getExecutionsByDescriptorIdAndContext(descriptor.getId(),
                execution3.getContext());

        assertTrue(executions.size() == 1);

    }

    public void testFire() throws LawException {
        // Creates in a root context
        IExecution execution = manager.newInstance(null, descriptor.getId(),
                null);
        // Attaches to the root context
        Context context = execution.getContext();
        context.attachObserver(this, Masks.STUB_EVENT);

        context.fireEventInContext(new StubEvent());
        assertTrue(counter == 1);

        // reset the counter
        counter = 0;

        // Creates an execution in a child context
        IExecution execution2 = manager.newInstance(execution.getId(),
                descriptor.getId(), null);
        Context context2 = execution2.getContext();
        // Generates the event in the child context and asserts if the parent
        // has received the notification
        context2.fireEventInContext(new StubEvent());
        assertTrue(counter == 1);
    }

    public void testEnableTrigger() throws LawException {
        // Creates in a root context
        IExecution execution = manager.newInstance(null, descriptor.getId(),
                null);
        // Enabling triggers
        Context context = execution.getContext();
        context.enableTrigger(descriptor);
        
        // Attaches to the root context
        context.attachObserver(this, Masks.STUB_ACTIVATION);

        // Fire an event that is a activation condition of a stubdescriptor. Then
        // it will create a StubExecution, generating a stub activation
        context.fireEventInContext(activationEvent);
        
        assertTrue (counter == 1);
        
        // fire again and test if it has received one more event notification
        context.fireEventInContext(activationEvent);
        assertTrue (counter == 2);
    }
    
    public void testDisableTrigger() throws LawException, InterruptedException{
        Id generatorId = new Id("generatorID");
        StubEvent event = new StubEvent(generatorId);
        
        
        DescriptorTable table = new DescriptorTable();        
        StubDescriptor descriptor = new StubDescriptor();
        
        // Add a deactivation condition for the sub descriptor
        descriptor.addDeactivation(generatorId,Masks.STUB_EVENT);        
        table.add(descriptor);
        
        ExecutionManager manager = new ExecutionManager(table);
        
        // creates an execution for the descriptor
        IExecution stubExecution  = manager.newInstance(null,descriptor.getId(),null);
        
        // attaches this test as an observer of stub deactivation
        stubExecution.getContext().attachObserver(this,Masks.STUB_DEACTIVATION);

        // fire the event that should deactivate the stub execution
        stubExecution.getContext().fire(event);
        
        Thread.sleep(1000);
        
        assertTrue(counter == 1);
        
    }

    public void update(Event event) throws LawException {
        counter++;
    }

	public void notifySubjectDeath(Subject subject) {
		// TODO Auto-generated method stub
		
	}

}

package br.pucrio.inf.les.law.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.model.LawException;
import junit.framework.TestCase;

public class SubjectTest extends TestCase implements IObserver {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(SubjectTest.class);

    private int counter = 0;

    private Subject subject = null;
    
    private boolean orderTest = false;
	private StubEvent e2;
	private List<Event> events = null;

    protected void setUp() throws Exception {
        counter = 0;
        subject  = new Subject();
        orderTest = false;
        events = new ArrayList<Event>();
    }

    public void testAttachObserver() {
        
        subject.attachObserver(this, Masks.STUB_EVENT);
        subject.fireEvent(new StubEvent());
        // Tests if it has received only one event notification
        assertTrue(counter == 1);
        counter = 0;
        // Try to attach again, the subject should notice that and avoid to send
        // duplicate notifications
        subject.attachObserver(this, Masks.STUB_EVENT);
        subject.fireEvent(new StubEvent());
        assertTrue(counter == 1);
    }
    
    public void testDettachObserver(){
        subject.attachObserver( this, Masks.STUB_EVENT);
        subject.fireEvent(new StubEvent());
        // Tests if it has received only one event notification
        assertTrue(counter == 1);
        counter = 0;
        // Now detaches the observer and test if it does not receive notifications anymore
        subject.detachObserver(this,Masks.STUB_EVENT);
        subject.fireEvent(new StubEvent());
        assertTrue( counter == 0);
        
        //Now attach this observer for another kind of event and test
        subject.attachObserver(this,Masks.CLOCKTICK);
        subject.fireEvent(new StubEvent());
        assertTrue( counter == 0);
    }

    public void update(Event event) throws LawException {
        counter++;
        events.add(event);
        
        if (orderTest){
        	orderTest=false;
        	subject.fireEvent(e2);        	
        }
    }
    
    public void testEventOrderGeneration(){
    	StubEvent e1 = new StubEvent();    	
    	e2 = new StubEvent();
    	StubEvent e3 = new StubEvent();
    	
    	orderTest = true;
    	
    	subject.attachObserver(this,Masks.STUB_EVENT);
    	
    	subject.fireEvent(e1);
    	subject.fireEvent(e3);
    	
    	assertEquals( e1, events.get(0));
    	assertEquals( e2, events.get(1));
    	assertEquals( e3, events.get(2));
    }

	public void notifySubjectDeath(Subject subject) {
		// TODO Auto-generated method stub
		
	}
    
    
}

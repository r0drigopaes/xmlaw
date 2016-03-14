package br.pucrio.inf.les.law.execution;

import rodrigopaes.utils.binary.BinaryOperations;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.StubEvent;
import br.pucrio.inf.les.law.model.Id;
import junit.framework.TestCase;

public class TriggerTest extends TestCase{

    public void testGetActivationMask(){
        Id id = new Id("theId");
        Trigger trigger = new Trigger();
        
        trigger.addActivation(id,Masks.CLOCKTICK);
        assertTrue( trigger.getActivationMask() == Masks.CLOCKTICK );
        
        trigger.addActivation(id,Masks.MESSAGE_ARRIVAL);
        
        assertTrue (trigger.getActivationMask() == BinaryOperations.add(Masks.CLOCKTICK,Masks.MESSAGE_ARRIVAL));
    }
    
    public void testActivates(){
        Id id = new Id("theId");
        Trigger trigger = new Trigger();        
        trigger.addActivation(id,Masks.CLOCKTICK);
        trigger.addActivation(id,Masks.STUB_EVENT);
        
        assertTrue (trigger.activates(new Event(Masks.CLOCKTICK, id)));
        assertTrue (trigger.activates(new StubEvent(id)));
    }
    
    public void testDeactivates(){
        Id id = new Id("theId");
        Trigger trigger = new Trigger();        
        trigger.addDeactivation(id,Masks.CLOCKTICK);
        trigger.addDeactivation(id,Masks.STUB_EVENT);
        
        assertTrue (trigger.deactivates(new Event(Masks.CLOCKTICK, id)));
        assertTrue (trigger.deactivates(new StubEvent(id)));
    }
}

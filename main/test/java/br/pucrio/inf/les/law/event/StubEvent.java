package br.pucrio.inf.les.law.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.model.Id;


public class StubEvent extends Event {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(StubEvent.class);

    public StubEvent() {
        super(Masks.STUB_EVENT,new Id());
    }
    
    public StubEvent(Id id) {
        super(Masks.STUB_EVENT,id);
    }
    
    

}

package br.pucrio.inf.les.law.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class MasksTest extends TestCase {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(MasksTest.class);

    /**
     * This method tests mainly if the table of Masks is consistently built.
     */
    public void testInit() {
        assertNotNull(Masks.getName(Masks.SCENE_CREATION));
    }

    public void testStubMask() {
        StubEvent event = new StubEvent();
        assertEquals("stub_event",Masks.getName(Masks.STUB_EVENT));
        assertEquals("stub_event", Masks.getName(event.getType()));
    }

}

package br.pucrio.inf.les.law.component.clock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.lang.reflect.Method;
import java.util.Properties;

import javax.xml.parsers.SAXParserFactory;

import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;


public class ClockHandlerTest extends XMLawParserScenario {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ClockHandlerTest.class);

    public void testClock1() {        
        ClockDescriptor clockDescriptor = (ClockDescriptor) table.get(new Id(
                "clock_1"));

        assertNotNull(clockDescriptor);

        assertEquals(clockDescriptor.getTimeout(), 2000);
        assertEquals(clockDescriptor.getType(), ClockExecution.Type.PERIODIC);

        assertTrue(clockDescriptor.getActivationEventType(new Id("t1")) == Masks.TRANSITION_ACTIVATION);
        assertTrue(clockDescriptor.getDeactivationEventType(new Id("m2")) == Masks.MESSAGE_ARRIVAL);

    }

}

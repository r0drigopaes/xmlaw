package br.pucrio.inf.les.law.component.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;

public class MessageHandlerTest extends XMLawParserScenario {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(MessageHandlerTest.class);

    public void testMessage1() {
        MessageDescriptor message = (MessageDescriptor) handlerCache
                .get(new Id("m1"));

        assertNotNull(message);

        assertEquals("request", message.getPerformative());
        assertEquals("23", message.getContentValue("age"));

        assertNotNull(message.getSender());
        assertNotNull(message.getSender().getRoleDescriptor());
        assertEquals(new Id("requester"), message.getSender()
                .getRoleDescriptor().getId());

        RoleReference roleReference = message.getReceivers().get(0);
        assertEquals(new Id("provider"), roleReference.getRoleDescriptor()
                .getId());
        assertTrue(roleReference.getMultiplicity() == 1);
    }

    public void testMessage2() {
        MessageDescriptor message = (MessageDescriptor) handlerCache
                .get(new Id("m2"));
        assertNotNull(message);

        assertEquals(message.getContentValue("name"), "Maira");
        assertEquals(message.getContentValue("age"), "23");

        assertEquals(message.getSender().getRoleInstance(), "aSeller");
        assertEquals(message.getReceivers().get(0).getRoleInstance(), "aBuyer");
        assertEquals(message.getReceivers().get(1).getRoleDescriptor().getId(),
                new Id("observer"));
    }
}

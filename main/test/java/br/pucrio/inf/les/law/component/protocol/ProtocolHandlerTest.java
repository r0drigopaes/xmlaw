package br.pucrio.inf.les.law.component.protocol;

import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;

public class ProtocolHandlerTest extends XMLawParserScenario {

    public void testProtocol() {

        ProtocolDescriptor protocolDescriptor = (ProtocolDescriptor) table
                .get(new Id("contract-net"));
        assertNotNull(protocolDescriptor);

        State s0 = protocolDescriptor.getInitialState();
        assertNotNull(s0);
        assertEquals(s0.getId(), new Id("s0"));
        assertTrue(s0.isInitial());

        // Verifica se a transicao t1 foi adicionada a s0
        Transition t1 = s0.getOutgoingTransition(new Id("t1"));
        assertNotNull(t1);
        assertEquals(t1.getFrom(), s0);
        assertTrue(t1.getActivationType() == Masks.MESSAGE_ARRIVAL);

        // Verifica se existe uma transicao de s0 para s1
        State s1 = t1.getTo();
        assertEquals(s1.getId(), new Id("s1"));
        assertTrue(s1.isExecution());

        // verifica se existe uma transicao de s1 para s2
        Transition t2 = s1.getOutgoingTransition(new Id("t2"));
        assertNotNull(t2);

        assertTrue(t2.getActivationType() == Masks.MESSAGE_ARRIVAL);

        // verifica se s3 é o destino de t2
        assertEquals(t2.getTo().getId(), new Id("s2"));
        assertTrue(t2.getTo().isSuccess());
    }
}

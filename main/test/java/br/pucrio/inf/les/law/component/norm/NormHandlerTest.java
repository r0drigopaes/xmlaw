package br.pucrio.inf.les.law.component.norm;

import br.pucrio.inf.les.law.component.message.RoleReference;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;

public class NormHandlerTest extends XMLawParserScenario {
    public void testNormAcquiredPermission() {
        NormDescriptor normDescriptor = (NormDescriptor) table.get(new Id(
                "acquiredPermission"));
        assertNotNull(normDescriptor);

        RoleReference reference = normDescriptor.getAssignee();
        assertEquals("seller", reference.getRoleDescriptor().getId().toString());
        assertEquals("$seller.instance", reference.getRoleInstance());

        assertTrue( Masks.TRANSITION_ACTIVATION == normDescriptor.getActivationEventType(new Id("t1")));
        assertTrue( Masks.SCENE_SUCCESSFUL_COMPLETION == normDescriptor.getDeactivationEventType(new Id("negotiation")));
        
    }
}

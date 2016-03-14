package br.pucrio.inf.les.law.component.organization;

import br.pucrio.inf.les.law.component.scene.SceneDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;

public class OrganizationHandlerTest extends XMLawParserScenario {

    public void testOrganizationHandler() {
        OrganizationDescriptor organizationDescriptor = (OrganizationDescriptor) table.get(new Id("auction"));

        assertNotNull(organizationDescriptor);

        assertEquals(organizationDescriptor.getName(), "World Auction");

        SceneDescriptor sceneDescriptor = organizationDescriptor.getSceneDescriptors().get(0);
        
        assertEquals(sceneDescriptor.getId(),new Id("game"));
        assertNotNull(organizationDescriptor.getNormDescriptor(new Id("acquiredPermission")));
        assertNotNull(organizationDescriptor.getClockDescriptor(new Id("clock_2")));
        
        //test declared roles
        assertTrue(organizationDescriptor.hasRoleDescriptor(new Id("buyer")));
    }

}

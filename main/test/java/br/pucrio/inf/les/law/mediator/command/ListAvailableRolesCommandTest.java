package br.pucrio.inf.les.law.mediator.command;

import java.util.HashMap;
import java.util.Map;

import br.pucrio.inf.les.law.XMLawScenario;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.mediator.command.validation.MessageValidator;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class ListAvailableRolesCommandTest extends XMLawScenario
{
	private ListAvailableRolesCommand	listRolesCommand;

	public void setUp() throws Exception
	{
		super.setUp();
		listRolesCommand = new ListAvailableRolesCommand();
	}

	public void testName()
	{
		assertEquals(MessageContentConstants.CMD_LIST_ROLES, listRolesCommand.name());
	}

	public void testListAvaiableRolesCommand()
	{
		Message reply = listRolesCommand.executeCommand(requestMessage, null, components);

		assertNotNull(reply);
		assertEquals(Message.INFORM, reply.getPerformative());
		assertEquals(MessageContentConstants.CMD_LIST_ROLES, reply.getContentValue(MessageContentConstants.KEY_COMMAND));

		assertEquals(orgExecution.getId().toString(), reply
				.getContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID));

		String expectedTxtMsg = LawResourceBundle.getInstance()
				.format("msg.info.list_roles",
						orgExecution.getDescriptor().getId().toString(),
						orgExecution.getId().toString());
		assertEquals(expectedTxtMsg, reply.getContentValue(MessageContentConstants.KEY_TXT_MESSAGE));

		// build an Map containing all roles that must be equals to the
		// organization's Map
		OrganizationDescriptor orgDesc = (OrganizationDescriptor) components
				.get(MessageValidator.Component.ORGANIZATION_DESCRIPTOR);
		int total = Integer.parseInt(reply.getContentValue(MessageContentConstants.KEY_TOTAL));
		Map<Id, RoleDescriptor> roles = new HashMap<Id, RoleDescriptor>();
		for (int i = 1; i <= total; i++)
		{
			Id roleId = new Id(reply.getContentValue(MessageContentConstants.KEY_ROLE_NAME + i));
			roles.put(roleId, new RoleDescriptor(roleId));
		}

		// Map equals verifies if all the elements are equally mapped
		assertEquals(orgDesc.getRoleDescriptors(), roles);

	}

}

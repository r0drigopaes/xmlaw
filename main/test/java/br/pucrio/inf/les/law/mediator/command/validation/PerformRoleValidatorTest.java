package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class PerformRoleValidatorTest extends MessageValidatorScenario 
{
	public void setUp() throws Exception 
	{	
		super.setUp();
		handler = new PerformRoleValidator();
		
		components.put(MessageValidator.Component.ORGANIZATION_EXECUTION,orgExecution);
		components.put(MessageValidator.Component.ROLE_DESCRIPTOR,new RoleDescriptor(new Id("seller")));
	}
	
	public void testAgentDoesNotPerformRole()
	{
		String expected = LawResourceBundle.getInstance().format("msg.doesnt_perform_role","seller");
		sendMessageAndAssertFailure(expected);
	}
	
	public void testAgentPerformRole()
	{
		orgExecution.addLogedAgent(sender);
		orgExecution.addRoleToAgent(sender,new RoleDescriptor(new Id("seller")));
		
		Message response = handler.handle(message,manager,components);
		
		assertNull(response);
	}

}

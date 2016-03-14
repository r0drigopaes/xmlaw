package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class EnteredOrganizationValidatorTest extends MessageValidatorScenario 
{
	public void setUp() throws Exception 
	{
		super.setUp();
		handler = new EnteredOrganizationValidator();
		components.put(MessageValidator.Component.ORGANIZATION_EXECUTION,orgExecution);
	}
	
	public void testAgentNotEnteredOrganization()
	{	
		String expected = LawResourceBundle.getInstance().format("msg.not_entered_org",orgExecution.getDescriptor().getId());
		sendMessageAndAssertFailure(expected);
	}
	
	public void testAgentEnteredOrganizationTest()
	{
		orgExecution.addLogedAgent(sender);		
		Message response = handler.handle(message,manager,components);
		
		assertNull(response);		
	}

}

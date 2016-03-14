package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.util.LawResourceBundle;


public class ExecutionManagerValidatorTest extends MessageValidatorScenario
{
	public void setUp() throws Exception 
	{
		super.setUp();
		handler = new ExecutionManagerValidator();
	}
	
	public void testExecutionManagerOk()
	{
		Message response = handler.handle(message,manager,components);
		
		assertNull(response);
	}
	
	public void testExecutionManagerAddLaw()
	{
		message.setContentValue(MessageContentConstants.KEY_COMMAND,MessageContentConstants.CMD_ADD_LAW);
		Message response = handler.handle(message,null,components);
		
		assertNull(response);
	}
	
	public void testExecutionManagerNull()
	{
		manager = null;
		String expected = LawResourceBundle.getInstance().getString("msg.no_exec_man");
		sendMessageAndAssertFailure(expected);
	}
	
	


}

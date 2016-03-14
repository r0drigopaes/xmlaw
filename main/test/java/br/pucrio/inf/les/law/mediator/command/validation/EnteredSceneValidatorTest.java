package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class EnteredSceneValidatorTest extends MessageValidatorScenario 
{
	public void setUp() throws Exception 
	{	
		super.setUp();
		handler = new EnteredSceneValidator();
		
		components.put(MessageValidator.Component.SCENE_EXECUTION,sceneExecution);
	}
	
	public void testNotEnteredScene()
	{
		String expected = LawResourceBundle.getInstance().format("msg.not_entered_scene",sceneExecution.getId());
		sendMessageAndAssertFailure(expected);
	}
	
	public void testEnteredScene()
	{
		sceneExecution.forceSceneEntrance(sender,new RoleDescriptor(new Id("buyer")));
		
		Message response = handler.handle(message,manager,components);
		
		assertNull(response);
	}

}

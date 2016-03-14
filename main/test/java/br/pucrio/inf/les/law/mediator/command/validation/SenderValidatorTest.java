package br.pucrio.inf.les.law.mediator.command.validation;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class SenderValidatorTest extends MessageValidatorScenario {

	public void setUp() throws Exception {
		super.setUp();
		handler = new SenderValidator();
	}

	public void testNoSender() {
		message.setSender(null, null);
		sendMessageAndAssertFailure(LawResourceBundle.getInstance().getString(
				"msg.no_sender"));
	}

	public void testSender() {
		Message response = handler.handle(message, manager, components);

		assertNull(response);
	}

}

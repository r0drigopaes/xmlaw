package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class SenderValidator extends MessageValidator {

	@Override
	protected Message handle(Message message, ExecutionManager manager,
			Map<Component, Object> components) {
		// no sender set
		if (message.getSender() == null) {
			return createFailureReply(message, LawResourceBundle.getInstance()
					.getString("msg.no_sender"));
		}

		return null;
	}

}

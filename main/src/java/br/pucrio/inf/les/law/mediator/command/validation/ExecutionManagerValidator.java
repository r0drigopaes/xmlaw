package br.pucrio.inf.les.law.mediator.command.validation;

import java.util.Map;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.MessageContentConstants;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.util.LawResourceBundle;

public class ExecutionManagerValidator extends MessageValidator {

	@Override
	protected Message handle(Message message, ExecutionManager manager,
			Map<Component, Object> components) {
		if (manager != null) {
			return null;
		}

		// at this point the manager is null
		String command = message.getContentValue(MessageContentConstants.KEY_COMMAND);
		if (command != null && command.equals(MessageContentConstants.CMD_ADD_LAW)) {
			return null;
		}

		return createFailureReply(message, LawResourceBundle.getInstance()
				.getString("msg.no_exec_man"));
	}

}

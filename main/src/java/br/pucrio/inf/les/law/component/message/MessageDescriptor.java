package br.pucrio.inf.les.law.component.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.model.Id;

public class MessageDescriptor
{

	private Id						id;

	private String					performative;

	private Map<String, Pattern>	content		= new HashMap<String, Pattern>();

	private RoleReference			sender		= new RoleReference();

	private List<RoleReference>		receivers	= new ArrayList<RoleReference>();

	public void setPerformative(String performative)
	{
		this.performative = performative;
	}

	public Id getId()
	{
		return id;
	}

	public void addToContent(String key, String regex)
	{
		content.put(key, Pattern.compile(regex));
	}

	public void setSender(RoleDescriptor roleDescriptor, String roleInstance)
	{
		sender.setRoleInstance(roleInstance);
		sender.setRoleDescriptor(roleDescriptor);
	}

	public void addReceiver(RoleDescriptor roleDescriptor, String roleInstance, int multiplicity)
	{
		RoleReference roleRef = new RoleReference();
		roleRef.setRoleInstance(roleInstance);
		roleRef.setRoleDescriptor(roleDescriptor);
		roleRef.setMultiplicity(multiplicity);
		receivers.add(roleRef);
	}

	public String getContentValue(String key)
	{
		return content.get(key).toString();
	}

	public String getPerformative()
	{
		return performative;
	}

	public List<RoleReference> getReceivers()
	{
		return receivers;
	}

	public RoleReference getSender()
	{
		return sender;
	}

	public void setId(Id id)
	{
		this.id = id;
	}

	public boolean matches(Message message)
	{
		if (!message.getPerformative().equals(getPerformative()))
		{
			return false;
		}

		if (!message.getSenderRole().equals( getSender().getRoleDescriptor().getId().toString() )){
			return false;
		}
		
		//TODO verificar para receiver = any
       	for (RoleReference receiver : getReceivers()) {
   			String roleMsgSent = receiver.getRoleDescriptor().getId().toString();
   			//checks if the role of the agent that is in the receiver list specified
				//is the same that in the message
   			if (!message.getAllReceivers().containsValue(roleMsgSent)){
   				return false;
   			}
		}
		
		
		// verifies if the content attribute names match and if the massage
		// values match the descriptor regular expression
		for (Map.Entry<String, Pattern> entry : content.entrySet())
		{
			if (!message.getContent().containsKey(entry.getKey()))
			{
				return false;
			}

			Matcher matcher = entry.getValue().matcher(message.getContentValue(entry.getKey()));
			if(!matcher.matches())
			{
				return false;
			}
		}

		return true;
	}
}

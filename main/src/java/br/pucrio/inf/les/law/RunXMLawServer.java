package br.pucrio.inf.les.law;

import br.pucrio.inf.les.law.mediator.Mediator;
import br.pucrio.inf.les.law.model.LawException;

public class RunXMLawServer 
{
	public static void main(String[] args) throws LawException 
	{
		Mediator mediator = new Mediator();
        mediator.startMediator();
	}
}

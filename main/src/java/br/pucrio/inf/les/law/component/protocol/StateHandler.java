/* ****************************************************************
 *   @filename:		StateHandler.java
 *   @projectname:  LawRefactoring
 *   @date:			10/05/2005
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.component.protocol;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

/**
 * @author Rodrigo - LES (PUC-Rio)
 * @version $Revision$
 */
public class StateHandler extends AbstractHandler {

    public StateHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id,Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    public void process(String namespaceURI, String localName, String qName,
            Attributes attr) throws LawException {
        
        ProtocolDescriptor protocolDescriptor = (ProtocolDescriptor) stack
                .peek(); // Protocol

        String typeStr = attr.getValue("type");
        String label = attr.getValue("label");

        State.Type type = null;

        if ("initial".equals(typeStr)) {
            type = State.Type.INITIAL;
        } else if ("execution".equals(typeStr)) {
            type = State.Type.EXECUTION;
        } else if ("success".equals(typeStr)) {
            type = State.Type.SUCCESS;
        } else if ("failure".equals(typeStr)) {
            type = State.Type.FAILURE;
        }

        State state = new State(getId(), label, type);

        if (state.isInitial()){
            protocolDescriptor.setInitialState(state);
        }
        
        // Tabela de cache
        handlerCache.put(state.getId(),state);
    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub
        
    }

}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.1  2005/12/29 16:35:30  rbp
 * UPDATE LOG: Versão da primeira pausa do pair-programming. Paramos depois da conversa com o guga.
 * UPDATE LOG: - Incluido o arquivo Brainstorm de planejamento, que contem os principios e passos de planejamento.
 * UPDATE LOG: - Incluido uma primeira versao da proposta do xml.
 * UPDATE LOG: UPDATE LOG: Revision 1.1 2005/12/05 18:29:48 rbp UPDATE
 * LOG: Versão inicial UPDATE LOG: Revision 1.1 2005/08/17 17:48:14 lfrodrigues
 * instalacao do maven como ferramenta de build automatico Revision 1.1
 * 2005/05/16 09:23:02 guga After 1st phase refactoring - backup
 ******************************************************************************/

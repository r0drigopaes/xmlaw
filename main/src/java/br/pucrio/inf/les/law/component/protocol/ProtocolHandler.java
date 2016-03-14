/* ****************************************************************
 *   @filename:		ProtocolHandler.java
 *   @projectname:  LawRefactoring
 *   @date:			10/05/2005
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.component.protocol;

import br.pucrio.inf.les.law.component.scene.SceneDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

import java.util.Hashtable;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

/**
 * @author Rodrigo - LES (PUC-Rio)
 * @version $Revision$
 */
public class ProtocolHandler extends ComposedHandler {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ProtocolHandler.class);

    public ProtocolHandler(Stack<Object> stack, DescriptorTable table,
            Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    public void process(String namespaceURI, String localName, String qName,
            Attributes attr) {
        SceneDescriptor scene = (SceneDescriptor) stack.peek(); //Scene

        ProtocolDescriptor protocol = new ProtocolDescriptor(getId());
        scene.setProtocolDescriptor(protocol);

        // Pilha de contextos
        stack.push(protocol);

        // Tabela de descritores
        table.add(protocol);
    }

    public void solvePendencies() throws LawException {
    }

}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.1  2005/12/29 16:35:30  rbp
 * UPDATE LOG: Versão da primeira pausa do pair-programming. Paramos depois da conversa com o guga.
 * UPDATE LOG: - Incluido o arquivo Brainstorm de planejamento, que contem os principios e passos de planejamento.
 * UPDATE LOG: - Incluido uma primeira versao da proposta do xml.
 * UPDATE LOG: Revision 1.1 2005/12/05 18:29:48 rbp Versão inicial
 * Revision 1.1 2005/08/17 17:48:14 lfrodrigues instalacao do maven como
 * ferramenta de build automatico Revision 1.1 2005/05/16 09:23:02 guga After
 * 1st phase refactoring - backup
 ******************************************************************************/
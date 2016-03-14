/* ****************************************************************
 *   @filename:		ComposedHandler.java
 *   @projectname:  Law
 *   @date:			Dec 28, 2005
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.parser.handler.base;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.io.CharArrayWriter;
import java.util.Hashtable;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

/**
 * Tags that may contain other tags extend this class in order to avoid stack
 * manipulation.
 * 
 * @author Rodrigo - LES (PUC-Rio)
 * @version $Revision$
 */
public abstract class ComposedHandler extends AbstractHandler {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ComposedHandler.class);

    public ComposedHandler(Stack<Object> stack, DescriptorTable table,
            Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    /**
     * This method is overrided to put the composed tag in the stack.
     * 
     * @throws LawException
     */
    public final void onStart(String namespaceURI, String localName,
            String qName, Attributes attr) throws LawException {
        super.onStart(namespaceURI, localName, qName, attr);
    }

    /**
     * This method is overrided to remove the composed tag from the stack.
     */
    public final void onEnd(String namespaceURI, String localName,
            String qName, CharArrayWriter contents) {
        try {
        	LOG.trace("Pop: " + stack.peek().toString());
            stack.pop();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.3  2006/05/02 23:33:55  mgatti
 * UPDATE LOG: 02/05/2006:  XMLaw - Actions e Constraints
 * UPDATE LOG: Está dando quando as duas cenas de uma mesma organização estão especificadas (o "saco" dos states e transitions está pegando tudo)
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.2  2005/12/29 16:35:30  rbp
 * UPDATE LOG: Versão da primeira pausa do pair-programming. Paramos depois da conversa com o guga.
 * UPDATE LOG: - Incluido o arquivo Brainstorm de planejamento, que contem os principios e passos de planejamento.
 * UPDATE LOG: - Incluido uma primeira versao da proposta do xml.
 * UPDATE LOG: Revision 1.1 2005/12/05 18:29:48 rbp Versão inicial
 * Revision 1.1 2005/08/17 17:48:14 lfrodrigues instalacao do maven como
 * ferramenta de build automatico Revision 1.1 2005/05/16 09:23:02 guga After
 * 1st phase refactoring - backup Revision 1.1 2005/05/10 16:22:57 guga Inicio
 * Refactoring Revision 1.1 2005/04/29 21:40:55 guga Modificações na estrutura e
 * primeira versao da implementacao do TAC SCM Revision 1.3 2004/11/24 01:06:04
 * rbp Main modifications: - Allow multiple instances of the same scene -
 * Introducing a 3 layer mapping from XML: XML --> Descriptor Layer --> Model
 * Layer - Framework of actions introduced; now it is possible use java code as
 * consequence of some law configuration. - Implementation of a case study in
 * the context of product trading using SWT and JFace ( it requires to configure
 * the eclipse for running the application) - Some bugs were fixed altough I
 * don't remember of them. Revision 1.1 2004/09/22 01:04:38 rbp This version
 * contains severals modifications: - Package renaming - Specification of laws
 * from XML language - Added a Sax based Xml parser for reading from XML - Some
 * hard-coded code were removed and placed in the Config.properties file - Added
 * the simple application example
 ******************************************************************************/

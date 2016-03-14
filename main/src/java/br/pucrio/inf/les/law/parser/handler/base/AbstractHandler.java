/* ****************************************************************
 *   @filename:		AbstractHandler.java
 *   @projectname:  LawRefactoring
 *   @date:			10/05/2005
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.parser.handler.base;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.saxmapper.TagTracker;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

/**
 * This class contains the life cycle for all tag handlers.
 * 
 * @author Rodrigo - LES (PUC-Rio)
 * @version $Revision$
 */
public abstract class AbstractHandler extends TagTracker {

    /**
     * Shared stack used for put ComposedTags available to their child tags.
     */
    protected Stack<Object> stack;

    protected DescriptorTable table;

    /**
     * All the implementors of this class are put in the cache just after the
     * invocation of the process() method.
     */
    protected Hashtable<Id, Object> handlerCache;

    /**
     * The id of each handler. This id is gotten from the XML.
     */
    protected String id;

    public AbstractHandler(Stack<Object> stack, DescriptorTable table,
            Hashtable<Id, Object> handlerCache) {
        this.stack = stack;
        this.table = table;
        this.handlerCache = handlerCache;
    }

    /**
     * If the clas inherits direcly from this abstract handler, this method
     * should not be overrided.
     * 
     * @throws LawException
     */
    public void onStart(String namespaceURI, String localName, String qName,
            Attributes attr) throws LawException {

        id = attr.getValue("id");

        if (id == null) {
            id = new Id().toString();
        }

        process(namespaceURI, localName, qName, attr);

    }

    /**
     * This method is automaticaly invoked. It should get all the attributes of
     * the XML tag.
     * 
     * @author Rodrigo
     */
    public abstract void process(String namespaceURI, String localName,
            String qName, Attributes attr) throws LawException;

    /**
     * Creates a descriptor based on the xml definition.
     */
    public abstract void solvePendencies() throws LawException;

    public Id getId() {
        return new Id(id);
    }
}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.2  2005/12/29 16:35:30  rbp
 * UPDATE LOG: Versão da primeira pausa do pair-programming. Paramos depois da conversa com o guga.
 * UPDATE LOG: - Incluido o arquivo Brainstorm de planejamento, que contem os principios e passos de planejamento.
 * UPDATE LOG: - Incluido uma primeira versao da proposta do xml.
 * UPDATE LOG: Revision 1.1 2005/12/05 18:29:48 rbp Versão inicial
 * Revision 1.1 2005/08/17 17:48:14 lfrodrigues instalacao do maven como
 * ferramenta de build automatico Revision 1.1 2005/05/16 09:23:02 guga After
 * 1st phase refactoring - backup
 ******************************************************************************/
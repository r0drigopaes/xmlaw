/*
 * Created on 11/05/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package br.pucrio.inf.les.law.parser.handler;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

/**
 * @author LESUser TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class LawsHandler extends AbstractHandler {

    public LawsHandler(Stack<Object> stack, DescriptorTable table,
            Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(LawsHandler.class);

    private Vector<AbstractHandler> trackerSolver = new Vector<AbstractHandler>();

    public void process(String namespaceURI, String localName, String qName,
            Attributes attr) {

    }

    public void track(String tagName, AbstractHandler tracker) {
        if (!tracker.equals(this)) {
            trackerSolver.add((AbstractHandler) tracker);
        } else {
            LOG.debug(tracker.toString());
        }
        super.track(tagName, tracker);
    }

    public void solvePendencies() throws LawException {
        for (AbstractHandler element : trackerSolver) {
            element.solvePendencies();
        }
    }

}

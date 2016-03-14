/* ****************************************************************
 *   @filename:     IObserver.java
 *   @projectname:  Law
 *   @date:         Jun 23, 2004
 *   @author        rbp
 *
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.event;

import br.pucrio.inf.les.law.model.LawException;

/**
 * Implementa o padr�o Observer. Ver GoF[95].
 * 
 * @alias IObserver
 * @author rbp & guga - LES (PUC-Rio)
 * @version $Revision$
 */
public interface IObserver {

    /**
     * M�todo que � invocado pelo ISubject avisando da ocorr�ncia de um
     * determinado evento.
     * 
     * @param event
     *            o evento ocorrido.
     * @throws LawException
     * @author rbp
     */
    void update(Event event) throws LawException;
    
    void notifySubjectDeath(Subject subject);
}
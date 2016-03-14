/* ****************************************************************
 *   @filename:		RunParser.java
 *   @projectname:  Law
 *   @date:			Sep 13, 2004
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

/**
 * @author Rodrigo - LES (PUC-Rio)
 * @version $Revision$
 */
public class XMLawParser {
	/**
	 * Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(XMLawParser.class);

	private XMLawReader reader;

	private Hashtable<Id, Object> handlerCache;

	public XMLawParser() {
		System.setProperty("org.xml.sax.driver",
				"org.apache.xerces.parsers.SAXParser");

	}

	public DescriptorTable load(InputStream in, String inputLocation) throws IOException, SAXException, LawException{

		reader = new XMLawReader(new DescriptorTable());
		LOG.info("Antes: "+inputLocation);
		DescriptorTable lawDescriptor = (DescriptorTable) reader.fromXML(in,inputLocation);
		LOG.info("Depois: "+inputLocation);
		handlerCache = reader.getHandlerCache();
		return lawDescriptor;
	}

	public Hashtable<Id, Object> getHandlerCache() {
		return handlerCache;
	}

}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.4  2006/02/22 20:49:30  mgatti
 * UPDATE LOG: Maíra: 1ª Versão da Integração DimaX & XMLaw
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.3  2006/01/26 12:56:26  lfrodrigues
 * UPDATE LOG: correcao no parser, ele recebe uma string indicando o local absoluto do arquivo a serer lido, isso é necessario para o trabalho em caminhos relativos
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.2  2006/01/25 20:22:55  lfrodrigues
 * UPDATE LOG: lancamento de excecoes no metodo load, o tratamento é delegado ao cliente do metodo
 * UPDATE LOG: UPDATE LOG: Revision 1.1 2005/12/29 16:35:30 rbp UPDATE
 * LOG: Versão da primeira pausa do pair-programming. Paramos depois da conversa
 * com o guga. UPDATE LOG: - Incluido o arquivo Brainstorm de planejamento, que
 * contem os principios e passos de planejamento. UPDATE LOG: - Incluido uma
 * primeira versao da proposta do xml. UPDATE LOG: Revision 1.1 2005/12/05
 * 18:29:48 rbp Versão inicial Revision 1.1 2005/08/17 17:48:14 lfrodrigues
 * instalacao do maven como ferramenta de build automatico Revision 1.2
 * 2005/05/16 09:23:02 guga After 1st phase refactoring - backup Revision 1.1
 * 2005/05/10 16:22:56 guga Inicio Refactoring Revision 1.6 2005/04/29 21:52:11
 * guga Modificações na estrutura e primeira versao da implementacao do TAC SCM
 * Revision 1.4 2005/02/15 20:56:04 rbp Versão da dissertação Revision 1.3
 * 2004/11/24 01:06:04 rbp Main modifications: - Allow multiple instances of the
 * same scene - Introducing a 3 layer mapping from XML: XML --> Descriptor Layer
 * --> Model Layer - Framework of actions introduced; now it is possible use
 * java code as consequence of some law configuration. - Implementation of a
 * case study in the context of product trading using SWT and JFace ( it
 * requires to configure the eclipse for running the application) - Some bugs
 * were fixed altough I don't remember of them. Revision 1.1 2004/09/22 01:04:38
 * rbp This version contains severals modifications: - Package renaming -
 * Specification of laws from XML language - Added a Sax based Xml parser for
 * reading from XML - Some hard-coded code were removed and placed in the
 * Config.properties file - Added the simple application example
 ******************************************************************************/

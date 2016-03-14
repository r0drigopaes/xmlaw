/* ****************************************************************
 *   @filename:     IdGenerator.java
 *   @projectname:  Law
 *   @date:         Mar 5, 2004
 *   @author        rbp
 *
 *   $Id: IdGenerator.java,v 1.2 2006/02/07 10:19:02 lfrodrigues Exp $
 * ***************************************************************/
package br.pucrio.inf.les.law.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Gerador de Ids. Esta classe pode ser utilizada, por exemplo, para
 * a geração de conversationIds.
 * 
 * @author rbp 
 * @version $Id: IdGenerator.java,v 1.2 2006/02/07 10:19:02 lfrodrigues Exp $
 */
public class IdGenerator {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(IdGenerator.class);

	private static IdGenerator singletonInstance = null;
	
	
	/**
	 * Em Java um int tem 32 bits o que dá um total de 
	 * 2^32 = 4294967296 números. Sendo que um deles é
	 * o zero e as outras metades são para os números
	 * positivos e negativos. Logo, o valor abaixo
	 * representa o maior valor possível para um 
	 * inteiro positivo em JAVA.
	 */
	private final int MAX = 2147483647;
							
	/**
	 * Armazena o id atual
	 */
	private int currentId;
	
	/**
	 * Nome do arquivo onde o Id atual é salvo
	 */
	private final String FILE_NAME = ConfigUtils.getTempDir()+"currentId.id";
	
	private IdGenerator() {
		try {
			/* Lê o último valor gerado por este objeto */
			FileInputStream fis = new FileInputStream(FILE_NAME);
			DataInputStream dis = new DataInputStream(fis);
			this.currentId = dis.readInt();
			dis.close();
			fis.close();
		} catch (Exception e) {
			this.currentId = 0;
		}
		
	}

	/**
	 * Retorna uma instância única de um gerador de Ids.
	 * @return
	 */
	public static IdGenerator getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new IdGenerator();
		}
		return singletonInstance;
	}
	
	/**
	 * Recupera um novo id.
	 * 
	 * @return o novo id	 
	 * @pos o arquivo currentId.id é salvo em disco de forma que o id atual seja sempre persistido.	 
	 * @author rbp
	 */
	public synchronized int getNewId(){
		this.currentId = ((this.currentId) % MAX)+1;
		this.save();
		return this.currentId;
	}
	
	/** 
	 * Grava o número atual da sequência para
	 * que depois possa recomeçar de onde parou.
	 */
	private void save(){		
		try {
			FileOutputStream fos = new FileOutputStream(FILE_NAME);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeInt(this.currentId);
			dos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			LOG.warn("Não foi possível salvar o arquivo de identificadores: podem ocorrer problemas com identificadores duplicados");
		} catch (IOException e) {
			LOG.warn("Não foi possível salvar o arquivo de identificadores: podem ocorrer problemas com identificadores duplicados");
		}	
	}	

}

/* ****************************************************************
 * UPDATE LOG:
 * 
 * $Log: IdGenerator.java,v $
 * Revision 1.2  2006/02/07 10:19:02  lfrodrigues
 * configuração da plataforma para utilizacao de diretorio de arquivos temporarios
 *
 * Revision 1.1  2006/02/01 20:18:45  lfrodrigues
 * mudificações no pacote model para melhor modularização do projeto em api cliente e servidor
 *
 * Revision 1.1  2005/12/05 18:29:48  rbp
 * Versão inicial
 *
 * Revision 1.1  2005/08/17 17:48:15  lfrodrigues
 * instalacao do maven como ferramenta de build automatico
 *
 * Revision 1.1  2005/05/10 16:23:01  guga
 * Inicio Refactoring
 *
 * Revision 1.3  2004/11/24 01:06:19  rbp
 * Main modifications:
 * - Allow multiple instances of the same scene
 * - Introducing a 3 layer mapping from XML: XML --> Descriptor Layer --> Model Layer
 * - Framework of actions introduced; now it is possible use java code as consequence of some law configuration.
 * - Implementation of a case study in the context of product trading using SWT and JFace ( it requires to configure the eclipse for running the application)
 * - Some bugs were fixed altough I don't remember of them.
 *
 * Revision 1.1  2004/09/22 01:04:38  rbp
 * This version contains severals modifications:
 * - Package renaming
 * - Specification of laws from XML language
 * - Added a Sax based Xml parser for reading from XML
 * - Some hard-coded code were removed and placed in the Config.properties file
 * - Added the simple application example
 *
 * Revision 1.2  2004/06/29 16:14:58  guga
 * alteracao execucao em paralelo
 *
 * Revision 1.1  2004/06/26 23:34:28  rbp
 * Melhorias na documentação do Código.
 * Especificação do cenário de testes.
 * Modificações no Log do sistema.
 * Algumas correções em classes como Clock que estava implementando a interface Runnable de uma forma não muito segura.
 *
 *
 * ***************************************************************/
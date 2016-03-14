package br.pucrio.inf.les.law.communication.socket;

import java.util.List;

public interface IList {
	/**
	 * Remove um objeto da lista
	 * @return Um objeto da lista
	 */
	public Object pop();
	
	/**
	 * Adiciona um objeto na lista
	 * @param item
	 */
	public void push(Object item);
	
	/**
	 * Adiciona uma lista de objetos
	 * @param item
	 */
	public void pushAll(List itens);
	
	/**
	 * Retorna true se a lista estiver vazia
	 */
	public boolean isEmpty();
}

package org.transparenciasjc.cicloviassp.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.transparenciasjc.cicloviassp.model.Ciclovia;

@Stateless
public class CicloviaService {

	@PersistenceContext
	EntityManager em;

	/**
	 * Todas as ciclovias do banco de dados
	 * 
	 * @return
	 */
	public List<Ciclovia> buscarTodos() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * Retorna uma única ciclovia por ID
	 * 
	 * @param id
	 * @return
	 */
	public Optional<Ciclovia> porId(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * As ocorrências da ciclovia somadas e agrupadas de acordo com o ano
	 * passado
	 * 
	 * @param c
	 * @param ano
	 * @return Um mapa onde a chave é o mês e a soma o valor.
	 */
	public Map<Object, Long> ocorrenciasSomadas(Ciclovia c, int ano) {
		return null;

	}

	public Map<Object, Long> ocorrenciasSomadas(Ciclovia c, int ano, int mes) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<Object, Long> ocorrenciasSomadas(Ciclovia c, int ano, int mes,
			int dia) {
		// TODO Auto-generated method stub
		return null;
	}

}

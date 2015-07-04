package org.transparenciasjc.cicloviassp.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.transparenciasjc.cicloviassp.model.ArquivoProcessado;
import org.transparenciasjc.cicloviassp.model.Ocorrencia;

/**
 * 
 * Uma classe de acesso aos dados da aplicação. Serve para acesso aos dados de
 * ciclovia, as ocorrências e os meta dados.
 * 
 * TODO: No futuro tirar SQL e usar os CSVs diretamente?
 * 
 * @author wsiqueir
 *
 */
@Stateless
public class CicloviasSPService {

	@PersistenceContext
	EntityManager em;

	public void salvar(Ocorrencia ocorrencia) {
		em.persist(ocorrencia);
	}

	public void salvar(ArquivoProcessado arquivoOcorrenciaProcessado) {
		em.persist(arquivoOcorrenciaProcessado);
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
	public Map<Object, Long> ocorrenciasSomadas(long ciclovia, int ano) {
		TypedQuery<Object[]> busca = em.createNamedQuery("SomaOcorrenciaPorCicloviaAno", Object[].class);
		busca.setParameter("ano", ano);
		busca.setParameter("ciclovia", ciclovia);
		return montaMapaAgregacao(busca.getResultList());

	}

	public Map<Object, Long> ocorrenciasSomadas(long ciclovia, int ano,
			int mes) {
		TypedQuery<Object[]> busca = em.createNamedQuery("SomaOcorrenciaPorCicloviaAnoMes", Object[].class);
		busca.setParameter("ano", ano);
		busca.setParameter("mes", mes);
		busca.setParameter("ciclovia", ciclovia);
		return montaMapaAgregacao(busca.getResultList());
	}

	public Map<Object, Long> ocorrenciasSomadas(long ciclovia, int ano,
			int mes, int dia) {
		TypedQuery<Object[]> busca = em.createNamedQuery("SomaOcorrenciaPorCicloviaAnoMesDia", Object[].class);
		busca.setParameter("ano", ano);
		busca.setParameter("mes", mes);
		busca.setParameter("dia", dia);
		busca.setParameter("ciclovia", ciclovia);
		return montaMapaAgregacao(busca.getResultList());
	}

	public List<String> arquivosProcessados() {
		CriteriaQuery<ArquivoProcessado> cq = em.getCriteriaBuilder()
				.createQuery(ArquivoProcessado.class);
		cq.select(cq.from(ArquivoProcessado.class));
		List<ArquivoProcessado> processados = em.createQuery(cq)
				.getResultList();
		return processados.stream().map(ArquivoProcessado::getArquivo)
				.collect(Collectors.toList());
	}

	private Map<Object, Long> montaMapaAgregacao(List<Object[]> linhas) {
		return linhas.stream().collect(
				Collectors.toMap(l -> l[0],
						l -> Long.parseLong(l[1].toString())));
	}

}
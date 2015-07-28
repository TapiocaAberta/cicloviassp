package org.transparenciasjc.cicloviassp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.transparenciasjc.cicloviassp.model.AnoMesesDiasDisponiveis;
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
		TypedQuery<Object[]> busca = em.createNamedQuery(
				"SomaOcorrenciaPorCicloviaAno", Object[].class);
		busca.setParameter("ano", ano);
		busca.setParameter("ciclovia", ciclovia);
		return montaMapaAgregacao(busca.getResultList());

	}

	public Map<Object, Long> ocorrenciasSomadas(long ciclovia, int ano, int mes) {
		TypedQuery<Object[]> busca = em.createNamedQuery(
				"SomaOcorrenciaPorCicloviaAnoMes", Object[].class);
		busca.setParameter("ano", ano);
		busca.setParameter("mes", mes);
		busca.setParameter("ciclovia", ciclovia);
		return montaMapaAgregacao(busca.getResultList());
	}

	public Map<Object, Long> ocorrenciasSomadas(long ciclovia, int ano,
			int mes, int dia) {
		TypedQuery<Object[]> busca = em.createNamedQuery(
				"SomaOcorrenciaPorCicloviaAnoMesDia", Object[].class);
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

	/**
	 * 
	 * Método para retornar a lista de anos, meses e dias disponíveis para consulta
	 * 
	 * 
	 * @param id
	 * @return
	 */
	public List<AnoMesesDiasDisponiveis> anosMesesDiasDisponiveis(long id) {
		//TODO: fazer cache dessa bagunça
		// Se alguém ver isso e tiver uma forma melhor de fazer me fale! To com sono e quero dormir..
		List<AnoMesesDiasDisponiveis> anosAnoMesesDiasDisponiveis;
		TypedQuery<Object[]> busca = em.createNamedQuery("AnosMesesDisponiveis", Object[].class);
		busca.setParameter("ciclovia", id);
		List<Object[]> resultado = busca.getResultList();
		// coletando os anos disponíveis primeiramente
		anosAnoMesesDiasDisponiveis = resultado.stream().map(l -> Integer.valueOf(l[0].toString()))
				.distinct()
				.map(AnoMesesDiasDisponiveis::new)			
				.collect(Collectors.toList());
		// agora para cada ano vamos coletar os meses e agrupar em um mapa cujo valor vai ser os dias para aquele mês
		for(AnoMesesDiasDisponiveis anoMesDiaDisponivel : anosAnoMesesDiasDisponiveis) {
			Map<Integer, List<Integer>> mesesDias = new HashMap<>();			
			final int ano = anoMesDiaDisponivel.getAno();
			// vamos pegar os meses disponíveis para esse ano
			Set<Integer> meses = resultado.stream()
					.filter(o -> ano == Integer.valueOf(o[0].toString()))
					.map(o -> String.valueOf(o[1])).map(Integer::valueOf)
					.distinct()
					.collect(Collectors.toSet());
			 // com os meses podemos pegar os dias!
			for(int mes : meses) {
				List<Integer> dias = resultado.stream().filter(o -> {
					int a =  Integer.valueOf(o[0].toString());
					int m = Integer.valueOf(o[1].toString());
					return ano == a && mes == m;
				})
				.map(o -> Integer.valueOf(o[2].toString()))
				.collect(Collectors.toList());
				mesesDias.put(mes, dias);
			}
			// agora sim configuramos nosso objeto
			anoMesDiaDisponivel.setMesesDias(mesesDias);
		}
		return anosAnoMesesDiasDisponiveis;
	}
	
	public List<Ocorrencia> rankingPorDia() {
		// TODO: Exceção vai ser lançada se esses métodos forem invocados. Implementar as named queries no futuro.
		TypedQuery<Ocorrencia> busca = em.createNamedQuery(
				"MontaRankingPorDia", Ocorrencia.class);		
		return busca.getResultList();
	}
	
	public List<Ocorrencia> rankingPorMes() {
		// TODO: Exceção vai ser lançada se esses métodos forem invocados. Implementar as named queries no futuro.
		TypedQuery<Ocorrencia> busca = em.createNamedQuery(
				"MontaRankingPorMes", Ocorrencia.class);		
		return busca.getResultList();
	}

}
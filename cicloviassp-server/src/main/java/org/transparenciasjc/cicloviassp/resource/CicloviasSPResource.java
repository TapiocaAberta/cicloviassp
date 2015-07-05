package org.transparenciasjc.cicloviassp.resource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.transparenciasjc.cicloviassp.helpers.CicloviaHelper;
import org.transparenciasjc.cicloviassp.model.AnoMesesDiasDisponiveis;
import org.transparenciasjc.cicloviassp.service.CicloviasSPService;

/**
 * 
 * O recurso REST para expor os dados para acesso via HTTP exclusivamente no
 * formato JSON.
 * 
 * @author wsiqueir
 *
 */
@Path("ciclovia")
@Produces(MediaType.APPLICATION_JSON)
public class CicloviasSPResource {

	final static String IDS_SEPARADOR = ";";

	@Inject
	CicloviasSPService service;

	@GET
	@Path("{ids}/ocorrencias/{ano}")
	public Map<String, Map<Object, Long>> ocorrenciasPorAno(
			@PathParam("ids") String ids, @PathParam("ano") int ano) {
		return montaMapaDaSoma(ids, c -> {
			return service.ocorrenciasSomadas(c, ano);
		});
	}

	@GET
	@Path("{ids}/ocorrencias/{ano}/{mes}")
	public Map<String, Map<Object, Long>> ocorrenciasPorMes(
			@PathParam("ids") String ids, @PathParam("ano") int ano,
			@PathParam("mes") int mes) {
		return montaMapaDaSoma(ids, c -> {
			return service.ocorrenciasSomadas(c, ano, mes);
		});
	}

	@GET
	@Path("{ids}/ocorrencias/{ano}/{mes}/{dia}")
	public Map<String, Map<Object, Long>> ocorrenciasPorDia(
			@PathParam("ids") String ids, @PathParam("ano") int ano,
			@PathParam("mes") int mes, @PathParam("dia") int dia) {
		return montaMapaDaSoma(ids, c -> {
			return service.ocorrenciasSomadas(c, ano, mes, dia);
		});
	}
	
	@GET
	@Path("{id}/datas-disponiveis")
	public Set<AnoMesesDiasDisponiveis> anosMesesDiasDisponiveis(@PathParam("id") long id){
		return service.anosMesesDiasDisponiveis(id);
		
	}

	private Map<String, Map<Object, Long>> montaMapaDaSoma(String ids,
			Function<Long, Map<Object, Long>> criaValor) {
		return formataIDCiclovias(ids).stream().collect(
				Collectors.toMap(CicloviaHelper::nome, criaValor));
	}

	private List<Long> formataIDCiclovias(String ids) {
		return Stream
				.of(ids.split(IDS_SEPARADOR))
				.map(Long::parseLong)
				.collect(Collectors.toList());
	}
}
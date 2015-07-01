package org.transparenciasjc.cicloviassp.resource;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.transparenciasjc.cicloviassp.model.Ciclovia;
import org.transparenciasjc.cicloviassp.service.CicloviaService;

/**
 * 
 * Cria os JSON que precisamos do banco
 * 
 * @author wsiqueir
 *
 */
@Path("ciclovia")
@Produces(MediaType.APPLICATION_JSON)
public class CicloviaResource {

	final static String IDS_SEPARADOR = ";";

	@Inject
	CicloviaService service;

	@GET
	public List<Ciclovia> ciclovias() {
		return service.buscarTodos();
	}

	@GET
	@Path("{ids}/ocorrencias/{ano}")
	public Map<String, Map<Object, Long>> ocorrenciasPorAno(
			@PathParam("ids") String ids, @PathParam("ano") int ano) {
		return montaMapaDaSoma(ids,  c -> {
			return service.ocorrenciasSomadas(c, ano);
		});
	}

	@GET
	@Path("{ids}/ocorrencias/{ano}/{mes}")
	public Map<String, Map<Object, Long>>  ocorrenciasPorMes(@PathParam("ids") String ids,
			@PathParam("ano") int ano, @PathParam("mes") int mes) {
		return montaMapaDaSoma(ids,  c -> {
			return service.ocorrenciasSomadas(c, ano, mes);
		});
	}

	@GET
	@Path("{ids}/ocorrencias/{ano}/{mes}/{dia}")
	public Map<String, Map<Object, Long>>  ocorrenciasPorDia(@PathParam("ids") String ids,
			@PathParam("ano") int ano, @PathParam("mes") int mes,
			@PathParam("dia") int dia) {
		return montaMapaDaSoma(ids,  c -> {
			return service.ocorrenciasSomadas(c, ano, mes, dia);
		});
	}

	private Map<String, Map<Object, Long>>  montaMapaDaSoma(String ids, Function<Ciclovia, Map<Object, Long>> criaValor) {
		return recuperaCiclovias(ids).stream().collect(Collectors.toMap(Ciclovia::getNome, criaValor));
	}
	
	private List<Ciclovia> recuperaCiclovias(String ids) {
		return Stream.of(ids.split(IDS_SEPARADOR))
				.map(Long::parseLong)
				.map(id -> {
					return service.porId(id).orElseThrow(() -> new WebApplicationException(Status.NOT_FOUND));
				})
				.collect(Collectors.toList());
	}
}
package org.transparenciasjc.cicloviassp.resource;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.transparenciasjc.cicloviassp.model.Ciclovia;

/**
 * 
 * Classe diretona no banco para criar o que precisamos no cliente para nossos
 * gráficos.
 * 
 * @author wsiqueir
 *
 */
@Path("ciclovia")
@Produces(MediaType.APPLICATION_JSON)
public class CicloviaResource {

	@Inject
	@PersistenceContext
	EntityManager em;

	@GET
	public List<Ciclovia> ciclovias() {
		return null;
	}

}

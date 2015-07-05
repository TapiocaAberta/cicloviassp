package org.transparenciasjc.cicloviassp.carga;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.transparenciasjc.cicloviassp.helpers.CargaHelper;
import org.transparenciasjc.cicloviassp.model.ArquivoProcessado;
import org.transparenciasjc.cicloviassp.service.CicloviasSPService;
import org.transparenciasjc.cicloviassp.service.RepositorioGitService;

/**
 * 
 * Objetivo dessa classe é trazer os arquivos novos e inserir eles no banco de
 * dados.
 * 
 * Após fazer um pull request, essa classe deverá ter acesso aos novos arquivos
 * trazidos do repositório remoto e os dados serem inseridos no banco
 * 
 * @author wsiqueir
 *
 */
@Singleton
@Startup
public class AtualizaBancoDados {

	@Inject
	CicloviasSPService cicloviaService;

	@Inject
	RepositorioGitService repoService;

	@Inject
	Logger log;

	/**
	 * 
	 * Irá atualizar os dados no banco de acordo com os arquivos que vieram do
	 * repositório. É executado quando subimos a aplicação, mas também todo dia
	 * as 1 da manhã pois os commits são as 00
	 * 
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	@Schedule(hour = "*/1")
	public void atualizaDados() throws Exception {
		List<String> arquivosProcessados = cicloviaService
				.arquivosProcessados();
		repoService
				.passaPorArquivosCSV((caminhoNoGit, caminho) -> {
					if (arquivosProcessados.indexOf(caminhoNoGit) == -1) {
						log.warning("Processando arquivo: " + caminhoNoGit);
						try {
							ArquivoProcessado arquivoProcessado = new ArquivoProcessado();
							arquivoProcessado.setArquivo(caminhoNoGit);
							arquivoProcessado.setDataProcessamento(new Date());
							arquivoProcessado.setQtdeLinhas(Files
									.lines(Paths.get(caminho)).skip(1).count());
							cicloviaService.salvar(arquivoProcessado);
							Files.lines(Paths.get(caminho))
									.skip(1)
									.map(CargaHelper::converteLinha)
									.peek(o -> o
											.setArquivoProcessado(arquivoProcessado))
									.forEach(cicloviaService::salvar);
							log.warning("Arquivo processado com sucesso");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
}
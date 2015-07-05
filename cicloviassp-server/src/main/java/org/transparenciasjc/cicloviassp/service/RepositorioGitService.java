package org.transparenciasjc.cicloviassp.service;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

@Stateless
public class RepositorioGitService {

	// Se estiver no openshift ele encontra o caminho
	private static String CAMINHO_REPO_LOCAL = System.getProperty(
			"OPENSHIFT_DATA_DIR", System.getProperty("CAMINHO_REPOSITORIO",
					"/opt/projects/DadosDaCiclovia_/"));

	private static final String DADOS_CICLOVIAS_URL = "git@github.com:LabProdam/DadosDaCiclovia.git";

	@Inject
	Logger log;
	
	public Repository pegarRepositorio() throws InvalidRemoteException,
			TransportException, GitAPIException, IOException {
		log.warning("Tentativa de criar ou acessar repositório");
		File repoLocal = new File(CAMINHO_REPO_LOCAL);
		if (repoLocal.exists()) {
			log.warning("Repositório já existe e será lido...");
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			builder.findGitDir(repoLocal);
			return builder.build();
		} else {
			log.warning("Repositório será clonado...");
			return Git.cloneRepository().setURI(DADOS_CICLOVIAS_URL)
					.setDirectory(repoLocal).call().getRepository();
		}
	}

	/**
	 * 
	 * Passa por cada arquivo do repositório e abre um stream para ele
	 * 
	 * @param acao
	 * @throws Exception
	 */
	public void passaPorArquivosCSV(BiConsumer<String, String> acao)
			throws Exception {
		Repository repository = pegarRepositorio();
		Git git = new Git(repository);
		log.warning("Trazendo dados do repositório");
		log.warning(git.pull().call().getFetchResult().getMessages().concat("\n"));
		log.warning(git.fetch().setCheckFetchedObjects(true).call().getMessages().concat("\n"));
		log.warning("Finalizado");
		ObjectId lastCommitId = repository.resolve(Constants.HEAD);
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(lastCommitId);
		RevTree tree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(false);
		while (treeWalk.next()) {
		    if (treeWalk.isSubtree()) {
		        treeWalk.enterSubtree();
		    } else {
		    	String path = treeWalk.getPathString();
		        if (path.endsWith("csv")) {
					acao.accept(path, CAMINHO_REPO_LOCAL + path);
				}
		    }

		}
	}
}
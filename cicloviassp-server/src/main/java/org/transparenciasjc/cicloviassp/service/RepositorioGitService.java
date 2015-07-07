package org.transparenciasjc.cicloviassp.service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.eclipse.jgit.api.CloneCommand;
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
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.jcraft.jsch.Session;

@Stateless
public class RepositorioGitService {

	// Se estiver no openshift ele encontra o caminho
	private static String CAMINHO_REPO_LOCAL = System.getProperty(
			"OPENSHIFT_DATA_DIR", System.getProperty("jboss.server.data.dir"))
			+ "/dados_cicloviasp_git/";

	private static final String DADOS_CICLOVIAS_URL = "https://github.com/LabProdam/DadosDaCiclovia.git";

	private String USR = System.getProperty("github.username");
	private String PSW = System.getProperty("github.password");	
	
	@Inject
	Logger log;

	public Git pegarRepositorio() throws InvalidRemoteException,
			TransportException, GitAPIException, IOException {
		
		// necessário para rodar no openshift, onde não consegue chegar o host
		SshSessionFactory.setInstance(new JschConfigSessionFactory() {
			@Override
			protected void configure(Host arg0, Session session) {
				session.setConfig("StrictHostKeyChecking", "no");
			}
		}); 		
		File repoLocal = new File(CAMINHO_REPO_LOCAL);
		Repository r;
		log.warning("Tentativa de criar ou acessar repositório");
		if (repoLocal.exists()) {
			log.warning("Repositório já existe e será lido...");
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			builder.findGitDir(repoLocal);
			r = builder.build();
		} else {
			log.warning("Repositório será clonado...");
			CloneCommand cloneCommand  = Git.cloneRepository().setURI(DADOS_CICLOVIAS_URL);
			if(!Objects.isNull(USR) && !Objects.isNull(PSW)){
				log.warning("Usando credenciais");
				cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(
						USR, PSW));
			}
			r = cloneCommand.setDirectory(repoLocal).call().getRepository();
		}
		return new Git(r);
	}

	/**
	 * 
	 * Passa por cada arquivo do repositório após atualizar o mesmo
	 * 
	 * @param acao
	 * @throws Exception
	 */
	public void passaPorArquivosCSV(BiConsumer<String, String> acao)
			throws Exception {
		Git git = pegarRepositorio();
		atualizaRepositorio(git);
		ObjectId lastCommitId = git.getRepository().resolve(Constants.HEAD);
		RevWalk revWalk = new RevWalk(git.getRepository());
		RevCommit commit = revWalk.parseCommit(lastCommitId);
		RevTree tree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(git.getRepository());
		treeWalk.addTree(tree);
		treeWalk.setRecursive(false);
		while (treeWalk.next()) {
			if (treeWalk.isSubtree()) {
				treeWalk.enterSubtree();
			} else {
				String path = treeWalk.getPathString();
				if (path.endsWith("csv")) {
					if(CAMINHO_REPO_LOCAL.endsWith("/")) {
						CAMINHO_REPO_LOCAL += "/";
					}
					acao.accept(path, CAMINHO_REPO_LOCAL + path);
				}
			}

		}
	}

	private void atualizaRepositorio(Git git) throws Exception {
		log.warning("Trazendo dados do repositório");
		log.warning(git.pull().call().getFetchResult().getMessages()
				.concat("\n"));
		log.warning(git.fetch().setCheckFetchedObjects(true).call()
				.getMessages().concat("\n"));
		log.warning("Finalizado");
	}
}
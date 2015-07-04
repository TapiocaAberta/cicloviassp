package org.transparenciasjc.cicloviassp.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Contém informação dos arquivos CSVs que já foram processados
 * 
 * @author wsiqueir
 *
 */
@Entity
@Table(name = "arquivo_processado")
public class ArquivoProcessado {

	@Id
	@GeneratedValue
	@Column(name="aop_id")
	private long id;
	@Column(name="aop_data_processamento")
	private Date dataProcessamento;
	@Column(name="aop_arquivo", unique=true)
	private String arquivo;
	@Column(name="aop_qtde_linhas")
	private long qtdeLinhas;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDataProcessamento() {
		return dataProcessamento;
	}

	public void setDataProcessamento(Date dataProcessamento) {
		this.dataProcessamento = dataProcessamento;
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public long getQtdeLinhas() {
		return qtdeLinhas;
	}

	public void setQtdeLinhas(long qtdeLinhas) {
		this.qtdeLinhas = qtdeLinhas;
	}
	
}

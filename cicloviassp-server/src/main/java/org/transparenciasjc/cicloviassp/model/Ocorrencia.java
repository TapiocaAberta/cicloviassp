package org.transparenciasjc.cicloviassp.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

@Entity
// comentando por causa desse issue:
// https://github.com/LabProdam/DadosDaCiclovia/issues/1
// @Table(name = "ocorrencia", uniqueConstraints = @UniqueConstraint(columnNames
// = {
// "ciclovia_cic_id", "ocr_horario" }))
@Table(name = "ocorrencia")
@NamedQueries({
		@NamedQuery(name = "SomaOcorrenciaPorCicloviaAno", query = "SELECT MONTH(o.horario), COUNT(o) FROM Ocorrencia o WHERE o.ciclovia = :ciclovia AND YEAR(o.horario) = :ano GROUP BY MONTH(o.horario)", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
		@NamedQuery(name = "SomaOcorrenciaPorCicloviaAnoMes", query = "SELECT DAY(o.horario), COUNT(o) FROM Ocorrencia o WHERE o.ciclovia = :ciclovia AND YEAR(o.horario) = :ano AND MONTH(o.horario) = :mes GROUP BY DAY(o.horario)", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
		@NamedQuery(name = "SomaOcorrenciaPorCicloviaAnoMesDia", query = "SELECT HOUR(o.horario), COUNT(o) FROM Ocorrencia o WHERE o.ciclovia = :ciclovia AND YEAR(o.horario) = :ano AND MONTH(o.horario) = :mes AND DAY(o.horario) = :dia GROUP BY HOUR(o.horario)", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
		@NamedQuery(name = "AnosMesesDisponiveis", query = "SELECT DISTINCT YEAR(o.horario), MONTH(o.horario), DAY(o.horario) FROM Ocorrencia o WHERE o.ciclovia = :ciclovia", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })		
public class Ocorrencia {

	@Id
	@GeneratedValue
	@Column(name = "ocr_id")
	private long id;

	@Column(name = "ocr_horario")
	private Date horario;

	@Column(name = "ocr_dir")
	private int dir;

	@Column(name = "ocr_ciclovia_id")
	long ciclovia;

	@ManyToOne
	@JoinColumn(name = "arquivo_processado_aop_id")
	ArquivoProcessado arquivoProcessado;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getHorario() {
		return horario;
	}

	public void setHorario(Date horario) {
		this.horario = horario;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public long getCiclovia() {
		return ciclovia;
	}

	public void setCiclovia(long ciclovia) {
		this.ciclovia = ciclovia;
	}

	public ArquivoProcessado getArquivoProcessado() {
		return arquivoProcessado;
	}

	public void setArquivoProcessado(ArquivoProcessado arquivoProcessado) {
		this.arquivoProcessado = arquivoProcessado;
	}

}
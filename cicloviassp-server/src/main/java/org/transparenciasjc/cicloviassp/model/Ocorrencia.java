package org.transparenciasjc.cicloviassp.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ocorrencia")
public class Ocorrencia {

	@Id
	@GeneratedValue
	@Column(name = "ocr_id")
	private long id;

	@Column(name = "ocr_horario")
	private Date horario;

	@Column(name = "ocr_dir")
	private int dir;

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
}
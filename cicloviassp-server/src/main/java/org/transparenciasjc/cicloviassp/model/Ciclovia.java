package org.transparenciasjc.cicloviassp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="ciclovia")
public class Ciclovia {
	
	@Id
	@GeneratedValue
	@Column(name="cic_id")
	private long id;
	
	@Column(name="cic_nome")
	private String nome;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
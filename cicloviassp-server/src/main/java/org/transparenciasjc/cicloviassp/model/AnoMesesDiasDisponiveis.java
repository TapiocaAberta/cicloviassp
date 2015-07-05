package org.transparenciasjc.cicloviassp.model;

import java.util.List;
import java.util.Map;

/**
 * 
 * Informação de anos, meses e dias disponíves no banco de dados
 * 
 * @author wsiqueir
 *
 */
public class AnoMesesDiasDisponiveis {

	int ano;
	Map<Integer, List<Integer>> mesesDias;
	
	public AnoMesesDiasDisponiveis() {
		super();
	}
	public AnoMesesDiasDisponiveis(int ano) {
		super();
		this.ano = ano;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	public Map<Integer, List<Integer>> getMesesDias() {
		return mesesDias;
	}
	public void setMesesDias(Map<Integer, List<Integer>> mesesDias) {
		this.mesesDias = mesesDias;
	}
	
	
}

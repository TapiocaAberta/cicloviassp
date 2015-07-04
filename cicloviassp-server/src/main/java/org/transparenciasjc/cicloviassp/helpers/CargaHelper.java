package org.transparenciasjc.cicloviassp.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.transparenciasjc.cicloviassp.model.Ocorrencia;

public class CargaHelper {
	
	private static DateFormat formataData;
	
	static {
		// 2015-06-24 05:15:21
		formataData = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}
	
	// DB_ID,PLACE_ID,DIRECTION,EVENT_TIME,WIDTH,HEIGHT,PROJECTION_DISTANCE,SPEED,FILE_NAME
	public static Ocorrencia converteLinha(String linha) {
		Ocorrencia ocorrencia = new Ocorrencia();
		String campos[] = linha.split("\\,");
		Long cicloviaId = Long.valueOf(campos[1]);
		int direcao = Integer.valueOf(campos[2]);
		String dataStr = campos[3].replaceAll("\\\"", "");
		ocorrencia.setCiclovia(cicloviaId);
		ocorrencia.setDir(direcao);
		try {
			ocorrencia.setHorario(formataData.parse(dataStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ocorrencia;
	}

}
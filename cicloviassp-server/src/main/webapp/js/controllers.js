/**
 * O código principal da aplicação em Angular
 */
var cicloviasSPApp = angular.module('cicloviasSPApp', []);

var ANO = 'Ano';
var MES = 'Mês';
var DIA = "Dia"
var MESES = [ "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
		"Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" ];

cicloviasSPApp.controller('CicloviasSPController', function($scope, $http) {
	/** ***** CONSTANTES ****** */
	$scope.ANO = ANO;
	$scope.MES = MES;
	$scope.DIA = DIA;

	/** ***** INICIALIZAÇÔES ****** */
	$http.get("rest/ciclovia").success(function(dados) {
		$scope.ciclovias = dados;
		$scope.cicloviaSelecionada = dados[0];
		$scope.carregaDatasDisponiveis();
	});

	$scope.carregaDatasDisponiveis = function() {
		var url = "rest/ciclovia/" + $scope.cicloviaSelecionada.key
				+ "/datas-disponiveis"
		$http.get(url).success(function(dados) {
			$scope.datasDisponiveis = dados;
			$scope.anoSelecionado = $scope.datasDisponiveis[0];
			$scope.carregaMeses();
			$scope.atualizar();
		});
	};
	/** ***** INICIALIZAÇÔES ESTÁTICAS ****** */
	$scope.agregacaoSelecionada = $scope.DIA;
	$scope.comparando = false;
	var busca = [];

	/** ***** LISTENERS ****** */
	$scope.carregaMeses = function() {
		var meses = []
		for (m in $scope.anoSelecionado.mesesDias) {
			meses.push({
				nome : MESES[m - 1],
				id : m
			});
		}
		$scope.meses = meses;
		$scope.mesSelecionado = $scope.meses[0];
		$scope.carregaDias();
	};

	$scope.carregaDias = function() {
		var i = $scope.mesSelecionado.id;
		$scope.dias = $scope.anoSelecionado.mesesDias[i];
		$scope.dias.sort(function(a, b) {
			return a - b
		});
		$scope.diaSelecionado = $scope.dias[0];
	}
	$scope.ehAgregacaoSelecionada = function(a) {
		return a == $scope.agregacaoSelecionada;
	}
	$scope.selecionaAgregacao = function(a) {
		busca = [];
		$scope.agregacaoSelecionada = a;
	}
	$scope.habilitaComparacao = function() {
		$scope.comparando = !$scope.comparando;
	}
	$scope.limpar = function() {
		busca = [];
		montaGrafico(busca, null);
	}
	
	$scope.atualizar = function() {
		var nomeSerie = $scope.cicloviaSelecionada.value + " (";		
		var url = "rest/ciclovia/" + $scope.cicloviaSelecionada.key
				+ "/ocorrencias/";
		var agregacao = $scope.agregacaoSelecionada;
		var ano = $scope.anoSelecionado.ano;
		var mes = $scope.mesSelecionado.id;
		var dia = $scope.diaSelecionado;
		if (agregacao == $scope.ANO) {
			url += ano;
			nomeSerie += ano;
		}
		if (agregacao == $scope.MES) {
			url += ano + "/" + mes;
			nomeSerie += MESES[mes - 1] + "/ " + ano;
		}
		if (agregacao == $scope.DIA) {
			url += ano + "/" + mes + "/" + dia;
			nomeSerie += dia + "/" + MESES[mes - 1] + "/ " + ano;
		}
		nomeSerie = nomeSerie + ")";
		// se já existir uma série com esse valor a gente não adiciona e nem faz a busca
		for(b in busca) {
			if(busca[b].nome == nomeSerie) {
				return;
			}
		}
		$scope.carregando = true;
		$http.get(url).success(function(dados) {
			$scope.carregando = false;
			if (!$scope.comparando) {
				busca = [];
			}
			busca.push({
				nome : nomeSerie,
				dados : dados
			});
			montaGrafico(busca, $scope.agregacaoSelecionada);
		});
	};

	/** ***** INICIALIZAÇÔES DE ELEMENTOS ****** */
	$('#lblCarregar').each(function() {
		var elem = $(this);
		setInterval(function() {
			elem.fadeToggle(600);
		}, 400);
	});
});

/**
 * Monta o gráfico para os dados possivelmente de diversas ciclovias que virão
 * 
 * @param dados
 */
function montaGrafico(busca, agregacao) {
	var dadosGrafico = [];
	var categorias = [];
	var series = [];
	// montando as séries em duas etapas
	// 1) pega todas as categorias disponíveis de todos os dados
	for (res in busca) {
		for (s in busca[res].dados) {
			$.each(busca[res].dados[s], function(i, v) {
				if (categorias.indexOf(i) == -1)
					categorias.push(i);
			});
		}
	}
	categorias.sort(function(a, b) {
		return a - b;
	});
	// 2) agora pra cada categoria disponível monta a série de dados
	for (d in busca) {
		for (s in busca[d].dados) {
			var serie = {};
			serie.name = busca[d].nome;
			serie.data = [];
			for (c in categorias) {
				var cat = categorias[c];
				var valor = busca[d].dados[s][cat];
				if (valor) {
					serie.data.push(valor);
				} else {
					serie.data.push(0);
				}
			}
			series.push(serie);
		}
	}
	// formata a categoria
	var categoriasFormatada = [];
	for (c in categorias) {
		var i = categorias[c];
		if (agregacao == ANO) {
			categoriasFormatada.push(MESES[i - 1]);
		} else {
			categoriasFormatada.push(i);
		}
	}
	$("#grafico").highcharts({
		title : {
			text : ""
		},
		yAxis : {
			title : {
				text : "Contagem ciclistas"
			},
			min : 0
		},
		xAxis : {
			categories : categoriasFormatada
		},
		plotOptions : {
			series : {
				allowPointSelect : true
			}
		},
		series : series
	});
}
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
	$scope.agregacaoSelecionada = $scope.DIA;
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
		$scope.agregacaoSelecionada = a;
	}

	$scope.atualizar = function() {
		$scope.carregando = true;
		var url = "rest/ciclovia/" + $scope.cicloviaSelecionada.key
				+ "/ocorrencias/";
		var agregacao = $scope.agregacaoSelecionada;
		var ano = $scope.anoSelecionado.ano;
		var mes = $scope.mesSelecionado.id;
		var dia = $scope.diaSelecionado;
		if (agregacao == $scope.ANO) {
			url += ano;
		}
		if (agregacao == $scope.MES) {
			url += ano + "/" + mes;
		}
		if (agregacao == $scope.DIA) {
			url += +ano + "/" + mes + "/" + dia;
		}
		$http.get(url).success(function(dados) {
			$scope.carregando = false;
			montaGrafico(dados, $scope.agregacaoSelecionada);
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
function montaGrafico(dados, agregacao) {
	var dadosGrafico = [];
	var categorias = [];
	var series = [];
	// montando as séries em duas etapas
	// 1) pega todas as categorias disponíveis de todos os dados
	for (s in dados) {
		$.each(dados[s], function(i, v) {
			if (categorias.indexOf(i) == -1)
				categorias.push(i);
		});
	}
	categorias.sort(function(a, b) {
		return a - b;
	});
	// 2) agora pra cada categoria disponível monta a série de dados
	for (s in dados) {
		var serie = {};
		serie.name = s;
		serie.data = []
		for (c in categorias) {
			var cat = categorias[c];
			if (dados[s][cat]) {
				serie.data.push(dados[s][cat]);
			} else {
				serie.data.push(0);
			}
		}
		series.push(serie);
	}
	// formata a categoria
	var categoriasFormatada = [];
	for(c in categorias) {
		var i = categorias[c];
		if(agregacao == ANO) {			
			categoriasFormatada.push(MESES[i - 1]);			
		} else{
			categoriasFormatada.push(i);	
		}
	}	
	$("#grafico").highcharts({
		title : {
			text : ""
		},
		yAxis : {			
			title: {
				text: "Contagem ciclistas"
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
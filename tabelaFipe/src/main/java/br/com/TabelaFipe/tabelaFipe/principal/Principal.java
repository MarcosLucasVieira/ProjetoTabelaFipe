package br.com.TabelaFipe.tabelaFipe.principal;

import br.com.TabelaFipe.tabelaFipe.model.Dados;
import br.com.TabelaFipe.tabelaFipe.model.Modelos;
import br.com.TabelaFipe.tabelaFipe.model.Veiculos;
import br.com.TabelaFipe.tabelaFipe.service.ConsumoApi;
import br.com.TabelaFipe.tabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitor = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu(){
        var menu = """
                **** OPÇÕES ****
                * CARRO
                * MOTO 
                * CAMINHÃO 
                
                
                DIGITE UMA DAS OPÇÕES PARA CONSULTA:
                """;

        System.out.println(menu);
        var opcao = leitor.nextLine();
        String endereco;

        if(opcao.toLowerCase().contains("carr")){
            endereco = URL_BASE + "carros/marcas";
        }else if (opcao.toLowerCase().contains("mot")){
            endereco = URL_BASE + "motos/marcas";
        }else {
            endereco = URL_BASE +"caminhoes/marcas";
        }
        var json = consumo.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta: ");
        var codigoMarca = leitor.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("Modelos dessa marca \n");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println( "\nDigite o nome do carro a ser buscado:");
        var nomeVeiculo = leitor.nextLine();
        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toUpperCase()))
                .collect(Collectors.toList());

        System.out.println("\nmodelos filtrados:");
        modelosFiltrados.forEach(System.out::println);

        System.out.println( "Digite por favor o código do modelo para buscar os valores de avaliação:");
        var codigoModelo = leitor.nextLine();

        endereco = endereco +"/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculos> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size() ; i++) {
            var endencorAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(endencorAnos);
            Veiculos veiculo =  conversor.obterDados(json, Veiculos.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veiculos filtrados com a avaliações por ano: ");
        veiculos.forEach(System.out::println);


    }
}

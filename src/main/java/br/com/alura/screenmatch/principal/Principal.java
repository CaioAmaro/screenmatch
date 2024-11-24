package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodios;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=afd46234";


    public void exibeMenu(){
        System.out.print("Digite o nome da serie para busca: ");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for(int i=1; i<= dados.totalTemporadas(); i++){
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+")+ "&Season="+ i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        temporadas.forEach(System.out::println);

        for(int i=0; i < dados.totalTemporadas(); i++){
            List<DadosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();
            for(int j=0; j< episodiosTemporadas.size(); j++){
                System.out.println(episodiosTemporadas.get(j).titulo());
            }
        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(n -> n.episodios().stream())
                .collect(Collectors.toList());

//        System.out.println("\n Top 10");
//        dadosEpisodios.stream()
//                .filter(n -> !n.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("(Filtro N/A) - " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("(Organizando em Decrescente) - " + e))
//                .limit(10)
//                .peek(e -> System.out.println("(Limitando em 10) - " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("(transformando em capslock) - " + e))
//                .forEach(System.out::println);

        List<Episodios> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodios(t.temporada(), e)))
                .collect(Collectors.toList());

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodios::getAvaliacao));

        System.out.println("Quantidade: " + est.getCount());
        System.out.println("Média: " + est.getAverage());
        System.out.println("Maior Avaliação: " + est.getMax());
        System.out.println("Menor Avaliação: " + est.getMin());


//        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.groupingBy(Episodios::getTemporada,
//                        Collectors.averagingDouble(Episodios::getAvaliacao
//                )));
//
//        avaliacoesPorTemporada.forEach( (chave, valor) -> System.out.println(chave +" - "+ valor));

//        episodios.forEach(System.out::println);

//        System.out.println("Digite o trecho do titulo pesquisado: ");
//        var trecho = leitura.nextLine();
//
//        Optional<Episodios> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trecho.toUpperCase()))
//                .peek(e -> System.out.println("Procurando filme contendo: \" " + trecho + " \" "+ e))
//                .findFirst();
//
//        if(episodioBuscado.isPresent()){
//            System.out.println("Episodio Encontrado!");
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//        }else{
//            System.out.println("Episodio Não encontrado!");
//        }

//        System.out.println("Você quer ver os episodios apartir de qual ano? ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano,1, 1);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(f -> System.out.println(
//                        "Temporada: " + f.getTemporada() +
//                                " Espisódio: " + f.getNumeroEpisodio() +
//                                " Data de Lançamento: " + f.getDataLancamento().format(formatter)
//                ));

//        List<String> nomes = Arrays.asList("caio", "brunna", "geizy","brunno","rafael","marcos");
//
//        nomes.stream()
//                .sorted()
//                .limit(3)
//                .filter(n -> n.startsWith("b"))
//                .map(String::toUpperCase)
//                .toList()
//                .forEach(System.out::println);

        //teste
    }
}

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

        System.out.println("\n Top 5");
        dadosEpisodios.stream()
                .filter(n -> !n.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodios> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodios(t.temporada(), e)))
                .collect(Collectors.toList());

        System.out.println(episodios);

        System.out.println("Você quer ver os episodios apartir de qual ano? ");
        var ano = leitura.nextInt();
        leitura.nextLine();

        LocalDate dataBusca = LocalDate.of(ano,1, 1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(f -> System.out.println(
                        "Temporada: " + f.getTemporada() +
                                " Espisódio: " + f.getNumeroEpisodio() +
                                " Data de Lançamento: " + f.getDataLancamento().format(formatter)
                ));

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

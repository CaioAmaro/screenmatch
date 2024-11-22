package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.model.DadosSerie;
import org.springframework.cglib.core.ClassesKey;

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);
}

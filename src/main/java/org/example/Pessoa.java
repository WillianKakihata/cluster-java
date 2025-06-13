package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pessoa {
    String nome;
    int idade;
    int faltas;
    String categoria; // Rótulo da classe

    public Pessoa(String nome, int idade, int faltas, String categoria) {
        this.nome = nome;
        this.idade = idade;
        this.faltas = faltas;
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Nome: " + nome +
                " | Idade: " + idade +
                " | Faltas: " + faltas +
                " | Categoria: " + categoria;
    }

    public static class Cluster {
        double centroid;
        List<Pessoa> pessoas = new ArrayList<>();
        Map<String, List<Pessoa>> subgrupos = new HashMap<>();
    }
}

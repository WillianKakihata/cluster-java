package org.example;

public class Pessoa {
    String nome;
    int idade;
    int faltas;

    public Pessoa(String nome, int idade, int faltas) {
        this.nome = nome;
        this.idade = idade;
        this.faltas = faltas;

    }

    @Override
    public String toString() {
        return nome + " | Idade: " + idade + " | Faltas: " + faltas;
    }
}
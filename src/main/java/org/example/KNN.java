package org.example;

import java.util.*;

public class KNN {

    public static String supervisionado(Pessoa alvo, Pessoa[] base, int k) {
        List<Pessoa> lista = new ArrayList<>(Arrays.asList(base));

        lista.removeIf(p -> p == alvo);

        lista.sort(Comparator.comparingDouble(p -> distancia(alvo, p)));

        Map<String, Integer> contagemCategorias = new HashMap<>();
        for (int i = 0; i < Math.min(k, lista.size()); i++) {
            String categoriaVizinho = lista.get(i).categoria;
            contagemCategorias.put(categoriaVizinho, contagemCategorias.getOrDefault(categoriaVizinho, 0) + 1);
        }

        return contagemCategorias.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Desconhecido");
    }

    private static double distancia(Pessoa p1, Pessoa p2) {
        return Math.abs(p1.faltas - p2.faltas);
    }
}

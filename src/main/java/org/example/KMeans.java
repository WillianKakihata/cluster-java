package org.example;

import java.util.*;

public class KMeans {
    private int k;
    private static double[][] centroids;
    private List<List<Integer>> clustersIndices;  
    private static final double THRESHOLD = 1e-4;

    public KMeans(int k) {
        this.k = k;
    }

    private void initializeCentroids(double[][] data) {
        Random rand = new Random();
        centroids = new double[k][1];
        for (int i = 0; i < k; i++) {
            int randomIndex = rand.nextInt(data.length);
            centroids[i] = Arrays.copyOf(data[randomIndex], 1);
        }
    }

    private void assignClusters(double[][] data) {
        clustersIndices = new ArrayList<>();
        for (int i = 0; i < centroids.length; i++) {
            clustersIndices.add(new ArrayList<>());
        }
        for (int i = 0; i < data.length; i++) {
            int nearestCentroid = getNearestCentroid(data[i]);
            clustersIndices.get(nearestCentroid).add(i);
        }
    }

    private int getNearestCentroid(double[] point) {
        int nearestIndex = 0;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < centroids.length; i++) {
            double distance = euclideanDistance(point, centroids[i]);
            if (distance < minDistance) {
                minDistance = distance;
                nearestIndex = i;
            }
        }
        return nearestIndex;
    }

    private double euclideanDistance(double[] point1, double[] point2) {
        return Math.abs(point1[0] - point2[0]);
    }

    private boolean updateCentroids(double[][] data) {
        boolean changed = false;
        for (int i = 0; i < clustersIndices.size(); i++) {
            double[] newCentroid = new double[1];
            List<Integer> clusterPointsIndices = clustersIndices.get(i);
            if (!clusterPointsIndices.isEmpty()) {
                for (int idx : clusterPointsIndices) {
                    newCentroid[0] += data[idx][0];
                }
                newCentroid[0] /= clusterPointsIndices.size();
            } else {
                newCentroid = centroids[i];
            }

            if (Math.abs(centroids[i][0] - newCentroid[0]) > THRESHOLD) {
                changed = true;
                centroids[i] = newCentroid;
            }
        }
        return changed;
    }

    public void fit(double[][] data) {
        initializeCentroids(data);
        boolean centroidsChanged;
        int iterations = 0;
        do {
            assignClusters(data);
            centroidsChanged = updateCentroids(data);
            iterations++;
        } while (centroidsChanged && iterations < 100);
        System.out.println("Convergiu em " + iterations + " iterações");
    }

    public void printClusters(Pessoa[] pessoas, double[][] data) {
        System.out.println("-------------------------- CLUSTERS ----------------------------");
        for (int i = 0; i < clustersIndices.size(); i++) {
            System.out.println("Cluster " + (i + 1) + " centroid: " + Arrays.toString(centroids[i]));
            System.out.println("Pessoas:");
            for (int idx : clustersIndices.get(i)) {
                Pessoa p = pessoas[idx];
                System.out.println("  " + p.nome + " | Idade: " + p.idade + " | Faltas: " + p.faltas);
            }
            System.out.println();
        }
    }

    public static int converterStringParaNumero(String valor) {
        return valor.hashCode();
    }

    private double[][] removerElemento(double[][] array, int index) {
        double[][] novo = new double[array.length - 1][];
        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != index) {
                novo[j++] = array[i];
            }
        }
        return novo;
    }

    public void refinarClusters(double[][] data) {
        final double DISTANCIA_LIMITE = 20.0;
        final int MAX_ITERACOES = 20;
        int iteracao = 0;
        boolean houveMudanca;

        do {
            houveMudanca = false;
            List<Integer> pontosADistancia = new ArrayList<>();

            for (int i = 0; i < clustersIndices.size(); i++) {
                double[] centroide = centroids[i];
                List<Integer> cluster = clustersIndices.get(i);

                Iterator<Integer> it = cluster.iterator();
                while (it.hasNext()) {
                    int idx = it.next();
                    double distancia = Math.abs(data[idx][0] - centroide[0]);

                    if (distancia > DISTANCIA_LIMITE) {
                        pontosADistancia.add(idx);
                        it.remove();
                        houveMudanca = true;
                    }
                }
            }

            if (!pontosADistancia.isEmpty()) {
                int novoIndice = clustersIndices.size();
                clustersIndices.add(new ArrayList<>(pontosADistancia));
                centroids = Arrays.copyOf(centroids, centroids.length + 1);
                centroids[novoIndice] = calcularNovoCentroide(data, pontosADistancia);
                pontosADistancia.clear();
            }

            for (int i = clustersIndices.size() - 1; i >= 0; i--) {
                if (clustersIndices.get(i).isEmpty()) {
                    clustersIndices.remove(i);
                    centroids = removerElemento(centroids, i);
                }
            }

            updateCentroids(data);
            iteracao++;

            System.out.println("Iteração de refinamento: " + iteracao + " | Total de clusters: " + clustersIndices.size());

        } while (houveMudanca && iteracao < MAX_ITERACOES);

        if (iteracao == MAX_ITERACOES) {
            System.out.println("Atingido o limite máximo de iterações no refinamento.");
        }
    }

    private double[] calcularNovoCentroide(double[][] data, List<Integer> pontos) {
        double[] centroide = new double[1];
        for (int idx : pontos) {
            centroide[0] += data[idx][0];
        }
        centroide[0] /= pontos.size();
        return centroide;
    }

    public void imprimirClustersComKNN(List<Pessoa.Cluster> clusters, int k) {
        System.out.println("-------- Impressão dos Clusters com Classificação Supervisionada (KNN) --------");

        for (int i = 0; i < clusters.size(); i++) {
            Pessoa.Cluster cluster = clusters.get(i);
            System.out.println("Cluster " + (i + 1) + ":");
            System.out.println("Centroide: " + cluster.centroid);
            System.out.println("Pessoas:");

            for (Pessoa p : cluster.pessoas) {
                String categoriaPredita = KNN.supervisionado(p, cluster.pessoas.toArray(new Pessoa[0]), k);
                System.out.println("   Nome: " + p.nome +
                        " | Categoria real: " + p.categoria +
                        " | Categoria predita (KNN): " + categoriaPredita
                        + "| Total faltas: " + p.faltas);
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        Random gerador = new Random();
        int tamanho = gerador.nextInt(30, 50);

        Pessoa[] pessoas = new Pessoa[tamanho];

        String[] nomes = {
                "Ana", "Bruno", "Carlos", "Daniela", "Eduardo", "Fernanda", "Gabriel", "Helena",
                "Igor", "Juliana", "Kleber", "Larissa", "Marcelo", "Natália", "Otávio", "Patrícia",
                "Rafael", "Sabrina", "Thiago", "Vanessa", "Lucas", "Mário", "Carla", "Renato",
                "Isabela", "Júlio", "Mariana", "André", "Roberta", "Brinquedo", "Victor", "Tatiane",
                "Luciana", "Fernando", "Bruna", "Vera", "Matheus", "Tatiane", "Fábio", "Alice",
                "Rodrigo", "Beatriz", "Giovana", "Samuel", "Pedro", "Cláudia", "Simone", "Rita",
                "João", "Paula", "Felipe", "Gustavo", "Marta", "Priscila", "Sérgio", "Elenice",
                "Juliana", "Antonio", "Bárbara", "Vanessa", "Marcelo", "Diana", "Lúcia", "Carlos",
                "Rogério", "Adriana", "Joana", "Vitor", "Robson", "Luana", "Simone", "Maurício",
                "Eliane", "Kátia", "Tânia", "Walter", "Renata", "Aline", "Ricardo", "Vanessa",
                "Irene", "Walter", "Denise", "Denilson", "Raquel", "Guilherme", "Sílvia", "Maurício",
                "Caroline", "Aline", "Alberto", "Leandro", "Fátima", "Camila", "Michele", "Felipe",
                "Cristiano", "Verônica", "Marcio", "Lúcia", "Lúcio", "Lourdes", "Tadeu", "Elen",
                "José", "Sérgio", "Ricardo", "Fernanda", "Andréa", "Gabriela", "Igor", "Marcelo",
                "Larissa", "Sandra", "Diana", "Juliano", "Otávio", "Daniel", "Vânia", "Douglas"
        };

        String[] categorias = {"Aprovado", "Reprovado"};

        for (int i = 0; i < tamanho; i++) {
            int idade = gerador.nextInt(18, 60);
            int faltas = gerador.nextInt(0, 50);
            String nomeAleatorio = nomes[gerador.nextInt(nomes.length)];
            //String categoriaAleatoria = categorias[gerador.nextInt(categorias.length)];

            String categoria = (faltas >= 25) ? "Reprovado" : "Aprovado";

            pessoas[i] = new Pessoa(nomeAleatorio, idade, faltas, categoria);
        }

        double[][] data = new double[pessoas.length][1];
        for (int i = 0; i < pessoas.length; i++) {
            data[i][0] = pessoas[i].faltas;
        }

        KMeans kMeans = new KMeans(2);
        System.out.println("-------- Clusterização inicial (KMeans) --------");
        kMeans.fit(data);
        kMeans.printClusters(pessoas, data);

        System.out.println("-------- Refinamento com KMeans --------");
        kMeans.refinarClusters(data);
        kMeans.printClusters(pessoas, data);
        System.out.println("-------- Classificação Supervisionada (KNN) --------");
        int k = 3;
        for (Pessoa p : pessoas) {
            String categoriaPredita = KNN.supervisionado(p, pessoas, k);
            System.out.println("Pessoa: " + p.nome + " | Categoria real: " + p.categoria + " | Categoria predita (KNN): " + categoriaPredita);
        }

        List<Pessoa.Cluster> clustersComPessoas = new ArrayList<>();

        for (int i = 0; i < kMeans.clustersIndices.size(); i++) {
            Pessoa.Cluster c = new Pessoa.Cluster();
            c.centroid = kMeans.centroids[i][0];
            List<Integer> pontosIndices = kMeans.clustersIndices.get(i);

            for (int idx : pontosIndices) {
                c.pessoas.add(pessoas[idx]);
            }

            clustersComPessoas.add(c);
        }

        kMeans.imprimirClustersComKNN(clustersComPessoas, k);
    }

}

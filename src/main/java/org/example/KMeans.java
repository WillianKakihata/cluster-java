package org.example;

import java.util.*;

public class KMeans {
    private int k;
    private double[][] centroids;
    private List<List<double[]>> clusters;
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

    public List<List<double[]>> getClusters() {
        return clusters;
    }

    private void assignClusters(double[][] data) {
        clusters = new ArrayList<>();
        for (int i = 0; i < centroids.length; i++) {
            clusters.add(new ArrayList<>());
        }
        for (double[] point : data) {
            int nearestCentroid = getNearestCentroid(point);
            clusters.get(nearestCentroid).add(point);
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

    private boolean updateCentroids() {
        boolean changed = false;
        for (int i = 0; i < clusters.size(); i++) {
            double[] newCentroid = new double[1];
            List<double[]> clusterPoints = clusters.get(i);
            if (!clusterPoints.isEmpty()) {
                for (double[] point : clusterPoints) {
                    newCentroid[0] += point[0];
                }
                newCentroid[0] /= clusterPoints.size();
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
            centroidsChanged = updateCentroids();
            iterations++;
        } while (centroidsChanged && iterations < 100);
        System.out.println("Convergiu em " + iterations + " itera\u00e7\u00f5es");
    }

    public void printClusters(Pessoa[] pessoas, double[][] data) {
        System.out.println("-------------------------- CLUSTERS ----------------------------");
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println("Cluster " + (i + 1) + " centroid: " + Arrays.toString(centroids[i]));
            System.out.println("Pessoas:");
            for (double[] point : clusters.get(i)) {
                for (int j = 0; j < data.length; j++) {
                    if (Arrays.equals(data[j], point)) {
                        Pessoa p = pessoas[j];
                        System.out.println("  " + p.nome + " | Idade: " + p.idade + " | Faltas: " + p.faltas);
                        break;
                    }
                }
            }
            System.out.println();
        }
    }

    public void refinarClusters() {
        boolean houveMudanca;
        do {
            houveMudanca = false;
            List<double[]> pontosADistancia = new ArrayList<>();

            for (int i = 0; i < clusters.size(); i++) {
                double[] centroide = centroids[i];
                List<double[]> cluster = clusters.get(i);

                Iterator<double[]> it = cluster.iterator();
                while (it.hasNext()) {
                    double[] ponto = it.next();
                    double distancia = Math.abs(ponto[0] - centroide[0]);

                    if (distancia > 20) {
                        pontosADistancia.add(ponto);
                        it.remove();
                        houveMudanca = true;
                    }
                }
            }

            if (!pontosADistancia.isEmpty()) {
                int novoIndice = clusters.size();
                clusters.add(new ArrayList<>(pontosADistancia));
                centroids = Arrays.copyOf(centroids, centroids.length + 1);
                centroids[novoIndice] = calcularNovoCentroide(pontosADistancia);
                pontosADistancia.clear();
            }

            updateCentroids();

        } while (houveMudanca);
    }

    private double[] calcularNovoCentroide(List<double[]> pontos) {
        double[] centroide = new double[1];
        for (double[] ponto : pontos) {
            centroide[0] += ponto[0];
        }
        centroide[0] /= pontos.size();
        return centroide;
    }


    public static void main(String[] args) {
        Random gerador = new Random();
        int tamanho = gerador.nextInt(5,100);

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


        for (int i = 0; i < tamanho; i++) {
            int idade = gerador.nextInt(100);
            int faltas = gerador.nextInt(100);
            String nomeAleatorio = nomes[gerador.nextInt(nomes.length)];
            pessoas[i] = new Pessoa(nomeAleatorio, idade, faltas);

        }

        double[][] data = new double[pessoas.length][1];
        for (int i = 0; i < pessoas.length; i++) {
            data[i][0] = pessoas[i].faltas;
        }

        KMeans kMeans = new KMeans(2);
        kMeans.fit(data);
        System.out.println("-----------------Cluster dinamicos----------");
        kMeans.refinarClusters();
        kMeans.printClusters(pessoas, data);

    }

}

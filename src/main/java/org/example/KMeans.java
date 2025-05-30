package org.example;

import java.util.*;

public class KMeans {
    private final int k;
    private double[][] centroids;
    private List<List<double[]>> clusters;
    private static final double THRESHOLD = 1e-4;

    public KMeans(int k) {
        this.k = k;
    }

    private void initializeCentroids(double[][] data) {
        Random rand = new Random();
        centroids = new double[k][data[0].length];
        for (int i = 0; i < k; i++) {
            int randomIndex = rand.nextInt(data.length);
            centroids[i] = Arrays.copyOf(data[randomIndex], data[randomIndex].length);
        }
    }

    public List<List<double[]>> getClusters() {
        return clusters;
    }


    private void assignClusters(double[][] data) {
        clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {
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
        for (int i = 0; i < k; i++) {
            double distance = euclideanDistance(point, centroids[i]);
            if (distance < minDistance) {
                minDistance = distance;
                nearestIndex = i;
            }
        }
        return nearestIndex;
    }

    private double euclideanDistance(double[] point1, double[] point2) {
        double sum = 0.0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }
        return Math.sqrt(sum);
    }

    private boolean updateCentroids() {
        boolean changed = false;
        for (int i = 0; i < k; i++) {
            double[] newCentroid = new double[centroids[i].length];
            List<double[]> clusterPoints = clusters.get(i);
            if (clusterPoints.isEmpty()) {
                newCentroid = centroids[i];
            } else {
                for (double[] point : clusterPoints) {
                    for (int j = 0; j < point.length; j++) {
                        newCentroid[j] += point[j];
                    }
                }
                for (int j = 0; j < newCentroid.length; j++) {
                    newCentroid[j] /= clusterPoints.size();
                }
            }
            if (!arraysEqualWithinThreshold(centroids[i], newCentroid, THRESHOLD)) {
                changed = true;
                centroids[i] = newCentroid;
            }
        }
        return changed;
    }

    private boolean arraysEqualWithinThreshold(double[] a, double[] b, double threshold) {
        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i] - b[i]) > threshold) return false;
        }
        return true;
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
        System.out.println("Convergiu em " + iterations + " iterações");
    }

    public void printClusters(Pessoa[] pessoas, int tamanho) {
        System.out.println("-------------------------- DADOS ----------------------------");
        System.out.println("quantidade de pessoas: " + tamanho);
        for (int i = 0; i < k; i++) {
            for (Pessoa p : pessoas) {
                System.out.println("  " + p.nome + " | Idade: " + p.idade + " | Faltas: " + p.faltas);
            }
        }
        System.out.println("-------------------------- CLUSTERS ----------------------------");
        for (int i = 0; i < k; i++) {
            System.out.println("Cluster " + (i + 1) + " centroid: " + Arrays.toString(centroids[i]));
            System.out.println("Pessoas:");
            for (double[] point : clusters.get(i)) {
                for (Pessoa p : pessoas) {
                    if (p.faltas == (int) point[0]) {
                        System.out.println("  " + p.nome + " | Idade: " + p.idade + " | Faltas: " + p.faltas);
                        break;
                    }
                }
            }
            System.out.println();
        }
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
            int faltas = gerador.nextInt(15);
            String nomeAleatorio = nomes[gerador.nextInt(nomes.length)];
            pessoas[i] = new Pessoa(nomeAleatorio, idade, faltas);
        }

        double[][] data = new double[pessoas.length][1];
        for (int i = 0; i < pessoas.length; i++) {
            data[i][0] = pessoas[i].faltas;
        }

        KMeans kMeans = new KMeans(10);
        kMeans.fit(data);
        kMeans.printClusters(pessoas, tamanho);
    }

}

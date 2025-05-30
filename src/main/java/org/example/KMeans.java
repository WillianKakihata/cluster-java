package org.example;

import java.util.*;

public class KMeans {
    private int k;
    private double[][] centroids;
    private List<List<double[]>> clusters;
    private static final double THRESHOLD = 1e-4; // critério de parada (mudança pequena)

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
                // mantém o centróide antigo se cluster vazio
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
        } while (centroidsChanged && iterations < 100); // limita a 100 iterações para evitar loop infinito
        System.out.println("Convergiu em " + iterations + " iterações");
    }

    public void printClusters() {
        System.out.println("-------------------------- CLUSTERS ----------------------------");
        for (int i = 0; i < k; i++) {
            System.out.println("Cluster " + i + " centroid: " + Arrays.toString(centroids[i]));
            System.out.println("Points:");
            for (double[] point : clusters.get(i)) {
                System.out.println(Arrays.toString(point));
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        double[][] data = {
                {1.0, 2.0},
                {1.5, 1.8},
                {5.0, 8.0},
                {8.0, 8.0},
                {1.0, 0.6},
                {9.0, 11.0},
                {3.0, 5.0}
        };
        KMeans kMeans = new KMeans(3);
        kMeans.fit(data);
        kMeans.printClusters();
    }
}

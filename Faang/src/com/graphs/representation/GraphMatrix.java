package com.graphs.representation;

public class GraphMatrix {
    private int[][] matrix;
    private int vertices;

    public GraphMatrix(int vertices) {
        this.vertices = vertices;
        matrix = new int[vertices][vertices];
    }

    // Undirected
    //u = source vertex  (one end of the edge)
    //v = destination vertex  (other end of the edge)
    public void addEdge(int u, int v) {
        matrix[u][v] = 1;
        matrix[v][u] = 1; // remove this line for directed graph
    }

    // Weighted
    public void addWeightedEdge(int u, int v, int weight) {
        matrix[u][v] = weight;
        matrix[v][u] = weight;
    }

    public void printMatrix() {
        for (int[] row : matrix) {
            for (int val : row) System.out.print(val + " ");
            System.out.println();
        }
    }

    public static void main(String[] args) {
        GraphMatrix g = new GraphMatrix(4);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.printMatrix();
    }
}

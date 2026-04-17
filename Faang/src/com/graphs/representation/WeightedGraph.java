package com.graphs.representation;

import java.util.*;

public class WeightedGraph {

    // Inner class to represent neighbor + weight
    static class Edge {
        int dest, weight;
        Edge(int dest, int weight) {
            this.dest = dest;
            this.weight = weight;
        }
    }

    private int vertices;
    private List<List<Edge>> adjList;

    public WeightedGraph(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++)
            adjList.add(new ArrayList<>());
    }

    public void addEdge(int u, int v, int weight) {
        adjList.get(u).add(new Edge(v, weight));
        adjList.get(v).add(new Edge(u, weight)); // remove for directed
    }

    public void printGraph() {
        for (int i = 0; i < vertices; i++) {
            System.out.print(i + " -> ");
            for (Edge e : adjList.get(i))
                System.out.print("[" + e.dest + ", w=" + e.weight + "] ");
            System.out.println();
        }
    }

    public static void main(String[] args) {
        WeightedGraph g = new WeightedGraph(4);
        g.addEdge(0, 1, 5);
        g.addEdge(0, 2, 3);
        g.addEdge(1, 3, 2);
        g.printGraph();
    }
}
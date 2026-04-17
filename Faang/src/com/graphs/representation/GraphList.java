package com.graphs.representation;

import java.util.*;

public class GraphList {
    private int vertices;
    private List<List<Integer>> adjList;

    public GraphList(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++)
            adjList.add(new ArrayList<>());
    }

    public void addEdge(int u, int v) {
        adjList.get(u).add(v);
        adjList.get(v).add(u); // remove for directed graph
    }

    public List<Integer> getNeighbors(int u) {
        return adjList.get(u);
    }

    public void printGraph() {
        for (int i = 0; i < vertices; i++) {
            System.out.print(i + " -> ");
            System.out.println(adjList.get(i));
        }
    }

    public static void main(String[] args) {
        GraphList g = new GraphList(4);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.printGraph();
    }

    public List<Integer> bfsOfGraph(){
        List<Integer> bfs = new ArrayList<>();
        boolean[] visited = new boolean[this.vertices];
        Queue<Integer> queue = new LinkedList<>();

        queue.add(0);
        visited[0] = true;

        while(!queue.isEmpty()){
            Integer node = queue.poll();

            bfs.add(node);

            for(Integer edge:getNeighbors(node)){

                if(!visited[edge]){
                    visited[edge] = true;
                    queue.add(edge);
                }
            }
        }
        return bfs;
    }

    public void dfsOfGraph(List<Integer> dfs, int node,boolean[] visited) {
        dfs.add(node);
        visited[node] = true;

        for(int i:getNeighbors(node)){
            if(!visited[i]){
                dfsOfGraph(dfs,i,visited);
            }
        }
    }
}
package com.sam.wang.alg;

import java.util.LinkedList;
import java.util.List;

public class Graph {
  private List[] vertexes;

  public Graph(int v) {
    vertexes = new LinkedList[v];
  }

  public void addEdge(int v, int w) {
    if (vertexes[v] == null) {
      vertexes[v] = new LinkedList();
    }
    vertexes[v].add(w);

    if (vertexes[w] == null) {
      vertexes[w] = new LinkedList();
    }
    vertexes[w].add(v);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < vertexes.length; i++) {
      sb.append(i).append(":").append(vertexes[i]).append("\n");
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    Graph graph = new Graph(6);

    graph.addEdge(0, 1);
    graph.addEdge(0, 2);
    graph.addEdge(0, 5);
    graph.addEdge(1, 2);
    graph.addEdge(2, 3);
    graph.addEdge(2, 4);
    graph.addEdge(3, 4);
    graph.addEdge(3, 5);

    System.out.println(graph);
  }
}

package com.sam.wang.alg;

import java.util.LinkedList;
import java.util.List;

public class Graph {
  private List[] vertexes;

  public Graph(int v) {
    vertexes = new LinkedList[v];
    for (int i = 0; i < v; i++) {
      vertexes[i] = new LinkedList();
    }
  }

  public void addEdge(int v, int w) {
    vertexes[v].add(w);
    vertexes[w].add(v);
  }

  // find vertexes connected to source s
  public class Search {
    private int s;
    private boolean[] marks;
    private int count;

    public Search(int s) {
      this.s = s;
      marks = new boolean[vertexes.length];
      count = 0;
      dfs(s);
    }

    private void dfs(int v) {
      if (marks[v]) return;
      marks[v] = true;
      for (Object obj : vertexes[v]) {
        int vertex = (Integer) obj;
        if (!marks[vertex]) {
          dfs(vertex);
          count++;
        }
      }
    }

    // is v connected to s
    public boolean marked(int v) {
      return marks[v];
    }

    // how many vertexes are connected to s
    public int count() {
      return count;
    }
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
    Graph graph = new Graph(8);

    graph.addEdge(0, 1);
    graph.addEdge(0, 2);
    graph.addEdge(0, 5);
    graph.addEdge(1, 2);
    graph.addEdge(2, 3);
    graph.addEdge(2, 4);
    graph.addEdge(3, 4);
    graph.addEdge(3, 5);

    graph.addEdge(6, 7);

    System.out.println(graph);

    Search s = graph.new Search(6);
    System.out.println(s.marked(5));
    System.out.println(s.count());
  }
}

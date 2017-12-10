package com.samwang.alg;

import java.util.Collections;
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

  public class Path {
    private int s;
    private boolean[] marks;

    // Keep each valid path so that links array can be converted into a tree
    private Integer[] links;

    // find paths from source s
    public Path(int s, boolean needShortest) {
      this.s = s;
      marks = new boolean[vertexes.length];
      links = new Integer[vertexes.length];

      if (needShortest) {
        bfs(s);
      } else {
        dfs(s);
      }

      /*
      System.out.println("links:");
      for (int i = 0; i < links.length; i++) {
        System.out.println(i + ":" + links[i]);
      }
      */
    }

    private void bfs(int v) {
      LinkedList<Integer> queue = new LinkedList<>();
      queue.add(v);
      while (!queue.isEmpty()) {
        int vertex = queue.removeFirst();
        marks[vertex] = true;
        for (Object obj : vertexes[vertex]) {
          int vertexTo = (Integer) obj;
          if (!marks[vertexTo]) {
            queue.add(vertexTo);
            // Set the mark to prevent it from visiting in the next loop (since it is not a recurrence call)
            marks[vertexTo] = true;
            links[vertexTo] = vertex;
          }
        }
      }
    }

    private void dfs(int v) {
      if (marks[v]) return;
      marks[v] = true;
      for (Object obj : vertexes[v]) {
        int vertex = (Integer) obj;
        if (!marks[vertex]) {
          // Due to dfs, it's NOT the shortest path, instead the result tree
          // depends on the input order of edges
          links[vertex] = v;
          dfs(vertex);
        }
      }
    }

    // is there a path from s to v
    public boolean hasPathTo(int v) {
      return marks[v];
    }

    // path from s to v
    public Iterable<Integer> pathTo(int v) {
      if (!hasPathTo(v)) {
        return Collections.emptyList();
      }

      LinkedList<Integer> linkStack = new LinkedList<>();
      Integer link = v;
      linkStack.addFirst(link);

      while ((link = links[link]) != null) {
        linkStack.addFirst(link);
        if (link == s) {
          break;
        }
      }

      return linkStack;
    }
  }

  public class ConnectedComponent {
    private boolean[] marks;
    private int count;
    private int[] ids;
    public ConnectedComponent() {
      marks = new boolean[vertexes.length];
      ids = new int[vertexes.length];
      count = 0;
      for (int i = 0; i < vertexes.length; i++) {
        if (!marks[i]) {
          dfs(i);
          count++;
        }
      }
    }

    private void dfs(int v) {
      if (marks[v]) return;
      marks[v] = true;
      ids[v] = count;
      for (Object obj : vertexes[v]) {
        int vertex = (Integer) obj;
        if (!marks[vertex]) {
          dfs(vertex);
        }
      }
    }

    public boolean connected(int v, int w) {
      return ids[v] == ids[w];
    }

    // number of groups of connected components
    public int count() {
      return count;
    }

    // group identifier
    public int id(int v) {
      return ids[v];
    }
  }

  public class Cycle {
    private boolean[] marks;
    private boolean hasCycle;
    public Cycle() {
      marks = new boolean[vertexes.length];
      for (int i = 0; i < vertexes.length; i++) {
        if (!marks[i]) {
          dfs(i, i);
        }
      }
    }

    private void dfs(int v, int prev) {
      if (marks[v]) return;
      marks[v] = true;
      for (Object obj : vertexes[v]) {
        int vertex = (Integer) obj;
        if (!marks[vertex]) {
          dfs(vertex, v);
        } else if (vertex != v) {
          hasCycle = true;
          return;
        }
      }
    }

    public boolean hasCycle() {
      return hasCycle;
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

    Path p = graph.new Path(0, true);
    System.out.println(p.hasPathTo(4));
    for (int vertex : p.pathTo(4)) {
      System.out.print(vertex + "->");
    }
    System.out.println();

    ConnectedComponent cc = graph.new ConnectedComponent();
    System.out.println(cc.count());
    System.out.println(cc.connected(1, 5));
    System.out.println(cc.connected(1, 6));
    System.out.println(cc.connected(7, 6));

    Cycle cycle = graph.new Cycle();
    System.out.println("Has cycle:" + cycle.hasCycle());
  }
}

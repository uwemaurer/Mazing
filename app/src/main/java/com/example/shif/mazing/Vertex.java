package com.example.shif.mazing;

/**
 * A vertex in the graph.
 * Each vertex has a key and an array of edges connected to the vertex.
 */
public class Vertex {

    final int key;
    Integer[] edges;
    // Whether or not the vertex was visited by the MazeBuilder
    boolean visited;
    int distance;
    Vertex previous;
    MainActivity.Color color;

    public Vertex(int key) {
        this.key = key;
        this.visited = false;
        this.distance = Integer.MAX_VALUE;
        this.previous = null;
        this.color = MainActivity.Color.WHITE;
    }

    public Vertex(int key, Integer[] edges) {
        this.key = key;
        this.edges = edges;
        this.visited = false;
        this.distance = Integer.MAX_VALUE;
        this.previous = null;
        this.color = MainActivity.Color.WHITE;
    }

    public void setEdges(Integer[] edges) { this.edges = edges; }

    public void setVisitedToTrue() { this.visited = true; }

    public void setDistance(int d) { this.distance = d; }

    public void setPrevious(Vertex v) { this.previous = v; }

    public void setColor(MainActivity.Color color) { this.color = color; }
}

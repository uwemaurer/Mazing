package com.example.shif.mazing;

import java.util.List;

/**
 * A vertex in the graph. Each vertex has a key and an array of edges connected to the vertex.
 */
public class Vertex {

    final int row, column, key;
    List<Integer> edges;
    // Whether or not the vertex was visited by the MazeBuilder
    boolean visited;
    int distance;
    Vertex previous;
    MainActivity.Color color;

    public Vertex(int row, int column, int key) {
        this.row = row;
        this.column = column;
        this.key = key;

        this.visited = false;
        this.distance = Integer.MAX_VALUE;
        this.previous = null;
        this.color = MainActivity.Color.WHITE;
    }

    public void setVisitedToTrue() {
        this.visited = true;
    }

    public void setDistance(int d) {
        this.distance = d;
    }

    public void setPrevious(Vertex v) {
        this.previous = v;
    }

    public void setColor(MainActivity.Color color) {
        this.color = color;
    }

    public void removeEdge(int key) {
        // note: it matters here to call the remove(Integer), not remove(int)
        // remove by value, not index
        edges.remove((Integer) key);
    }
}

package com.example.shif.mazing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The graph representation of the maze, using an adjacency list. The constructor creates a graph
 * where each vertex is a cell in a grid, and each vertex (cell) has edges to all of its neighboring
 * vertices (cells). Method negateGraph creates a negative of an existing graph: considering each
 * vertex represents a cell in a grid, for each vertex (cell) the method adds edges to neighboring
 * cells (vertices) that are not connected and removes existing edges.
 */
public class Graph {
    final int rows, columns;

    final List<Vertex> V;

    public Vertex getVertex(int index) {
        return V.get(index);
    }
    public Vertex getVertex(int row, int column) {
        return V.get(columns * row + column);
    }

    public Graph(int rows, int columns) {
        this.columns = columns;
        this.rows = rows;
        int size = rows * columns;
        this.V = new ArrayList<>(size);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Vertex v = new Vertex(r, c, this.V.size());
                this.V.add(v);

                List<Integer> edgesArrayList = new ArrayList<>(4);
                if (r != 0) {
                    edgesArrayList.add(columns * (r - 1) + c);
                }
                if (c != columns - 1) {
                    edgesArrayList.add(columns * r + (c + 1));
                }
                if (r != rows - 1) {
                    edgesArrayList.add(columns * (r + 1) + c);
                }
                if (c != 0) {
                    edgesArrayList.add(columns * r + (c - 1));
                }
                Collections.shuffle(edgesArrayList);
                v.edges = edgesArrayList;
            }
        }
    }

    public Graph negateGraph() {
        Graph result = new Graph(this.rows, this.columns);
        // Iterate over all vertices and negate the adjacency lists
        for (int i = 0; i < result.V.size(); i++) {
            result.V.get(i).edges.removeAll(this.V.get(i).edges);
        }
        return result;
    }
}

package com.example.shif.mazing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The graph representation of the maze, using an adjacency list. The constructor creates a graph
 * where each vertex is a cell in a grid, and each vertex (cell) has edges to all of its neighboring
 * vertices (cells). Method negateGraph creates a negative of an existing graph: considering each
 * vertex represents a cell in a grid, for each vertex (cell) the method adds edges to neighboring
 * cells (vertices) that are not connected and removes existing edges.
 */
public class Graph {
    int rows, columns;

    Vertex[] V;

    public Graph(int rows, int columns) {
        this.columns = columns;
        this.rows = rows;
        int size = rows * columns;
        this.V = new Vertex[size];
        int index = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Vertex v = V[index] = new Vertex(index);
                index++;

                List<Integer> edgesArrayList = new ArrayList<>();
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
                v.edges = new Integer[edgesArrayList.size()];
                Collections.shuffle(edgesArrayList);
                v.edges = edgesArrayList.toArray(v.edges);
            }
        }
    }

    public Graph negateGraph() {
        Graph result = new Graph(this.rows, this.columns);

        // Iterate over all vertices and negate the adjacency lists
        for (int i = 0; i < result.V.length; i++) {
            Set<Integer> oldEdges = new HashSet<>(Arrays.asList(this.V[i].edges));

            Integer[] newAdjacencyList = result.V[i].edges;

            List<Integer> edgesToRemove = new ArrayList<>();
            // Iterate over the current vertex adjacency list
            for (Integer integer : newAdjacencyList) {
                // If the original graph contains the current edge, delete it from the new graph
                if (oldEdges.contains(integer)) {
                    edgesToRemove.add(integer);
                }
            }
            // Remove the edges to remove from the new adjacency list
            for (int newE = 0; newE < edgesToRemove.size(); newE++) {
                newAdjacencyList = MainActivity.removeValueFromArray(newAdjacencyList,
                    edgesToRemove.get(newE));
            }

            result.V[i].edges = newAdjacencyList;
        }
        return result;
    }
}

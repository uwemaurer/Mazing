package com.example.shif.mazing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Finds the longest path from the starting point of the maze (lower left corner) to a cell in the
 * circumference of the maze. When the solution of the maze is a longer path, the maze is more
 * satisfying to solve. Returns an array with the keys of the vertices along the solution path,
 * which is then used by FingerLine to detect when the user solves the maze.
 */
public class LongestPathFinder {

    private LongestPathFinder() {
    }

    public static List<Integer> findLongestPath(Graph graph) {
        Graph transposedGraph = graph.negateGraph();
        List<Integer> result = new ArrayList<>();
        int rows = graph.rows;
        int columns = graph.columns;
        int startIndex = columns * (rows - 1);
        Vertex start =
            transposedGraph.V[startIndex]; // To start from the top left corner, choose index 0

        int maxDistance = 0;

        Vertex farthestVertex = start;
        start.setColor(MainActivity.Color.GRAY);
        start.setDistance(0);
        // Create an empty queue Q
        Queue<Vertex> Q = new LinkedList<>();
        // insert start to Q
        Q.add(start);

        while (!Q.isEmpty()) {
            Vertex vertex = Q.remove();

            for (int i = 0; i < vertex.edges.length; i++) {

                int vertexIndex = vertex.edges[i];
                Vertex transposedV = transposedGraph.V[vertexIndex];

                if (transposedV.color == MainActivity.Color.WHITE) {
                    transposedV.setColor(MainActivity.Color.GRAY);
                    transposedV.setDistance(vertex.distance + 1);
                    boolean isOuter =
                        ((vertexIndex + 1) % columns < 2 || vertexIndex < rows || vertexIndex > (
                            columns * (rows - 1) - 1));
                    boolean isStart = transposedV.equals(start);

                    if (transposedV.distance > maxDistance && isOuter && !isStart) {
                        maxDistance = vertex.distance + 1;
                        farthestVertex = transposedV;
                    }
                    transposedV.setPrevious(vertex);
                    Q.add(transposedV);
                }
            }
            vertex.setColor(MainActivity.Color.BLACK);
        }
        // Retrieve a list of vertices keys
        Vertex currVertex = farthestVertex;
        result.add(currVertex.key);
        while (currVertex.previous != null) {
            currVertex = currVertex.previous;
            result.add(currVertex.key);
        }
        return result;
    }
}

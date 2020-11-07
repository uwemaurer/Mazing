package com.example.shif.mazing;

/**
 * Helper for building a maze from a fully-connected graph representing a full grid. Contains two
 * methods: carveWay changes the visual representation of the maze. It breaks down vertical and
 * horizontal walls in the mazeView, using a depth-first walk in the graph. removeEdges changes the
 * graph representation of the maze. It disconnects edges in the graph representation of the maze,
 * according to the state of the walls after carveWay was called. An effort to combine the two
 * functions resulted in a less readable code.
 */
public final class MazeBuilder {
    private MazeBuilder() {
    }

    public static void carveWay(Graph graph, Vertex start, MazeView mazeView) {
        for (int edge : start.edges) {
            // If the current neighbor was not visited yet,
            // break the wall between the current vertex and the neighbor
            // and then recursively carve way from the neighbor to its neighbors
            Vertex vertex = graph.getVertex(edge);
            if (!vertex.visited) {
                // Find which is the larger-key vertex
                Vertex biggerVertex;
                Vertex smallerVertex;
                if (start.key >= vertex.key) {
                    biggerVertex = start;
                    smallerVertex = vertex;
                } else {
                    biggerVertex = vertex;
                    smallerVertex = start;
                }

                int row = biggerVertex.row;
                int column = biggerVertex.column;

                // Check if wall is vertical or horizontal
                // and turn matching wall to false
                if (biggerVertex.row == smallerVertex.row) {
                    // vertical wall
                    mazeView.verticalLines[row][column] = false;
                } else {
                    // Horizontal wall
                    mazeView.horizontalLines[row][column] = false;
                }

                // Change visited to true
                vertex.setVisitedToTrue();

                // Move on recursively
                carveWay(graph, vertex, mazeView);
            }
        }
        // All neighbors were visited, move back in recursion
    }

    public static void removeEdges(MazeView mazeView) {
        Graph graph = mazeView.graph;
        int verticalLinesColumnCount = graph.columns + 1;
        int horizontalLinesRowCount = graph.rows + 1;
        for (int row = 0; row < graph.rows; row++) {
            // Don't iterate over the first and last vertical lines as they are the maze's borders
            for (int linesColumn = 1; linesColumn < verticalLinesColumnCount - 1; linesColumn++) {
                // If there is no line, remove the edge
                if (!mazeView.verticalLines[row][linesColumn]) {
                    Vertex v1 = graph.getVertex(row, linesColumn - 1);
                    Vertex v2 = graph.getVertex(row, linesColumn);
                    v1.removeEdge(v2.key);
                    v2.removeEdge(v1.key);
                }
            }
        }

        // Iterate over the horizontalLines

        // Don't iterate over the first and last horizontal lines as they are the maze's borders
        for (int linesRow = 1; linesRow < horizontalLinesRowCount - 1; linesRow++) {
            for (int column = 0; column < graph.columns; column++) {
                // If there is not line, remove the edge
                if (!mazeView.horizontalLines[linesRow][column]) {
                    Vertex v1 = graph.getVertex(linesRow - 1, column);
                    Vertex v2 = graph.getVertex(linesRow, column);
                    v1.removeEdge(v2.key);
                    v2.removeEdge(v1.key);
                }
            }
        }
    }
}

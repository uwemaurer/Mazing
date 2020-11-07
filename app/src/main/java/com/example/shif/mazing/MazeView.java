package com.example.shif.mazing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.example.shif.mazing.MainActivity.FAT_FINGERS_MARGIN;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import androidx.core.content.ContextCompat;

/**
 * This view displays the maze on screen. It holds the graph that represents the maze, and the
 * arrays of vertical and horizontal walls.
 */
public class MazeView extends View {

    public Paint mazePaint;
    public int screenWidth;
    public int screenHeight;
    int cellWidth;
    int cellHeight;
    int padding;
    DisplayMetrics displaymetrics;
    public boolean[][] verticalLines;
    public boolean[][] horizontalLines;
    public List<Integer> solutionVerticesKeys;
    public Graph graph;
    public double fractionOfWallsToRemove = 0.15;

    public MazeView(Context context, int rows, int columns) {
        super(context);
        this.graph = new Graph(rows, columns);
        init();
    }

    public MazeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Create the paint brush
        mazePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mazePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        mazePaint.setStyle(Paint.Style.STROKE);
        mazePaint.setStrokeCap(Paint.Cap.ROUND);
        mazePaint.setStrokeJoin(Paint.Join.ROUND);
        mazePaint.setStrokeWidth(14f);

        displaymetrics = new DisplayMetrics();
        padding = 32;
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels - padding * 2;
        screenHeight = displaymetrics.heightPixels - padding * 2;
        int rows = graph.rows;
        int columns = graph.columns;

        cellWidth = cellHeight = Math.min(screenWidth / columns, screenHeight * 2 / 3 / rows);

        // Create 2 2-dimensional arrays to keep the vertical and the horizontal lines of the
        // MazeView
        verticalLines = new boolean[rows][columns + 1];
        horizontalLines = new boolean[rows + 1][columns];

        for (boolean[] verticalLine : verticalLines) {
            Arrays.fill(verticalLine, true);
        }
        for (boolean[] horizontalLine : horizontalLines) {
            Arrays.fill(horizontalLine, true);
        }
        // Break the starting wall of the maze
        horizontalLines[rows][0] = false;

        // The maze starts at the left-bottom corner of the screen

        // Declare the first cell of the maze as visited
        Vertex start = graph.getVertex(rows - 1, 0);
        start.visited = true;
        MazeBuilder.carveWay(graph, start, this);

        // Improvement of the maze: remove a few random walls
        // to make the maze more confusing.
        // Choose randomly a few walls and break those walls
        Random rand = new Random();
        for (int holes = 0; holes < Math.floor(
            Math.pow(fractionOfWallsToRemove * (rows + columns) * 0.5f, 2)); holes++) {

            int rowVertical = rand.nextInt(rows);
            int columnVertical = rand.nextInt(columns - 1) + 1;
            verticalLines[rowVertical][columnVertical] = false;

            int rowHorizontal = rand.nextInt(rows - 1) + 1;
            int columnHorizontal = rand.nextInt(columns);
            horizontalLines[rowHorizontal][columnHorizontal] = false;
        }

        MazeBuilder.removeEdges(this);

        solutionVerticesKeys = LongestPathFinder.findLongestPath(graph);

        // Break the ending wall of the maze
        // get the end vertex key

        Vertex endVertex = graph.getVertex(solutionVerticesKeys.get(0));

        // check if it's vertical or horizontal
        boolean horizontalEnd = endVertex.row == 0 || endVertex.row == (rows - 1);
        if (horizontalEnd) {
            if (endVertex.row ==0) { // top row of horizontal lines
                horizontalLines[0][endVertex.column] = false;
            } else { // bottom row of horizontal lines
                horizontalLines[rows][endVertex.column] = false;
            }
        } else {
            if (endVertex.column == 0) { // left most column of vertical lines
                verticalLines[endVertex.row][0] = false;
            } else { // right most column of vertical lines
                verticalLines[endVertex.row][columns] = false;
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Iterate over the boolean arrays to draw walls
        for (int row = 0; row < graph.rows + 1; row++) {
            for (int column = 0; column < graph.columns + 1; column++) {
                float x = column * cellWidth + padding;
                float y = row * cellHeight + padding;

                if (row < graph.rows && verticalLines[row][column]) {
                    // Draw a vertical line
                    canvas.drawLine(x, y, x, y + cellHeight, mazePaint);
                }

                if (column < graph.columns && horizontalLines[row][column]) {
                    // Draw a horizontal line
                    canvas.drawLine(x, y, x + cellWidth, y, mazePaint);
                }
            }
        }
    }

    public List<Rect> getSolutionAreas() {
        int columns = graph.columns;
        // Trace the path from start to farthestVertex using the line of predecessors,
        // apply this information to form an array of rectangles
        // which will be passed on to fingerLine view
        // where the line has to pass.
        // The array be checked against the drawn line in FingerLine.

        List<Rect> solutionAreas = new ArrayList<>();

        for (int currentVertexKey : solutionVerticesKeys) {
            Vertex vertex = graph.getVertex(currentVertexKey);
            // Translate vertex key to location on screen
            int row = vertex.row;
            int column = vertex.column;
            int left = padding + (column * cellWidth) - FAT_FINGERS_MARGIN;
            int top = padding + (row * cellHeight) - FAT_FINGERS_MARGIN;

            int right = padding + ((column + 1) * cellWidth) + FAT_FINGERS_MARGIN;
            int bottom = padding + ((row + 1) * cellHeight) + FAT_FINGERS_MARGIN;
            solutionAreas.add(new Rect(left, top, right, bottom));
        }
        return solutionAreas;
    }

    public int getColumnFromCoordinate(int x) {
        int column =  (x - padding) / cellWidth;
        return column >= 0 && column < graph.columns ? column : -1;
    }

    public int getRowFromCoordinate(int y) {
        int row =  (y - padding) / cellHeight;
        return row >= 0 && row < graph.rows ? row : -1;
    }

    public boolean canConnect(int row1, int column1, int row2, int column2) {
        if (row1 <0 || row2 < 0 || column1 <0 || column2<0) {
            return false;
        }
        if (row1 == row2) {
            if (column1 == column2) {
                return true;
            }
            int c1 = Math.min(column1, column2);
            int c2 = Math.max(column1, column2);
            if (c2 == c1 + 1) {
                return !verticalLines[row1][c1 + 1];
            }
        } else if (column1 == column2) {
            int r1 = Math.min(row1, row2);
            int r2 = Math.max(row1, row2);
            if (r2 == r1 + 1) {
                return !horizontalLines[r1 + 1][column1];
            }
        }
        return false;
    }
}

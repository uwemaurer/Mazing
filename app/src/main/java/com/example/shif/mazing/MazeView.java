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
            Arrays.fill(verticalLine, Boolean.TRUE);
        }
        for (boolean[] horizontalLine : horizontalLines) {
            Arrays.fill(horizontalLine, Boolean.TRUE);
        }
        // Break the starting wall of the maze
        horizontalLines[rows][0] = false;

        // The maze starts at the left-bottom corner of the screen
        int graphStartKey = columns * (rows - 1);
        // Declare the first cell of the maze as visited
        graph.V[graphStartKey].visited = true;

        MazeBuilder.carveWay(graph, graph.V[graphStartKey], this);

        // Improvement of the maze: remove a few random walls
        // to make the maze more confusing.
        // Choose randomly a few walls and break those walls
        Random rand = new Random();
        for (int holes = 0; holes < Math.floor(
            Math.pow(fractionOfWallsToRemove * (rows + columns) * 0.5f, 2)); holes++) {

            int randomXvertical = rand.nextInt(rows);
            int randomYvertical = rand.nextInt(columns - 1) + 1;
            verticalLines[randomXvertical][randomYvertical] = false;

            int randomXhorizontal = rand.nextInt(rows - 1) + 1;
            int randomYhorizontal = rand.nextInt(columns);
            horizontalLines[randomXhorizontal][randomYhorizontal] = false;
        }

        MazeBuilder.removeEdges(this);

        solutionVerticesKeys = LongestPathFinder.findLongestPath(graph);

        // Break the ending wall of the maze
        // get the end vertex key
        int endKey = solutionVerticesKeys.get(0);

        // check if it's vertical or horizontal
        boolean horizontalEnd = endKey < columns || endKey >= columns * (rows - 1);
        if (horizontalEnd) {
            if (endKey < columns) { // top row of horizontal lines
                horizontalLines[0][endKey] = false;
            } else { // bottom row of horizontal lines
                horizontalLines[rows][endKey % columns] = false;
            }
        } else {
            if (endKey % columns == 0) { // left most column of vertical lines
                verticalLines[endKey / columns][0] = false;
            } else { // right most column of vertical lines
                verticalLines[endKey / columns][columns] = false;
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
            // Translate vertex key to location on screen
            int row = (currentVertexKey) / columns;
            int column = (currentVertexKey) % columns;
            int left = padding + (column * cellWidth) - FAT_FINGERS_MARGIN;
            int top = padding + (row * cellHeight) - FAT_FINGERS_MARGIN;

            int right = padding + ((column + 1) * cellWidth) + FAT_FINGERS_MARGIN;
            int bottom = padding + ((row + 1) * cellHeight) + FAT_FINGERS_MARGIN;
            solutionAreas.add(new Rect(left, top, right, bottom));
        }
        return solutionAreas;
    }
}

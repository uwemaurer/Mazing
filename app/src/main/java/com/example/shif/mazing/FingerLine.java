package com.example.shif.mazing;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * View of the line the user draws with their finger.
 */
public class FingerLine extends View {
    private Paint mPaint, debugPaint;
    private Path mPath;
    private List<Rect> solutionPath;
    int solutionCellsVisited;
    private ArrayList<Boolean> solved;
    private boolean solvedMaze;

    public FingerLine(Context context) {
        this(context, null);
        init();
    }

    public FingerLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FingerLine(Context context, AttributeSet attrs, List<Rect> solutionPath) {
        super(context, attrs);
        this.solutionPath = solutionPath;
        init();
    }

    private void init() {
        // Create the paint brush
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.STROKE);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.solver));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(10f);

        debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        debugPaint.setStyle(Style.STROKE);
        debugPaint.setStrokeWidth(1f);

        mPath = new Path();

        solutionCellsVisited = 0;

        solvedMaze = false;

        solved = new ArrayList<>(solutionPath.size());
        for (int i = 0; i < solutionPath.size(); i++) {
            solved.add(false);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
        //debug(canvas);
    }

    private void debug(Canvas canvas) {
        for (int i = 0; i < solutionPath.size(); i++) {
            debugPaint.setColor(solved.get(i) ? Color.GREEN : Color.GRAY);
            canvas.drawRect(solutionPath.get(i), debugPaint);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mPath.moveTo(event.getX(), event.getY());
            return true;
        case MotionEvent.ACTION_MOVE:
            mPath.lineTo(event.getX(), event.getY());
            break;
        case MotionEvent.ACTION_UP:
            break;
        default:
            return false;
        }
        // Schedule a repaint
        invalidate();
        // Check if user solved the maze
        int x = (int) event.getX();
        int y = (int) event.getY();
        for (int i = 0; i < solutionPath.size(); i++) {
            if (solutionPath.get(i).contains(x, y)) {
                solved.set(i, true);
                if (!solved.contains(false) && !solvedMaze) {
                    ((MainActivity) getContext()).startGameSolvedAnimation();
                    Toast.makeText(this.getContext(), R.string.maze_solved, Toast.LENGTH_SHORT)
                        .show();
                    solvedMaze = true;
                    return true;
                }
            }
        }
        return true;
    }
}

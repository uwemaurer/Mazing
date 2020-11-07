/*
 * Copyright 2016 Shif Ben Avraham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.shif.mazing;

import java.util.List;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public enum Color {
        WHITE, GRAY, BLACK
    }

    public MazeView mMazeView;
    public FingerLine mFingerLine;
    private ImageView strawberry;
    ImageView arrow;
    public FingerLine line;
    public Toolbar mazingToolBar;
    DisplayMetrics displaymetrics = new DisplayMetrics();
    FrameLayout mFrameLayout;
    public static final int FAT_FINGERS_MARGIN = 25;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        mazingToolBar = findViewById(R.id.mazing_toolbar);
        setSupportActionBar(mazingToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mToolBarText = findViewById(R.id.toolbar_title);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Schoolbell.ttf");
        mToolBarText.setTypeface(typeFace);

        mFrameLayout = findViewById(R.id.mazeWrapper);
        ViewGroup.LayoutParams params = mFrameLayout.getLayoutParams();
        params.height = (int) Math.floor(displaymetrics.heightPixels * 0.7);
        mFrameLayout.setLayoutParams(params);

        FloatingActionButton newMazeButton = findViewById(R.id.newMazeButton);
        newMazeButton.setOnClickListener(v -> createMaze());
        newMazeButton.performClick();
    }

    public void createMaze() {
        // First remove any existing MazeView and FingerLine
        if (mMazeView != null) {
            ((ViewGroup) mMazeView.getParent()).removeView(mMazeView);
        }
        if (mFingerLine != null) {
            ((ViewGroup) mFingerLine.getParent()).removeView(mFingerLine);
        }
        int rows = 5;
        int columns = 6;
        mMazeView = new MazeView(this, rows, columns);
        List<Rect> solutionAreas = mMazeView.getSolutionAreas();

        mFrameLayout.addView(mMazeView);
        mFingerLine = new FingerLine(this, null, solutionAreas, mMazeView);
        mFrameLayout.addView(mFingerLine);

        // Add start arrow pic
        int startCellArrowX =
            solutionAreas.get(mMazeView.solutionVerticesKeys.size() - 1).left + 12 + FAT_FINGERS_MARGIN;
        int startCellArrowY =
            solutionAreas.get(mMazeView.solutionVerticesKeys.size() - 1).top + 100 + FAT_FINGERS_MARGIN;
        arrow = findViewById(R.id.arrow);
        arrow.setX(startCellArrowX);
        arrow.setY(startCellArrowY);
        arrow.setVisibility(View.VISIBLE);

        // Add strawberry pic
        int endCellStrawberryX = solutionAreas.get(0).left + 8 + FAT_FINGERS_MARGIN;
        int endCellStrawberryY = solutionAreas.get(0).top + 10 + FAT_FINGERS_MARGIN;
        strawberry = findViewById(R.id.strawberry);
        strawberry.setX(endCellStrawberryX);
        strawberry.setY(endCellStrawberryY);
        strawberry.setVisibility(View.VISIBLE);
    }

    public void startGameSolvedAnimation() {
        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(700);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(3);
        animation.setRepeatMode(Animation.REVERSE);
        strawberry.startAnimation(animation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_level) {
            // User chose the "Level" item, show the level settings UI...
            return true;
        }// If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
}

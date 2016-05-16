package com.example.bishal.fitness;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Bishal on 5/15/2016.
 */
public class VerticalSpace extends RecyclerView.ItemDecoration {
    int space;
    public  VerticalSpace(int space){
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        if(parent.getChildLayoutPosition(view)==0){
            outRect.top = space;
        }
    }
}

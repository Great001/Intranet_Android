package com.xogrp.tkgz.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by hliao on 6/21/2016.
 */
public class ItemDividerDecoration extends RecyclerView.ItemDecoration {

    private static final int [] ATTR=new int []{android.R.attr.listDivider};
    private Drawable mDivider;

    private static final int HORIZONTAL_LIST= LinearLayout.HORIZONTAL;
    private static final int VERTICAL_LIST=LinearLayout.VERTICAL;

    private int mOrientation;



    public ItemDividerDecoration(Context context,int orientation){
        final TypedArray array=context.obtainStyledAttributes(ATTR);
        mDivider=array.getDrawable(0);
        array.recycle();
        setOrientation(orientation);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if(mOrientation==VERTICAL_LIST){
            drawVetical(c,parent);
        }
        else if(mOrientation==HORIZONTAL_LIST){
            drawHorizontal(c,parent);
        }
    }

    public void setOrientation(int orientation){
        if(orientation!=HORIZONTAL_LIST&&orientation!=VERTICAL_LIST){
            throw new IllegalArgumentException("uninvalid orientation");
        }
        else{
            mOrientation=orientation;
        }
    }

    public void drawVetical(Canvas c,RecyclerView parent){
        int count=parent.getChildCount();
        int left=parent.getPaddingLeft();
        int right=parent.getWidth()-parent.getPaddingRight();

        for(int i=0;i<count;i++){
            View child=parent.getChildAt(i);
            final RecyclerView.LayoutParams Params=(RecyclerView.LayoutParams)child.getLayoutParams();
            int top=child.getBottom()+Params.bottomMargin;
            int bottom=top+mDivider.getIntrinsicHeight();
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
        }
    }


    public void drawHorizontal(Canvas c,RecyclerView parent){
        int count=parent.getChildCount();
        int top=parent.getPaddingTop();
        int bottom=parent.getHeight()-parent.getPaddingBottom();

        for(int i=0;i<count;i++){
            View child=parent.getChildAt(i);
            final RecyclerView.LayoutParams Params=(RecyclerView.LayoutParams)child.getLayoutParams();
            int left=child.getRight()+Params.rightMargin;
            int right=left+mDivider.getIntrinsicWidth();
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
        }

    }


}

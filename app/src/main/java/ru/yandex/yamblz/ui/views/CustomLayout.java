package ru.yandex.yamblz.ui.views;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.ViewGroup;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by GEORGY on 31.07.2016.
 */

public class CustomLayout extends ViewGroup {

    public CustomLayout(Context context) {
        super(context, null);
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        int deviceWidth = deviceDisplay.x;

        int leftView = getPaddingLeft();
        int topView = getPaddingTop();
        int rightView;

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);

            if (childView.getVisibility() != GONE) {
                rightView = leftView + childView.getMeasuredWidth();
                if (rightView > deviceWidth) {
                    throw new IllegalStateException("Перебор по длине! "+rightView + " "+ deviceWidth);
                }
                childView.layout(leftView, topView, rightView, childView.getMeasuredHeight());

                leftView = rightView;
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);

        int width = getPaddingLeft() + getPaddingRight();
        int maxHeight = getPaddingTop() + getPaddingBottom();

        View matchParentChild = null;

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);

            if (childView.getVisibility() != GONE) {

                measureChild(childView, widthMeasureSpec, heightMeasureSpec);

                if (childView.getLayoutParams().width == LayoutParams.MATCH_PARENT) {
                    if (matchParentChild != null) {
                        throw new IllegalStateException("Только один match_parent!");
                    }
                    matchParentChild = childView;
                } else {
                    width += childView.getMeasuredWidth();
                }

                maxHeight = Math.max(maxHeight, childView.getHeight());
            }
        }
        if (matchParentChild != null) {
            matchParentChild.measure(MeasureSpec.makeMeasureSpec(Math.max(maxWidth - width, 0), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(matchParentChild.getMeasuredHeight(), MeasureSpec.EXACTLY));
        }

        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
                resolveSize(maxHeight, heightMeasureSpec));
    }


    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

}
package com.wuxufanhua.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Created by wuxufanhua on 2016/7/1.
 */
public class ParallaxScrollView extends ScrollView {

    private static final float DEFAULT_PARALLAX_FACTOR = 1.9F;

    public ParallaxScrollView(Context context) {
        super(context);
    }

    public ParallaxScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParallaxScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        ViewGroup childAt = (ViewGroup) getChildAt(0);
        View childAt1 = childAt.getChildAt(0);
        childAt1.setTranslationY(t / DEFAULT_PARALLAX_FACTOR);
    }
}

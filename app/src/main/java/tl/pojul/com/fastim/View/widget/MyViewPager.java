package tl.pojul.com.fastim.View.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyViewPager extends ViewPager {


    public MyViewPager(Context context) {
        super(context);
    }
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canSlide;
    }

    @Override public boolean onTouchEvent(MotionEvent ev) { return canSlide; }
    }*/

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this && v instanceof ViewPager) {
            ViewPager viewPager = (ViewPager) v;
            if(viewPager.getCurrentItem() != 0 || viewPager.getCurrentItem() != (viewPager.getAdapter().getCount() - 1)){
                return true;
            }
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

}

package tl.pojul.com.fastim.View.widget.marqueeview.util;

import android.view.View;

/**
 * Created by GongWen on 18/1/8.
 */

public interface OnItemClickListener<V extends View, E> {
    void onItemClickListener(V mView, E mData, int mPosition);
}
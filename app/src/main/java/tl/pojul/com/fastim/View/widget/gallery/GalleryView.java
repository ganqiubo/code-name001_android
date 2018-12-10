package tl.pojul.com.fastim.View.widget.gallery;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.PhotoViewPager;

public class GalleryView extends RelativeLayout {

    @BindView(R.id.viewPager)
    PhotoViewPager viewPager;
    @BindView(R.id.index)
    TextView index;

    private ArrayList<String> urls = new ArrayList<>();

    private GalleryAdapter galleryAdapter;

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.include_gallery_view, this);
        ButterKnife.bind(this);
        /*galleryAdapter = new GalleryAdapter(urls, getContext());
        viewPager.setAdapter(galleryAdapter);*/
    }

    public void setData(ArrayList<String> urls) {
        this.urls = urls;
        galleryAdapter = new GalleryAdapter(urls, getContext());
        viewPager.setAdapter(galleryAdapter);
        index.setText("1/" + urls.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index.setText((position + 1) + "/" + urls.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


}
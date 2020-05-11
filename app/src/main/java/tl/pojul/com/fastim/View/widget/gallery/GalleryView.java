package tl.pojul.com.fastim.View.widget.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    public String getCurrentUrl(){
        if(urls == null){
            return null;
        }
        return urls.get(viewPager.getCurrentItem());
    }

    public String getCurrentBitmapUrl(){
        if(galleryAdapter != null && galleryAdapter.getCurrentPhotoUrl() != null &&
                !galleryAdapter.getCurrentPhotoUrl().equals("")){
            /*galleryAdapter.getCurrentPhoto().buildDrawingCache();
            //BitmapDrawable bmpDrawable = (BitmapDrawable) galleryAdapter.getCurrentPhoto().getDrawable();
            Bitmap bitmap = galleryAdapter.getCurrentPhoto().getDrawingCache();*/
            return galleryAdapter.getCurrentPhotoUrl();
        }
        return null;
    }


}

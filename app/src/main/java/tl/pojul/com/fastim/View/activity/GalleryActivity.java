package tl.pojul.com.fastim.View.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.pojul.fastIM.entity.Pic;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.gallery.GalleryView;

public class GalleryActivity extends BaseActivity {

    private static final int INIT = 326;
    @BindView(R.id.gallery_view)
    GalleryView galleryView;

    private ArrayList<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        urls = getIntent().getStringArrayListExtra("urls");
        if (urls == null || urls.size() <= 0) {
            finish();
            return;
        }

        mHandler.sendEmptyMessageDelayed(INIT, 100);

    }


    private void init() {
        galleryView.setData(urls);
    }


    private MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<GalleryActivity> activity;

        MyHandler(GalleryActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activity.get() == null) {
                return;
            }
            switch (msg.what) {
                case INIT:
                    activity.get().init();
                    break;
            }
        }
    }
}

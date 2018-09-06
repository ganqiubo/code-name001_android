package tl.pojul.com.fastim.View.widget.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import java.util.ArrayList;

import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;
import tl.pojul.com.fastim.GlideApp;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.GlideUtil;

public class GalleryAdapter extends PagerAdapter {

    private ArrayList<String> urls;
    private Context context;
    private TextView progressTv;

    public GalleryAdapter(ArrayList<String> urls, Context context) {
        this.urls = urls;
        this.context = context;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_gallery, null);
        container.addView(v);
        PhotoView imge = v.findViewById(R.id.photo);
        ProgressBar progress = v.findViewById(R.id.progress);
        RelativeLayout progressLl = v.findViewById(R.id.progress_ll);
        TextView progressTv = v.findViewById(R.id.progress_tv);
        progressLl.setVisibility(View.GONE);
        final String[] url = {new String(urls.get(position))};
        ProgressManager.getInstance().addResponseListener(url[0], new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                if(progressInfo.isFinish() || progressInfo.getPercent() >= 100){
                    progressLl.setVisibility(View.GONE);
                    ProgressManager.getInstance().notifyOnErorr(urls.get(position), new Exception("finished"));
                    url[0] = null;
                    return;
                }
                progressLl.setVisibility(View.VISIBLE);
                progress.setProgress(progressInfo.getPercent());
                progressTv.setText((progressInfo.getPercent() + "%"));
                Log.e("ProgressManager", "ProgressManager: " + progressInfo.getPercent());
            }

            @Override
            public void onError(long id, Exception e) {
                progressLl.setVisibility(View.GONE);
                url[0] = null;
                Log.e("ProgressManager", "ProgressManager: " + e.getMessage());
            }
        });
        GlideApp.with(context)
                .load(url[0])
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressLl.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imge);

        //GlideUtil.setImageBitmapNoOptions(new String(urls.get(position)), imge);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

}

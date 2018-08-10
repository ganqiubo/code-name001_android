package tl.pojul.com.fastim.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pojul.fastIM.entity.Pic;
import com.pojul.objectsocket.utils.FileClassUtil;

import java.io.File;

import tl.pojul.com.fastim.R;

public class GlideUtil {

    public static void setImageBitmap(String url, ImageView imageView, float thumbnail){
        if(url == null){
            return;
        }
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.pic)
                .error(R.drawable.pic)
                .fallback(R.drawable.pic);
        if(FileClassUtil.isHttpUrl(url)){
            Glide.with(imageView).load(url).thumbnail(thumbnail).apply(options).into(imageView);
        }else{
            File file = new File(url);
            Glide.with(imageView).load(file).thumbnail(thumbnail).apply(options).into(imageView);
        }
    }

}

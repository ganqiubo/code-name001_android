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
        try{
            if(url == null){
                return;
            }
            if(imageView == null || imageView.getContext() == null){
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
        }catch(Exception e){}
    }

    public static void setImageBitmap(String url, ImageView imageView){
        try{
            if(url == null){
                return;
            }
            if(imageView == null || imageView.getContext() == null){
                return;
            }
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.pic)
                    .error(R.drawable.pic)
                    .fallback(R.drawable.pic);
            if(FileClassUtil.isHttpUrl(url)){
                Glide.with(imageView).load(url).apply(options).into(imageView);
            }else{
                File file = new File(url);
                Glide.with(imageView).load(file).apply(options).into(imageView);
            }
        }catch(Exception e){}
    }

    public static void setImageBitmapNoOptions(String url, ImageView imageView){
        try{
            if(url == null){
                return;
            }
            if(imageView == null || imageView.getContext() == null){
                return;
            }
            if(FileClassUtil.isHttpUrl(url)){
                Glide.with(imageView).load(url).into(imageView);
            }else{
                File file = new File(url);
                Glide.with(imageView).load(file).into(imageView);
            }
        }catch(Exception e){}
    }

}

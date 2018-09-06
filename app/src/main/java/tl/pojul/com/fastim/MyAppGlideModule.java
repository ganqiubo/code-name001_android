package tl.pojul.com.fastim;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
       //BaseApplication application = (BaseApplication) context.getApplicationContext();
        //Glide 底层默认使用 HttpConnection 进 行网络请求,这里替换为 Okhttp 后才能使用本框架,进行 Glide 的加载进度监听
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(MyApplication.getApplication().getOkHttpClient()));

    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

}

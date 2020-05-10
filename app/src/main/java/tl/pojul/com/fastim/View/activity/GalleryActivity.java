package tl.pojul.com.fastim.View.activity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.gallery.GalleryView;
import tl.pojul.com.fastim.util.Constant;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class GalleryActivity extends BaseActivity {

    private static final int INIT = 326;
    private static final int CROP_PICTURE = 7465;
    @BindView(R.id.gallery_view)
    GalleryView galleryView;
    @BindView(R.id.more)
    ImageView more;
    private String wallperPath;
    private int wallperType = 0; //0: lock; 1: desktop

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
        AniExit = getIntent().getIntExtra("AniExit", 0);
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

    @OnClick({R.id.more})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.more:
                DialogUtil.getInstance().showGalleryMorePop(GalleryActivity.this, more);
                DialogUtil.getInstance().setDialogClick(str -> {
                    onMorePopClick(str);
                });
                break;
        }
    }

    private void onMorePopClick(String str) {
        switch (str){
            case "save":
                savePic(true);
                break;
            case "share":
                sharePic();
                break;
            /*case "lock_wallper":
                cropWallper();
                wallperType = 0;
                break;
            case "desktop_wallper":
                cropWallper();
                wallperType = 1;
                break;*/
        }
    }

    private void sharePic() {
        String url = galleryView.getCurrentUrl();
        UMImage image1 = new UMImage(this, url);//网络图片
        new ShareAction(this)
                .withMedia(image1)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(umShareListener)
                .open();
        /*UMWeb web = new UMWeb("http://a.hiphotos.baidu.com/image/pic/item/2f738bd4b31c8701e96739342a7f9e2f0608ff0b.jpg");
        UMImage image = new UMImage(this, "http://a.hiphotos.baidu.com/image/pic/item/2f738bd4b31c8701e96739342a7f9e2f0608ff0b.jpg");
        web.setTitle("来自脚步社区.每日一图");
        web.setDescription("描述");
        web.setThumb(image);
        new ShareAction(this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(umShareListener)
                .open();*/
    }

    private UMShareListener umShareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
        }
    };

    private String savePic(boolean toast){
        Bitmap bitmap = galleryView.getCurrentBitmap();
        if(bitmap == null){
            if(toast){
                showShortToas("图片正在加载中");
            }
            return null;
        }else{
            String filePath = SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) +
                    "/footstep/downloadPic/" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(filePath);
            if(!imageFile.getParentFile().exists()){
                imageFile.getParentFile().mkdirs();
            }
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                if(toast){
                    showLongToas("图片已保存至" + filePath);
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(new File(filePath)));
                    this.sendBroadcast(mediaScanIntent);
                }
                return filePath;
            } catch (Exception e) {
                if(toast){
                    showShortToas("保存失败");
                }
                return null;
            }
        }
    }

    private void cropWallper(){
        Bitmap bitmap = galleryView.getCurrentBitmap();
        if(bitmap == null){
            showShortToas("图片正在加载中");
        } else {
            cutoutPic();
        }
    }

    private void setLockWallper() {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(wallperPath);
            WallpaperManager wpManager = WallpaperManager.getInstance(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wpManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
            } else {
                Class class1 = wpManager.getClass();
                Method setWallPaperMethod = class1.getMethod("setLockScreenBitmap", Bitmap.class);
                setWallPaperMethod.invoke(wpManager, bitmap);
            }
            showShortToas("设置成功");
        } catch (Exception e) {
            showShortToas("设置失败");
        }finally {
            clearCrop();
        }
    }

    private void setDesktopWallper(){
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(wallperPath);
            WallpaperManager wpManager = WallpaperManager.getInstance(this);
            wpManager.setBitmap(bitmap);
            showShortToas("设置成功");
        } catch (Exception e) {
            showShortToas("设置失败");
        }finally {
            clearCrop();
        }
    }

    private void clearCrop() {
        if(wallperPath != null){
            File file = new File(wallperPath);
            if(file.exists()){
                file.delete();
            }
        }
    }

    private void cutoutPic() {
        String path = savePic(false);
        wallperPath = path;
        if(path == null){
            showShortToas("设置失败");
            return;
        }
        Uri uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(this, "tl.pojul.com.fastim.fileProvider", new File(path));
        }else{
            path = "file:///" + path;
            uri = Uri.parse(path);
        }
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        //该参数可以不设定用来规定裁剪区的宽高比
        intent.putExtra("aspectX", MyApplication.SCREEN_WIDTH);
        intent.putExtra("aspectY", MyApplication.SCREEN_HEIGHT);
        //该参数设定为你的imageView的大小
        intent.putExtra("outputX", MyApplication.SCREEN_WIDTH);
        intent.putExtra("outputY", MyApplication.SCREEN_HEIGHT);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        //是否返回bitmap对象
        intent.putExtra("return-data", false);
        Uri tempUri = Uri.parse(("file:///" + path));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片的格式
        intent.putExtra("noFaceDetection", true); // 头像识别
        startActivityForResult(intent, CROP_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CROP_PICTURE) {
            if(wallperType == 0){
                setLockWallper();
            }else{
                setDesktopWallper();
            }
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}

package tl.pojul.com.fastim.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.pojul.objectsocket.constant.StorageType;
import com.pojul.objectsocket.utils.FileClassUtil;

import java.io.File;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.LoadingDialog;
import tl.pojul.com.fastim.View.widget.ShapedImageView;

/**
 * Created by gqb on 2018/5/30.
 */

public class DialogUtil {

    private static DialogUtil mDialogUtil;
    private LoadingDialog mLoadingDialog;
    private Dialog dialog;
    public boolean isImgDetailAniFin = true;

    public static DialogUtil getInstance() {
        if(mDialogUtil == null) {
            synchronized (DialogUtil.class) {
                if(mDialogUtil == null) {
                    mDialogUtil = new DialogUtil();
                }
            }
        }
        return mDialogUtil;
    }

    public void showLoadingDialog(Context ct, String msg){
        if(mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
        LoadingDialog.Builder builder1=new LoadingDialog.Builder(ct)
                .setMessage(msg)
                .setCancelable(false);
        mLoadingDialog=builder1.create();
        mLoadingDialog.show();
    }

    public void dimissLoadingDialog(){
        if(mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
    }

    /*public void CreateDetailImgDialog(Context context, String path, int storageType, float x, float y, int width, int height, Drawable drawable) {
        dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_detail_img, null);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//去背景
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        dialog.setContentView(dialogView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Window dialogWindow = dialog.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() *1.0); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
        dialog.show();

        PhotoView photoView = dialogView.findViewById(R.id.detail_img);
        RequestOptions options = new RequestOptions();
        options.placeholder(drawable)
                .error(R.drawable.pic)
                .fallback(R.drawable.pic);
        if (storageType == StorageType.LOCAL) {
            File file = new File(path);
            Glide.with(context).load(file).apply(options).into(photoView);
        } else {
            Glide.with(context).load(path).apply(options).into(photoView);
        }
        photoView.enable();

    }*/

    public void showDetailImgDialogPop(Context context, String path, int storageType ,ImageView rawView, String rawPath){
        isImgDetailAniFin = false;
        ImageView photoView = (ImageView) LayoutInflater.from(context).inflate(R.layout.dialog_detail_img, null);
        PopupWindow popUpWin1 = new PopupWindow(photoView, MyApplication.SCREEN_WIDTH, MyApplication.SCREEN_HEIGHT);
        popUpWin1.setBackgroundDrawable(new BitmapDrawable());
        popUpWin1.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popUpWin1.setOutsideTouchable(true);
        if(rawPath != null){
            RequestOptions options = new RequestOptions();
            options.error(R.drawable.pic);
            if(FileClassUtil.isHttpUrl(rawPath)){
                Glide.with(context).load(rawPath).apply(options).into(photoView);
            }else{
                File file = new File(rawPath);
                Glide.with(context).load(file).apply(options).into(photoView);
            }
        }else{
            Glide.with(context).load(rawView.getDrawable()).into(photoView);
        }

        popUpWin1.showAtLocation(rawView, Gravity.CENTER ,0, 0);

        int[] positions = new int[2];
        rawView.getLocationInWindow(positions);
        ViewGroup.LayoutParams layoutParams = photoView.getLayoutParams();
        if(rawView instanceof ShapedImageView){
            layoutParams.width = ((ShapedImageView) rawView).drawableWidth;
            layoutParams.height = ((ShapedImageView) rawView).drawableHeight;
            photoView.setX((positions[0] + (rawView.getWidth() -layoutParams.width)*0.5f));
            photoView.setY((positions[1] - getStatusBarHeight(context) + (rawView.getHeight() -layoutParams.height)*0.5f));
        }else{
            layoutParams.width = rawView.getWidth();
            layoutParams.height = rawView.getHeight();
            photoView.setX(positions[0]);
            photoView.setY((positions[1] - getStatusBarHeight(context)));
        }
        photoView.setLayoutParams(layoutParams);

        PhotoView photoView2 = (PhotoView) LayoutInflater.from(context).inflate(R.layout.dialog_detail_photoview, null);
        PopupWindow popUpWin2 = new PopupWindow(photoView2, MyApplication.SCREEN_WIDTH, MyApplication.SCREEN_HEIGHT);
        popUpWin2.setBackgroundDrawable(new BitmapDrawable());
        popUpWin2.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popUpWin2.setOutsideTouchable(true);
        popUpWin2.setAnimationStyle(R.style.showPopupAnimation2);
        photoView2.enable();

        startAnimator(photoView, popUpWin1, popUpWin2, rawView, context, path, photoView2, storageType);
    }


    private void startAnimator(ImageView photoView, PopupWindow popUpWin1, PopupWindow popUpWin2,
                               ImageView rawView, Context context, String path, PhotoView photoView2, int storageType) {
        ViewGroup.LayoutParams layoutParams = photoView.getLayoutParams();
        float rawX = photoView.getX();
        float rawY = photoView.getY();

        float rawWidth = layoutParams.width;
        float rawHeight = layoutParams.height;

        float scalex = MyApplication.SCREEN_WIDTH *1.0f / rawWidth;
        float scaley = MyApplication.SCREEN_HEIGHT *1.0f / rawHeight;

        float dscale;
        float dy;
        float dx;
        if(scalex <= scaley){
            dscale = scalex - 1;
            dx =  - photoView.getX();
            dy = - photoView.getY() + (scaley - scalex) * rawHeight *0.5f;
        }else{
            dscale = scaley - 1;
            dx =  - photoView.getX() + (scalex - scaley) * rawWidth *0.5f;
            dy = - photoView.getY();
        }
        photoView.setPivotX(0);
        photoView.setPivotY(0);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(380);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                photoView.setScaleX((1 +  dscale * animatedValue));
                photoView.setScaleY((1 +  dscale * animatedValue));
                photoView.setTranslationX((rawX + dx * animatedValue));
                photoView.setTranslationY((rawY + dy * animatedValue));
                if(animatedValue >= 1){
                    isImgDetailAniFin = true;
                    RequestOptions options = new RequestOptions();
                    options.placeholder(photoView.getDrawable())
                            .error(photoView.getDrawable())
                            .fallback(photoView.getDrawable());
                    if (storageType == StorageType.LOCAL) {
                        File file = new File(path);
                        Glide.with(context).load(file).apply(options).into(photoView2);
                    } else {
                        Glide.with(context).load(path).apply(options).into(photoView2);
                    }
                    popUpWin2.showAtLocation(rawView.getRootView(), Gravity.CENTER ,0, 0);
                    new Handler(Looper.getMainLooper()).postDelayed(()->{
                        if(popUpWin1 != null){
                            popUpWin1.dismiss();
                        }
                    }, 55);
                }
            }
        });
        animator.start();
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void dimissDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

}

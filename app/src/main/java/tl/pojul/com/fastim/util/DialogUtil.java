package tl.pojul.com.fastim.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.NetPicMessage;
import com.pojul.fastIM.message.chat.PicMessage;
import com.pojul.fastIM.message.chat.VideoMessage;
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
    private DialogClick dialogClick;
    private boolean isAnim;

    public static final int POP_TYPR_IMG = 1;
    public static final int POP_TYPR_VIDEO = 2;

    private final static String TAG = "DialogUtil";

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

    public void showDetailImgDialogPop(Context context, ChatMessage message , ImageView rawView, int popType){
        if(isAnim){
            return;
        }
        isAnim = true;
        ImageView photoView = (ImageView) LayoutInflater.from(context).inflate(R.layout.dialog_detail_img, null);
        PopupWindow popUpWin1 = new PopupWindow(photoView, MyApplication.SCREEN_WIDTH, MyApplication.SCREEN_HEIGHT);
        popUpWin1.setBackgroundDrawable(new BitmapDrawable());
        popUpWin1.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popUpWin1.setOutsideTouchable(true);
        String rawPath = "";
        String path = "";
        if(message instanceof PicMessage){
            rawPath = ((PicMessage)message).getPic().getFilePath();
            path = rawPath;
        }else if(message instanceof NetPicMessage){
            rawPath = ((NetPicMessage)message).getThumbURL().getFilePath();
            if(((NetPicMessage)message).getFullURL() == null){
                path = rawPath;
            }else{
                path = ((NetPicMessage)message).getFullURL().getFilePath();
            }
        } else if(message instanceof VideoMessage){
            rawPath = ((VideoMessage)message).getFirstPic().getFilePath();
        }
        if(rawPath != null){
            RequestOptions options = new RequestOptions();
            options.error(R.drawable.pic);
            if(FileClassUtil.isHttpUrl(rawPath)){
                String finalPath = path;
                Glide.with(context).load(rawPath).thumbnail(0.1f).apply(options).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //加载失败 移除监听
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //成功 移除监
                        if(isFirstResource){
                            return false;
                        }
                        switch (popType) {
                            case POP_TYPR_IMG:
                                showDetailImgPop(popUpWin1, photoView, context, finalPath, rawView);
                                break;
                            case POP_TYPR_VIDEO:
                                showDetailVideoPop(popUpWin1, photoView, context, (VideoMessage) message, rawView);
                                break;
                        }
                        return false;
                    }
                }).into(photoView);
            }else{
                File file = new File(rawPath);
                Glide.with(context).load(file).thumbnail(0.1f).apply(options).into(photoView);
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

        if(rawPath == null || !FileClassUtil.isHttpUrl(rawPath)) {
            switch (popType) {
                case POP_TYPR_IMG:
                    showDetailImgPop(popUpWin1, photoView, context, path, rawView);
                    break;
                case POP_TYPR_VIDEO:
                    showDetailVideoPop(popUpWin1, photoView, context, (VideoMessage) message, rawView);
                    break;
            }
        }
        //startAnimator(photoView, popUpWin1, popUpWin2, rawView, context, path, photoView2);
    }

    private void showDetailVideoPop(PopupWindow popUpWin1, ImageView photoView, Context context, VideoMessage message, ImageView rawView) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.dialog_detail_video, null);
        PopupWindow popUpWin2 = new PopupWindow(relativeLayout, MyApplication.SCREEN_WIDTH, MyApplication.SCREEN_HEIGHT);
        popUpWin2.setBackgroundDrawable(new BitmapDrawable());
        popUpWin2.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popUpWin2.setOutsideTouchable(true);
        popUpWin2.setAnimationStyle(R.style.showPopupAnimation2);

        ProgressBar process = relativeLayout.findViewById(R.id.process);
        ImageView play = relativeLayout.findViewById(R.id.play);
        ImageView img = relativeLayout.findViewById(R.id.img);
        VideoView video = relativeLayout.findViewById(R.id.video);
        img.setVisibility(View.GONE);
        //video.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        process.setVisibility(View.GONE);

        AnimatorUtil.startPopObjAnimator(photoView, new AnimatorUtil.AnimatorListener(){
            @Override
            public void onFinished() {
                if(message.getFirstPic() != null){
                    popUpWin2.showAtLocation(rawView.getRootView(), Gravity.CENTER ,0, 0);
                    new Handler(Looper.getMainLooper()).postDelayed(()->{
                        if(popUpWin1 != null){
                            popUpWin1.dismiss();
                        }
                        dialog.show();
                    }, 555);

                    RequestOptions options = new RequestOptions();
                    options.placeholder(photoView.getDrawable())
                            .error(photoView.getDrawable())
                            .fallback(photoView.getDrawable());
                    if (!FileClassUtil.isHttpUrl(message.getFirstPic().getFilePath())) {
                        File file = new File(message.getFirstPic().getFilePath());
                        Glide.with(context).load(file).apply(options).into(img);
                    } else {
                        Glide.with(context).load(message.getFirstPic().getFilePath()).apply(options).into(img);
                    }
                }
                video.setVideoPath(message.getVideo().getFilePath());
                MediaController controller = new MediaController(context);
                video.setMediaController(controller);
                controller.setMediaPlayer(video);
                video.start();
                isAnim = false;
            }
        });
    }

    private void showDetailImgPop(PopupWindow popUpWin1, ImageView photoView, Context context, String path, ImageView rawView) {
        PhotoView photoView2 = (PhotoView) LayoutInflater.from(context).inflate(R.layout.dialog_detail_photoview, null);
        PopupWindow popUpWin2 = new PopupWindow(photoView2, MyApplication.SCREEN_WIDTH, MyApplication.SCREEN_HEIGHT);
        popUpWin2.setBackgroundDrawable(new BitmapDrawable());
        popUpWin2.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popUpWin2.setOutsideTouchable(true);
        popUpWin2.setAnimationStyle(R.style.showPopupAnimation2);
        photoView2.enable();

        AnimatorUtil.startPopObjAnimator(photoView, new AnimatorUtil.AnimatorListener() {
            @Override
            public void onFinished() {
                RequestOptions options = new RequestOptions();
                options.placeholder(photoView.getDrawable())
                        .error(photoView.getDrawable())
                        .fallback(photoView.getDrawable());
                if (!FileClassUtil.isHttpUrl(path)) {
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
                isAnim = false;
            }
        });
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void showNoteDialog(Context context, String title, String content){
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle(title);
        normalDialog.setMessage(content);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    public void showPromptDialog(Context context, String title, String content){
        AlertDialog.Builder promptDialogBuilder = new AlertDialog.Builder(context);
        View view = View .inflate(context, R.layout.dialog_prompt, null);
        ((TextView)view.findViewById(R.id.prompt_title)).setText(title);
        ((TextView)view.findViewById(R.id.prompt_content)).setText(content);
        promptDialogBuilder.setView(view);
        AlertDialog promptDialog = promptDialogBuilder.create();
        view.findViewById(R.id.left_button).setOnClickListener(v ->{
            if(dialogClick != null){
                dialogClick.onclick("确定");
            }
            promptDialog.dismiss();
        });
        view.findViewById(R.id.right_button).setOnClickListener(v ->{
            if(dialogClick != null){
                dialogClick.onclick("取消");
            }
            promptDialog.dismiss();
        });
        promptDialog.show();
    }

    public void showSaveWhiteBlackDialog(Context context, String type){
        String typeName;
        if("white".equals(type)){
            typeName = "白名单";
        }else{
            typeName = "黑名单";
        }
        AlertDialog.Builder promptDialogBuilder = new AlertDialog.Builder(context);
        View view = View .inflate(context, R.layout.dialog_save_whiteblack, null);
        ((TextView)view.findViewById(R.id.prompt_title)).setText(("保存" + typeName));
        ((TextView)view.findViewById(R.id.name_note)).setText((typeName + "名："));
        EditText nameEt = view.findViewById(R.id.name);
        promptDialogBuilder.setView(view);
        AlertDialog promptDialog = promptDialogBuilder.create();
        view.findViewById(R.id.left_button).setOnClickListener(v ->{
            if(dialogClick != null){
                String name = nameEt.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(context, ("name_note" + "名不能为空"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(SPUtil.getInstance().containWhiteBlack(name)){
                    Toast.makeText(context, "该名称已存在黑白名单中，请选取其他名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogClick.onclick(name);
            }
            promptDialog.dismiss();
        });
        view.findViewById(R.id.right_button).setOnClickListener(v ->{
            if(dialogClick != null){
                dialogClick.onclick("取消");
            }
            promptDialog.dismiss();
        });
        promptDialog.show();
    }


    public void setDialogClick(DialogClick dialogClick) {
        this.dialogClick = dialogClick;
    }

    public void dimissDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

    public interface DialogClick{
        void onclick(String str);
    }

}

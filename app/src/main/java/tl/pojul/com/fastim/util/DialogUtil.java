package tl.pojul.com.fastim.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.pojul.fastIM.entity.MessageFilter;
import com.pojul.fastIM.entity.PicFilter;
import com.pojul.fastIM.entity.UserFilter;
import com.pojul.fastIM.entity.UserSelectFilter;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.NetPicMessage;
import com.pojul.fastIM.message.chat.PicMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.fastIM.message.chat.VideoMessage;
import com.pojul.objectsocket.utils.FileClassUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.PopListAdapter;
import tl.pojul.com.fastim.View.widget.LoadingDialog;
import tl.pojul.com.fastim.View.widget.MessageFilterView;
import tl.pojul.com.fastim.View.widget.PicFilterView;
import tl.pojul.com.fastim.View.widget.ReportView;
import tl.pojul.com.fastim.View.widget.ShapedImageView;
import tl.pojul.com.fastim.View.widget.UserFilterView;
import tl.pojul.com.fastim.View.widget.UserSelectView;

public class DialogUtil {

    private static DialogUtil mDialogUtil;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.ok)
    TextView ok;
    private LoadingDialog mLoadingDialog;
    private Dialog dialog;
    private DialogClick dialogClick;
    //private boolean isAnim;
    private ScreenPopClick screenPopClick;

    public static final int POP_TYPR_IMG = 1;
    public static final int POP_TYPR_VIDEO = 2;

    private final static String TAG = "DialogUtil";
    private PopupWindow screenPop;
    private PopupWindow loadingSimplePop;
    private PicFilterClick picFilterClick;
    private PopupWindow picFilterPop;
    private PopupWindow userSelectFilterPop;
    private UserSelectFilterPopClick userSelectFilterPopClick;
    public AlertDialog suggestDialog;

    public static DialogUtil getInstance() {
        if (mDialogUtil == null) {
            synchronized (DialogUtil.class) {
                if (mDialogUtil == null) {
                    mDialogUtil = new DialogUtil();
                }
            }
        }
        return mDialogUtil;
    }

    public void showLoadingDialog(Context ct, String msg) {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        LoadingDialog.Builder builder1 = new LoadingDialog.Builder(ct)
                .setMessage(msg)
                .setCancelable(false);
        mLoadingDialog = builder1.create();
        mLoadingDialog.show();
    }

    public void dimissLoadingDialog() {
        try{
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
            if (loadingSimplePop != null) {
                loadingSimplePop.dismiss();
            }
        }catch(Exception e){}
    }

    public boolean isShowLoadingDialog(){
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            return true;
        }
        if(loadingSimplePop != null && loadingSimplePop.isShowing()){
            return true;
        }
        return false;
    }

    public void showLoadingSimple(Context context, View rootView) {
        showLoadingDialog(context, "加载中");
        /*try{
            View popView = LayoutInflater.from(context).inflate(R.layout.dialog_loading_simple, null);
            loadingSimplePop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            loadingSimplePop.setBackgroundDrawable(new BitmapDrawable());
            loadingSimplePop.setFocusable(false);
            // 设置可以触摸弹出框以外的区域
            loadingSimplePop.setOutsideTouchable(false);
            loadingSimplePop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        }catch(Exception e){}*/
    }

    public void showDetailImgDialogPop(Context context, ChatMessage message, ImageView rawView, int popType) {
        try {
            /*if (isAnim) {
                return;
            }
            isAnim = true;*/
            ImageView photoView = (ImageView) LayoutInflater.from(context).inflate(R.layout.dialog_detail_img, null);
            PopupWindow popUpWin1 = new PopupWindow(photoView, MyApplication.SCREEN_WIDTH, MyApplication.SCREEN_HEIGHT);
            popUpWin1.setBackgroundDrawable(new BitmapDrawable());
            popUpWin1.setFocusable(true);
            // 设置可以触摸弹出框以外的区域
            popUpWin1.setOutsideTouchable(true);
            String rawPath = "";
            String path = "";
            if (message instanceof PicMessage) {
                rawPath = ((PicMessage) message).getPic().getFilePath();
                path = rawPath;
            } else if (message instanceof NetPicMessage) {
                rawPath = ((NetPicMessage) message).getThumbURL().getFilePath();
                if (((NetPicMessage) message).getFullURL() == null) {
                    path = rawPath;
                } else {
                    path = ((NetPicMessage) message).getFullURL().getFilePath();
                }
            } else if (message instanceof VideoMessage) {
                rawPath = ((VideoMessage) message).getFirstPic().getFilePath();
            }
            if (rawPath != null) {
                RequestOptions options = new RequestOptions();
                options.error(R.drawable.pic);
                if (FileClassUtil.isHttpUrl(rawPath)) {
                    String finalPath = path;
                    Glide.with(context).load(rawPath).apply(options).listener(new RequestListener<Drawable>() {

                        boolean hasExecute = false;

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //加载失败 移除监听
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //成功 移除监
                            if (hasExecute) {
                                return false;
                            }
                            hasExecute = true;
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
                } else {
                    File file = new File(rawPath);
                    Glide.with(context).load(file).apply(options).into(photoView);
                }
            } else {
                Glide.with(context).load(rawView.getDrawable()).into(photoView);
            }
            popUpWin1.showAtLocation(rawView, Gravity.CENTER, 0, 0);

            int[] positions = new int[2];
            rawView.getLocationInWindow(positions);
            ViewGroup.LayoutParams layoutParams = photoView.getLayoutParams();
            if (rawView instanceof ShapedImageView) {
                layoutParams.width = ((ShapedImageView) rawView).drawableWidth;
                layoutParams.height = ((ShapedImageView) rawView).drawableHeight;
                photoView.setX((positions[0] + (rawView.getWidth() - layoutParams.width) * 0.5f));
                photoView.setY((positions[1] - getStatusBarHeight(context) + (rawView.getHeight() - layoutParams.height) * 0.5f));
            } else {
                layoutParams.width = rawView.getWidth();
                layoutParams.height = rawView.getHeight();
                photoView.setX(positions[0]);
                photoView.setY((positions[1] - getStatusBarHeight(context)));
            }
            photoView.setLayoutParams(layoutParams);

            if (rawPath == null || !FileClassUtil.isHttpUrl(rawPath)) {
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
        } catch (Exception e) {
            //isAnim = false;
        }
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

        AnimatorUtil.startPopObjAnimator(photoView, () -> {
            if (message.getFirstPic() != null) {
                //isAnim = false;
                popUpWin2.showAtLocation(rawView.getRootView(), Gravity.CENTER, 0, 0);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (popUpWin1 != null) {
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

        AnimatorUtil.startPopObjAnimator(photoView, new AnimatorUtil.AnimatorListener() {
            @Override
            public void onFinished() {
                //isAnim = false;
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
                popUpWin2.showAtLocation(rawView.getRootView(), Gravity.CENTER, 0, 0);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (popUpWin1 != null) {
                        popUpWin1.dismiss();
                    }
                }, 55);
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

    public void showNoteDialog(Context context, String title, String content) {
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle(title);
        normalDialog.setMessage(content);
        normalDialog.setPositiveButton("确定",
                (dialog, which) -> {
                });
        normalDialog.setNegativeButton("关闭",
                (dialog, which) -> {
                });
        normalDialog.show();
    }

    public void showPromptDialog(Context context, String title, String content) {
        AlertDialog.Builder promptDialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_prompt, null);
        ((TextView) view.findViewById(R.id.prompt_title)).setText(title);
        ((TextView) view.findViewById(R.id.prompt_content)).setText(content);
        promptDialogBuilder.setView(view);
        AlertDialog promptDialog = promptDialogBuilder.create();
        view.findViewById(R.id.left_button).setOnClickListener(v -> {
            if (dialogClick != null) {
                dialogClick.onclick("确定");
            }
            setDialogClick(null);
            promptDialog.dismiss();
        });
        view.findViewById(R.id.right_button).setOnClickListener(v -> {
            if (dialogClick != null) {
                dialogClick.onclick("取消");
            }
            setDialogClick(null);
            promptDialog.dismiss();
        });
        promptDialog.show();
    }

    public void showSaveWhiteBlackDialog(Context context, String type) {
        String typeName;
        if ("white".equals(type)) {
            typeName = "白名单";
        } else {
            typeName = "黑名单";
        }
        AlertDialog.Builder promptDialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_save_whiteblack, null);
        ((TextView) view.findViewById(R.id.prompt_title)).setText(("保存" + typeName));
        ((TextView) view.findViewById(R.id.name_note)).setText((typeName + "名："));
        EditText nameEt = view.findViewById(R.id.name);
        promptDialogBuilder.setView(view);
        AlertDialog promptDialog = promptDialogBuilder.create();
        view.findViewById(R.id.left_button).setOnClickListener(v -> {
            if (dialogClick != null) {
                String name = nameEt.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(context, ("name_note" + "名不能为空"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (SPUtil.getInstance().containWhiteBlack(name)) {
                    Toast.makeText(context, "该名称已存在黑白名单中，请选取其他名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogClick.onclick(name);
            }
            promptDialog.dismiss();
        });
        view.findViewById(R.id.right_button).setOnClickListener(v -> {
            if (dialogClick != null) {
                dialogClick.onclick("取消");
            }
            promptDialog.dismiss();
        });
        promptDialog.show();
    }

    public void showScreenPop(Context context, View showDown, MessageFilter messageFilter, boolean hideEffective) {
        LinearLayout popView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_screen_pop, null);
        screenPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        screenPop.setBackgroundDrawable(new BitmapDrawable());
        screenPop.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        screenPop.setOutsideTouchable(false);
        MessageFilterView messageFilterView = popView.findViewById(R.id.message_filter_view);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            messageFilterView.setFilter(messageFilter);
            if(hideEffective){
                messageFilterView.hideEffective();
            }
        }, 100);
        popView.findViewById(R.id.ok).setOnClickListener(v -> {
            if (screenPopClick != null) {
                screenPopClick.onclick(messageFilterView.getFilter());
            }
            screenPop.dismiss();
        });
        popView.findViewById(R.id.cancel).setOnClickListener(v -> {
            //messageFilterView.resetFilter();
            screenPop.dismiss();
        });
        screenPop.showAsDropDown(showDown, 0, 0);
    }

    public boolean isScreenPopShow() {
        if (screenPop != null && screenPop.isShowing()) {
            return true;
        }
        return false;
    }

    public void showReportDialog(Context context, TagCommuMessage tagCommuMessageg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_report, null);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        ReportView reportView = view.findViewById(R.id.report_view);
        view.findViewById(R.id.cancel).setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
                setDialogClick(null);
            }
        });
        view.findViewById(R.id.ok).setOnClickListener(v -> {
            reportView.requsetReport(tagCommuMessageg, result -> {
                if ("sucesses".equals(result) && dialogClick != null) {
                    dialogClick.onclick("report_sucesses");
                    if (dialog != null) {
                        dialog.dismiss();
                        setDialogClick(null);
                    }
                }
            });
        });
    }

    public void showSuggestDialog(Context context){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_suggest, null);
        EditText editText = view.findViewById(R.id.content);
        dialogBuilder.setView(view);
        suggestDialog = dialogBuilder.create();
        suggestDialog.show();
        view.findViewById(R.id.left_button).setOnClickListener(v->{
            if(editText.getText().toString().isEmpty()){
                Toast.makeText(context, "内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if(dialogClick != null){
                dialogClick.onclick(editText.getText().toString());
            }
        });

        view.findViewById(R.id.right_button).setOnClickListener(v->{
            setDialogClick(null);
            suggestDialog.dismiss();
        });
    }

    public void dimissSuggestDialog(){
        if(suggestDialog != null && suggestDialog.isShowing()){
            setDialogClick(null);
            suggestDialog.dismiss();
        }
    }

    public void showSubReplyDialog(Context context, View view, String nickName) {
        View popView = LayoutInflater.from(context).inflate(R.layout.dialog_subreply, null);
        PopupWindow subReplyPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subReplyPop.setBackgroundDrawable(new BitmapDrawable());
        subReplyPop.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        subReplyPop.setOutsideTouchable(false);
        subReplyPop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        EditText subReplyText = popView.findViewById(R.id.subreply_text);
        subReplyText.setHint(("回复" + nickName));
        popView.findViewById(R.id.ok).setOnClickListener(v -> {
            if (subReplyText.getText().toString().isEmpty()) {
                Toast.makeText(context, "回复内容为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dialogClick != null) {
                dialogClick.onclick(subReplyText.getText().toString());
            }
            setDialogClick(null);
            subReplyPop.dismiss();
        });
        popView.findViewById(R.id.cancel).setOnClickListener(v -> {
            setDialogClick(null);
            subReplyPop.dismiss();
        });
    }

    public void showEditDialog(Context context, String title, String note){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_edit, null);
        ((TextView)view.findViewById(R.id.title)).setText(title);
        ((TextView)view.findViewById(R.id.note)).setText(note);
        EditText editText = view.findViewById(R.id.content);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        view.findViewById(R.id.left_button).setOnClickListener(v->{
            if(editText.getText().toString().isEmpty()){
                Toast.makeText(context, "内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if(dialogClick != null){
                dialogClick.onclick(editText.getText().toString());
                setDialogClick(null);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.right_button).setOnClickListener(v->{
            setDialogClick(null);
            dialog.dismiss();
        });
    }

    public void showEditCommuDetail(Context context){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_edit_commudeatil, null);
        EditText editText = view.findViewById(R.id.content);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        view.findViewById(R.id.left_button).setOnClickListener(v->{
            if(editText.getText().toString().isEmpty()){
                Toast.makeText(context, "内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if(dialogClick != null){
                dialogClick.onclick(editText.getText().toString());
                setDialogClick(null);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.right_button).setOnClickListener(v->{
            setDialogClick(null);
            dialog.dismiss();
        });
    }

    public void showEditDialog(Context context, String title, String note, int maxLen){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_edit, null);
        ((TextView)view.findViewById(R.id.title)).setText(title);
        ((TextView)view.findViewById(R.id.note)).setText(note);
        EditText editText = view.findViewById(R.id.content);
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLen)};
        editText.setFilters(filters);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        view.findViewById(R.id.left_button).setOnClickListener(v->{
            if(editText.getText().toString().isEmpty()){
                Toast.makeText(context, "内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if(dialogClick != null){
                dialogClick.onclick(editText.getText().toString());
                setDialogClick(null);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.right_button).setOnClickListener(v->{
            setDialogClick(null);
            dialog.dismiss();
        });
    }

    public void showPicFilterDialog(Context context, View showDown, PicFilter picFilter,
                                    boolean hideLabel, boolean enableThirdGally) {
        LinearLayout popView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_pic_filter, null);
        picFilterPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        picFilterPop.setBackgroundDrawable(new BitmapDrawable());
        picFilterPop.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        picFilterPop.setOutsideTouchable(false);
        PicFilterView picFilterView = popView.findViewById(R.id.pic_filter_view);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            picFilterView.setFilter(picFilter, hideLabel);
            if(!enableThirdGally){
                picFilterView.disableThirdGally();
            }
        }, 100);
        popView.findViewById(R.id.ok).setOnClickListener(v -> {
            if (picFilterClick != null) {
                picFilterClick.onclick(picFilterView.getFilter());
                setPicFilterClick(null);
            }
            picFilterPop.dismiss();
        });
        popView.findViewById(R.id.cancel).setOnClickListener(v -> {
            //messageFilterView.resetFilter();
            setPicFilterClick(null);
            picFilterPop.dismiss();
        });
        picFilterPop.showAsDropDown(showDown, 0, 0);
    }

    public boolean ispicFilterShow() {
        if (picFilterPop != null && picFilterPop.isShowing()) {
            return true;
        }
        return false;
    }

    public void showUserFilterDialog(Context context, View showDown, UserSelectFilter filter){
        LinearLayout popView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_user_select, null);
        userSelectFilterPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        userSelectFilterPop.setBackgroundDrawable(new BitmapDrawable());
        userSelectFilterPop.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        userSelectFilterPop.setOutsideTouchable(false);
        UserSelectView userSelectView = popView.findViewById(R.id.user_select_view);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            userSelectView.setUserSelectFilter(filter);
        }, 100);
        popView.findViewById(R.id.ok).setOnClickListener(v -> {
            if (userSelectFilterPopClick != null) {
                userSelectFilterPopClick.onclick(userSelectView.getFilter());
            }
            setUserSelectFilterPopClick(null);
            userSelectFilterPop.dismiss();
        });
        popView.findViewById(R.id.cancel).setOnClickListener(v -> {
            setUserSelectFilterPopClick(null);
            userSelectFilterPop.dismiss();
        });
        userSelectFilterPop.showAsDropDown(showDown, 0, 0);
    }

    public boolean isUserSelectFilterPopShow() {
        if (userSelectFilterPop != null && userSelectFilterPop.isShowing()) {
            return true;
        }
        return false;
    }


    public void showAddFriendDialog(Context context, String titleStr){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_add_friend_says, null);
        EditText editText = view.findViewById(R.id.content);
        TextView title = view.findViewById(R.id.title);
        title.setText(titleStr);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        view.findViewById(R.id.left_button).setOnClickListener(v->{
            if(dialogClick != null){
                dialogClick.onclick(editText.getText().toString());
                setDialogClick(null);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.right_button).setOnClickListener(v->{
            setDialogClick(null);
            dialog.dismiss();
        });
    }

    public void showBirthdayTypeDialog(Context context){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_birthday_type, null);
        RadioButton lunar = view.findViewById(R.id.birthday_lunar);
        RadioButton gregorian = view.findViewById(R.id.birthday_gregorian);
        lunar.setChecked(true);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        view.findViewById(R.id.left_button).setOnClickListener(v->{
            if(dialogClick != null){
                if(lunar.isChecked()){
                    dialogClick.onclick("农历");
                }else{
                    dialogClick.onclick("阳历");
                }
                setDialogClick(null);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.right_button).setOnClickListener(v->{
            setDialogClick(null);
            dialog.dismiss();
        });
    }

    public void showSexDialog(Context context){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_sex, null);
        RadioButton man = view.findViewById(R.id.sex_man);
        RadioButton woman = view.findViewById(R.id.sex_woman);
        man.setChecked(true);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        view.findViewById(R.id.left_button).setOnClickListener(v->{
            if(dialogClick != null){
                if(man.isChecked()){
                    dialogClick.onclick("男生");
                }else{
                    dialogClick.onclick("女生");
                }
                setDialogClick(null);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.right_button).setOnClickListener(v->{
            setDialogClick(null);
            dialog.dismiss();
        });
    }

    public void showKeyguardGalleryDialog(Context context, long validStatus){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_keyguard_gallery, null);
        Button gotoPay = view.findViewById(R.id.goto_pay);
        Button experience = view.findViewById(R.id.experience);
        if(validStatus == 1){
            experience.setVisibility(View.GONE);
            view.findViewById(R.id.line2).setVisibility(View.GONE);
        }
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        view.findViewById(R.id.close).setOnClickListener(v->{
            setDialogClick(null);
            dialog.dismiss();
        });
        gotoPay.setOnClickListener(v->{
            if(dialogClick != null){
                dialogClick.onclick("gotopay");
            }
            setDialogClick(null);
            dialog.dismiss();
        });
        experience.setOnClickListener(v->{
            if(dialogClick != null){
                dialogClick.onclick("experience");
            }
            setDialogClick(null);
            dialog.dismiss();
        });
    }

    public void showGalleryMorePop(Context context, View showDown){
        LinearLayout popView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_gallery_morepop, null);
        PopupWindow popupWindow = new PopupWindow(popView, DensityUtil.dp2px(context, 120),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAsDropDown(showDown, 0, -DensityUtil.dp2px(context, 6));
        popView.findViewById(R.id.save).setOnClickListener(v->{
            if(dialogClick != null){
                dialogClick.onclick("save");
            }
            dialogClick = null;
            popupWindow.dismiss();
        });
        popView.findViewById(R.id.share).setOnClickListener(v->{
            if(dialogClick != null){
                dialogClick.onclick("share");
            }
            dialogClick = null;
            popupWindow.dismiss();
        });
        /*popView.findViewById(R.id.lock_wallper).setOnClickListener(v->{
            if(dialogClick != null){
                dialogClick.onclick("lock_wallper");
            }
            dialogClick = null;
            popupWindow.dismiss();
        });
        popView.findViewById(R.id.desktop_wallper).setOnClickListener(v->{
            if(dialogClick != null){
                dialogClick.onclick("desktop_wallper");
            }
            dialogClick = null;
            popupWindow.dismiss();
        });*/
    }

    public void showAdminInfo(Context context, String account, String passwd){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_admin_info, null);
        ((TextView)view.findViewById(R.id.account)).setText("账号：" + account);
        ((TextView)view.findViewById(R.id.passwd)).setText("密码：" + passwd);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
        view.findViewById(R.id.remembered).setOnClickListener(v->{
            DialogUtil.getInstance().showLoadingDialog(context, "写入文件中...");
            String path= (SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH ) +
                    "/footstep/file/account.txt");
            File file = new File(path);
            if(file.exists()){
                file.delete();
            }
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            try {
                fos=new FileOutputStream(file);
                writer=new OutputStreamWriter(fos,"utf-8");
                String content = "账号：" + account + "\n密码：" + passwd;
                writer.write(content);
                dialog.dismiss();
                Toast.makeText(context, "账号信息已被保存至" + path + "下", Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Toast.makeText(context, "文件写入失败", Toast.LENGTH_SHORT).show();
            }finally {
                DialogUtil.getInstance().dimissLoadingDialog();
                try{
                    writer.close();
                    fos.close();
                }catch(Exception e){}
            }
        });
    }

    public void showPopList(Context context, List<String> datas, View downAt){
        RecyclerView popView = (RecyclerView) LayoutInflater.from(context).inflate(R.layout.dialog_pop_list, null);
        PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popupWindow.setOutsideTouchable(false);
        popupWindow.setWidth(DensityUtil.dp2px(context, 70));
        popView.setLayoutManager(new LinearLayoutManager(context));
        PopListAdapter adapter = new PopListAdapter(context, datas, position -> {
            if(dialogClick != null){
                dialogClick.onclick(datas.get(position));
            }
            popupWindow.dismiss();
        });
        popView.setAdapter(adapter);
        popupWindow.showAsDropDown(downAt, -10, 5);
    }

    public void showGuideDialog(Context context){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_guide, null);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
        view.findViewById(R.id.knows).setOnClickListener(v->{
            dimissDialog();
            SPUtil.getInstance().putInt(SPUtil.SHOW_SCREEN_GUIDE, 1);
        });
        view.findViewById(R.id.gotoopen).setOnClickListener(v->{
            DeviceUtil.toSelfSetting(MyApplication.getApplication());
            SPUtil.getInstance().putInt(SPUtil.SHOW_SCREEN_GUIDE, 1);
        });

    }

    public void setDialogClick(DialogClick dialogClick) {
        this.dialogClick = dialogClick;
    }

    public void dimissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void dimissScreenPop() {
        if (screenPop != null && screenPop.isShowing()) {
            screenPop.dismiss();
        }
    }

    public ScreenPopClick getScreenPopClick() {
        return screenPopClick;
    }

    public void setScreenPopClick(ScreenPopClick screenPopClick) {
        this.screenPopClick = screenPopClick;
    }

    public void setUserSelectFilterPopClick(UserSelectFilterPopClick userSelectFilterPopClick) {
        this.userSelectFilterPopClick = userSelectFilterPopClick;
    }

    public void setPicFilterClick(PicFilterClick picFilterClick) {
        this.picFilterClick = picFilterClick;
    }
    
    public interface DialogClick {
        void onclick(String str);
    }

    public interface ScreenPopClick {
        void onclick(MessageFilter messageFilter);
    }

    public interface PicFilterClick {
        void onclick(PicFilter picFilter);
    }

    public interface UserSelectFilterPopClick {
        void onclick(UserSelectFilter userSelectFilter);
    }

}

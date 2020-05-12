package tl.pojul.com.fastim.View.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pojul.fastIM.entity.ExtendUploadPic;
import com.pojul.fastIM.entity.PicFilter;
import com.pojul.fastIM.entity.ScreenPosition;
import com.pojul.objectsocket.utils.LogUtil;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.LockScreenImg;
import tl.pojul.com.fastim.View.widget.PicFilterView;
import tl.pojul.com.fastim.dao.LockScreenDao;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.transfer.lockscreenimg.AlphaTransfer;
import tl.pojul.com.fastim.util.AnimatorUtil;
import tl.pojul.com.fastim.util.BitmapUtil;
import tl.pojul.com.fastim.util.CustomTimeDown;
import tl.pojul.com.fastim.util.DensityUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.FlashLightUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.SPUtil;
import tl.pojul.com.fastim.util.ShellUtils;

public class LockScreenActivity extends BaseActivity implements CustomTimeDown.OnTimeDownListener {

    @BindView(R.id.wallaper)
    LockScreenImg wallaper;
    @BindView(R.id.unlock)
    ShimmerTextView unlock;
    @BindView(R.id.root_view)
    RelativeLayout rootView;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.time_clock)
    TextView timeClock;
    @BindView(R.id.pre_pics)
    ImageView prePics;
    @BindView(R.id.pre_pic)
    ImageView prePic;
    @BindView(R.id.like)
    ImageView like;
    @BindView(R.id.collect)
    ImageView collect;
    @BindView(R.id.next_pic)
    ImageView nextPic;
    @BindView(R.id.next_pics)
    ImageView nextPics;
    @BindView(R.id.operate_ll)
    LinearLayout operateLl;
    @BindView(R.id.camera)
    ImageView camera;
    @BindView(R.id.flash_light)
    ImageView flashLight;
    @BindView(R.id.bottom)
    RelativeLayout bottom;
    @BindView(R.id.filter)
    ImageView filter;
    @BindView(R.id.center)
    View center;

    PicFilterView picFilterView;
    TextView cancel;
    TextView ok;
    LinearLayout filterLl;
    @BindView(R.id.refresh)
    ImageView refresh;
    @BindView(R.id.next_ten)
    ImageView nextTen;
    @BindView(R.id.thumb_up)
    ImageView thumbUp;

    private static final String TAG = "LockScreenActivity";
    private static final int INIT = 912;
    private static final int SET_FILTER = 495;
    private CustomTimeDown customTimeDown;

    private float touchUlockX;
    private boolean isUnlockAni;
    private long lastShimmerTime;
    private boolean isFilterAni;
    private ObjectAnimator rorateAni;

    private PicFilter picFilter;
    private List<ExtendUploadPic> pics;
    private ExtendUploadPic currentPics;
    private LockScreenDao lockScreenDao;
    private int reqPage;
    private Shimmer shimmer;
    private int battery;
    private BatteryBroadCastReceiver batteryRec;
    private int reqMode = 1; //1: normal; 2: prev
    private int operateType;
    private FlashLightUtil flashLightUtil;
    private boolean isFlashOpen;

    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_lock_screen);
        ButterKnife.bind(this);

        /*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mHandler.sendEmptyMessageDelayed(INIT, 50);
    }

    private void init() {
        //GlideUtil.setImageBitmapNoOptions(urls[0], wallaper);
        wallaper.setTransfer(new AlphaTransfer());
        picFilter = SPUtil.getInstance().getLockSctreenFiler();
        lockScreenDao = new LockScreenDao();
        if(picFilter == null){
            picFilter = new PicFilter();
            picFilter.setGallery("pixabay");
        }
        pics = SPUtil.getInstance().getLastLockPics();
        if(pics == null || pics.size() <= 0 || picFilter.getIndex() >= pics.size() || pics.get(picFilter.getIndex()) == null
                || pics.get(picFilter.getIndex()).getPics() == null || pics.get(picFilter.getIndex()).getPics().size() <= picFilter.getSubIndex()){
            reqPage = picFilter.getPage();
            reqPics();
        }else{
            currentPics = pics.get(picFilter.getIndex());
            setWallper();
        }
        Log.e(TAG, "init");
        shimmer = new Shimmer();
        shimmer.setRepeatCount(3)
                .setDuration(1800)
                .setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        shimmer.start(unlock);
        unlock.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchUlockX = event.getX();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                onUnLockMove(event);
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                onUnLockUp(event);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                onUnLockUp(event);
            }
            return false;
        });

        wallaper.setCanSlideToUnlock(true);
        wallaper.setOnSlideToUnlockListener(() -> {
            LogUtil.e("setOnSlideToUnlockListener onUnlock");
            unlockScreen();
            finish();
        });
    }

    private void unlockScreen(){
        if(Build.VERSION.SDK_INT>=26){
            try{
                KeyguardManager keyguardManager = (KeyguardManager) LockScreenActivity.this.getSystemService(KEYGUARD_SERVICE);
                keyguardManager.requestDismissKeyguard(LockScreenActivity.this, new KeyguardManager.KeyguardDismissCallback() {
                    @Override
                    public void onDismissError() {
                        super.onDismissError();
                        LogUtil.e("onDismissError");
                    }

                    @Override
                    public void onDismissSucceeded() {
                        super.onDismissSucceeded();
                        LogUtil.e("onDismissSucceeded");
                    }

                    @Override
                    public void onDismissCancelled() {
                        super.onDismissCancelled();
                        LogUtil.e("onDismissCancelled");
                    }
                });
            }catch (Exception e){
                LogUtil.e(e.getMessage());
            }
        }else{
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
            //解锁
            kl.disableKeyguard();
        }
    }

    private void setWallper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(isDestroyed()){
                return;
            }
        }
        if(currentPics == null){
            nextPic.setEnabled(false);
            nextPics.setEnabled(false);
            prePics.setEnabled(false);
            prePic.setEnabled(false);
            Glide.with(this).load(R.drawable.lock_default).into(wallaper);
            return;
        }
        String path = null;
        try{
            path = pics.get(picFilter.getIndex()).getPics().get(picFilter.getSubIndex()).getUploadPicUrl().getFilePath();
        }catch(Exception e){}
        if(path == null){
            return;
        }
        SPUtil.getInstance().saveLockSctreenFiler(picFilter);
        startRefreshAni();
        if(!picFilter.isHasMore()){
            nextPic.setEnabled(false);
            nextPics.setEnabled(false);
            nextTen.setEnabled(false);
        }else{
            nextTen.setEnabled(true);
            if(currentPics.getPics().size() <= 1){
                nextPics.setEnabled(false);
            }else{
                nextPics.setEnabled(true);
            }
            nextPic.setEnabled(true);
        }
        if(currentPics.getPics().size() <= 1){
            prePics.setEnabled(false);
        }else{
            prePics.setEnabled(true);
        }
        if(picFilter.getPage() <= 0 && picFilter.getSubIndex() <= 0 && picFilter.getIndex() <= 0){
            prePic.setEnabled(false);
        }else{
            prePic.setEnabled(true);
        }
        Glide.with(this).load(path).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                stopRefreshAni();
                showShortToas("加载失败");
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                stopRefreshAni();
                //Log.e(TAG, "onResourceReady: " + new BitmapUtil().getBright(wallaper.getmBitmap()));
                return false;
            }
        }).into(wallaper);
        if(currentPics.getHasThubmUp() > 0){
            thumbUp.setSelected(true);
        }else{
            thumbUp.setSelected(false);
        }
        if(currentPics.getHasLiked() > 0){
            like.setSelected(true);
        }else{
            like.setSelected(false);
        }
        if(currentPics.getHasCollected() > 0){
            collect.setSelected(true);
        }else{
            collect.setSelected(false);
        }
    }

    private void reqPics() {
        if(rorateAni != null || lockScreenDao == null || reqCallBack == null || picFilter == null){
            return;
        }
        startRefreshAni();
        picFilter.setPicId(currentPics == null?-1:currentPics.getId());
        if(picFilter.getOther() != null && picFilter.getOther().equals("附近")){
            LocationManager.getInstance().registerLocationListener(iLocationListener);
        }else{
            lockScreenDao.reqPics(picFilter, reqCallBack, null, reqPage);
        }
    }

    private LockScreenDao.CallBack reqCallBack = new LockScreenDao.CallBack() {
        @Override
        public void onFail(String msg) {
            runOnUiThread(()->{
                stopRefreshAni();
                showShortToas(msg);
            });
        }

        @Override
        public void onSuccess(List<ExtendUploadPic> uploadPics) {
            runOnUiThread(()->{
                stopRefreshAni();
                if(uploadPics.size() > 0){
                    if(reqMode == 1){
                        picFilter.setIndex(0);
                        picFilter.setSubIndex(0);
                        pics = uploadPics;
                        currentPics = pics.get(0);
                    }else if(reqMode == 2){
                        reqMode = 1;
                        picFilter.setIndex((uploadPics.size() - 1));
                        picFilter.setSubIndex(0);
                        pics = uploadPics;
                    }else{
                        currentPics = pics.get((uploadPics.size() - 1));
                    }
                    picFilter.setPage(reqPage);
                    picFilter.setHasMore(true);
                    SPUtil.getInstance().saveLockSctreenFiler(picFilter);
                    SPUtil.getInstance().saveLastLockPics(pics);
                    setWallper();
                }else{
                    picFilter.setHasMore(false);
                    nextPics.setEnabled(false);
                    nextPic.setEnabled(false);
                    nextTen.setEnabled(false);
                    if(reqPage == 0){
                        picFilter.setIndex(0);
                        picFilter.setSubIndex(0);
                        pics = uploadPics;
                        currentPics = null;
                        picFilter.setPage(reqPage);
                        SPUtil.getInstance().saveLockSctreenFiler(picFilter);
                        SPUtil.getInstance().saveLastLockPics(pics);
                        setWallper();
                        /*showShortToas("未查询到数据");*/
                    }else{
                        /*showShortToas("没有更多了");*/
                    }
                }
            });
        }
    };

    private LocationManager.ILocationListener iLocationListener = new LocationManager.ILocationListener() {
        @Override
        public void onReceive(BDLocation bdLocation) {
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            if(lockScreenDao == null || reqCallBack == null){
                return;
            }
            picFilter.setPicId(currentPics == null?-1:currentPics.getId());
            lockScreenDao.reqPics(picFilter, reqCallBack, bdLocation, reqPage);
        }

        @Override
        public void onFail(String msg) {
            stopRefreshAni();
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            showShortToas(msg);
        }
    };

    /**
     * operateType 1: like; 2: collect; 3: thumbup
     *@param type
     * */
    private void reqOperator(int type){
        if(rorateAni != null || operateType == -1 || currentPics == null ||
                callBackEmpty == null || lockScreenDao == null){
            return;
        }
        startRefreshAni();
        if(operateType == 1){
            lockScreenDao.reqLikePic(currentPics, type, callBackEmpty);
        }else if(operateType == 2){
            lockScreenDao.reqCollectPic(currentPics, type, callBackEmpty);
        }else{
            lockScreenDao.reqThumbUpPic(currentPics, callBackEmpty);
        }
    }

    private LockScreenDao.CallBackEmpty callBackEmpty = new LockScreenDao.CallBackEmpty() {
        @Override
        public void onFail(String msg) {
            runOnUiThread(()->{
                stopRefreshAni();
                showShortToas(msg);
                operateType = -1;
            });
        }

        @Override
        public void onSuccess() {
            runOnUiThread(()->{
                stopRefreshAni();
                if(currentPics == null){
                    return;
                }
                if(operateType == 1){
                    currentPics.setHasLiked(like.isSelected()?0:1);
                    like.setSelected(!like.isSelected());
                }else if(operateType == 2){
                    currentPics.setHasCollected(collect.isSelected()?0:1);
                    collect.setSelected(!collect.isSelected());
                }else if(operateType == 3){
                    currentPics.setHasThubmUp(1);
                    thumbUp.setSelected(true);
                }
                operateType = -1;
            });
        }
    };

    private void showPicFilter() {
        filterLl = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.lock_screen_filter, null);
        picFilterView = filterLl.findViewById(R.id.pic_filter_view);
        ok = filterLl.findViewById(R.id.ok);
        cancel = filterLl.findViewById(R.id.cancel);
        picFilterView.showOthers();
        ok.setOnClickListener(v -> {
            if(picFilterView != null){
                PicFilter tempFilter = picFilterView.getFilter();
                tempFilter.setPage(picFilter.getPage());
                tempFilter.setIndex(picFilter.getIndex());
                SPUtil.getInstance().saveLockSctreenFiler(tempFilter);
                picFilter = tempFilter;
                reqPage = 0;
                reqPics();
            }
            hidePicFilter();
        });
        cancel.setOnClickListener(v -> {
            hidePicFilter();
        });

        if (filterLl.getParent() == null) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((rootView.getWidth() - DensityUtil.dp2px(this, 40)),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rootView.addView(filterLl, params);
        }
        mHandler.sendEmptyMessageDelayed(SET_FILTER, 600);
        int[] location1 = new int[2];
        filter.getLocationOnScreen(location1);
        int[] location2 = new int[2];
        center.getLocationOnScreen(location2);
        isFilterAni = true;
        AnimatorUtil.startMoveScaleAni(new ScreenPosition(location1[0], location1[1]),
                new ScreenPosition(location2[0], location2[1]),
                1, filterLl, () -> {
                    isFilterAni = false;
                });
    }

    private void hidePicFilter() {
        if (filterLl == null) {
            return;
        }
        int[] location1 = new int[2];
        filter.getLocationOnScreen(location1);
        int[] location2 = new int[2];
        center.getLocationOnScreen(location2);
        isFilterAni = true;
        AnimatorUtil.startMoveScaleAni(
                new ScreenPosition(location2[0], location2[1]),
                new ScreenPosition(location1[0], location1[1]),
                2, filterLl, () -> {
                    isFilterAni = false;
                    if (filterLl.getParent() != null) {
                        rootView.removeView(filterLl);
                        filterLl = null;
                    }
                });
    }

    private void onUnLockUp(MotionEvent event) {
        if (rootView == null) {
            return;
        }
        if (rootView.getTranslationX() > rootView.getWidth() * 0.1f) {
            startUnlockAni(rootView.getTranslationX(), 2);
        } else {
            startUnlockAni(rootView.getTranslationX(), 1);
        }
    }

    private void onUnLockMove(MotionEvent event) {
        if (rootView == null) {
            return;
        }
        float dx = (event.getX() - touchUlockX) * 1.0f;
        if (Math.abs(dx) < 3) {
            return;
        }
        float tempTrasX = rootView.getTranslationX() + dx;
        if (tempTrasX <= 0) {
            return;
        }
        rootView.setX(tempTrasX);
        touchUlockX = event.getX();
    }

    private void startUnlockAni(float transX, int type) {
        ValueAnimator unlockAnimator;
        if (type == 2) { // toright
            unlockAnimator = ValueAnimator.ofFloat(0, 1);
            unlockAnimator.setDuration(300);
        } else {
            unlockAnimator = ValueAnimator.ofFloat(1, 0);
            unlockAnimator.setDuration(100);
        }
        unlockAnimator.addUpdateListener(animation -> {
            if (!isUnlockAni || rootView == null) {
                unlockAnimator.cancel();
                return;
            }
            float value = (float) unlockAnimator.getAnimatedValue();
            if (type == 2) {
                rootView.setTranslationX(transX + (rootView.getWidth() - transX) * value);
                if (value >= 1) {
                    isUnlockAni = false;
                    unlockScreen();
                    finishNoAni();
                }
            } else {
                rootView.setTranslationX(transX * value);
                if (value <= 0) {
                    isUnlockAni = false;
                }
            }
        });
        isUnlockAni = true;
        unlockAnimator.start();
    }

    public void startRefreshAni(){
        if(refresh == null || rorateAni != null){
            return;
        }
        rorateAni = ObjectAnimator.ofFloat(refresh, "rotation", refresh.getRotation(), (refresh.getRotation() - 720));
        rorateAni.setInterpolator(new LinearInterpolator());
        rorateAni.setRepeatCount(100);
        rorateAni.setDuration(2300);
        rorateAni.start();
    }

    public void stopRefreshAni(){
        if(rorateAni == null || refresh == null){
            return;
        }
        rorateAni.cancel();
        rorateAni = null;
    }

    private void setFilter(){
        if(picFilterView == null || picFilter == null){
            return;
        }
        picFilterView.setFilter(picFilter, false);
    }

    @Override
    protected void onStop() {
        if(batteryRec != null){
            unregisterReceiver(batteryRec);
        }
        if (customTimeDown != null) {
            customTimeDown.cancel();
            customTimeDown.setOnTimeDownListener(null);
            customTimeDown = null;
        }
        if(wakeLock != null){
            wakeLock.release();
            wakeLock = null;
            pm = null;
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.alpha = 1f;
        window.setAttributes(windowParams);
        if (customTimeDown == null) {
            customTimeDown = new CustomTimeDown(Long.MAX_VALUE, 60 * 1000);
            customTimeDown.setOnTimeDownListener(this);
            customTimeDown.start();
        }
        batteryRec = new BatteryBroadCastReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryRec, filter);

        if(pm == null) {
            pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            //保持cpu一直运行，不管屏幕是否黑屏
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
            wakeLock.acquire();
        }
    }

    @OnClick({R.id.pre_pics, R.id.pre_pic, R.id.like, R.id.collect, R.id.next_pic, R.id.next_pics, R.id.camera,
            R.id.flash_light, R.id.filter, R.id.refresh, R.id.next_ten, R.id.thumb_up})
    public void onViewClicked(View view) {
        if (filterLl != null) {
            return;
        }
        switch (view.getId()) {
            case R.id.pre_pics:
                prePics();
                break;
            case R.id.pre_pic:
                prevPic();
                break;
            case R.id.like:
                operateType = 1;
                reqOperator(like.isSelected()?1:0);
                break;
            case R.id.collect:
                operateType = 2;
                reqOperator(collect.isSelected()?1:0);
                break;
            case R.id.next_pic:
                nextPic();
                break;
            case R.id.next_pics:
                nextPics();
                break;
            case R.id.camera:
                unlockScreen();
                Intent intent = new Intent();
                // 指定开启系统相机的Action
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
                break;
            case R.id.flash_light:
                if(flashLightUtil == null){
                    flashLightUtil = new FlashLightUtil(LockScreenActivity.this);
                }
                isFlashOpen = !isFlashOpen;
                flashLightUtil.lightSwitch(isFlashOpen);
                break;
            case R.id.filter:
                showPicFilter();
                break;
            case R.id.refresh:
                break;
            case R.id.next_ten:
                nextTen();
                break;
            case R.id.thumb_up:
                if(thumbUp.isSelected()){
                    return;
                }
                operateType = 3;
                reqOperator(-1);
                break;
        }
    }

    private void nextTen() {
        if(picFilter.isHasMore()){
            reqPage = (picFilter.getPage() + 1);
            reqPics();
        }
    }

    private void prePics() {
        if(pics == null){
            return;
        }
        if(picFilter.getIndex() >= 1){
            picFilter.setIndex((picFilter.getIndex() - 1));
            currentPics = pics.get(picFilter.getIndex());
            picFilter.setSubIndex(0);
            picFilter.setHasMore(true);
            setWallper();
        }else{
            if(picFilter.getPage() >= 1){
                reqPage = picFilter.getPage() - 1;
                reqMode = 2;
                reqPics();
            }
        }
    }

    private void nextPics() {
        if(pics == null){
            return;
        }
        if(picFilter.getIndex() < (pics.size() - 1)){
            picFilter.setIndex((picFilter.getIndex() + 1));
            picFilter.setSubIndex(0);
            currentPics = pics.get(picFilter.getIndex());
            setWallper();
        }else{
            if(picFilter.isHasMore()){
                reqPage = (picFilter.getPage() + 1);
                reqPics();
            }
        }
    }

    private void prevPic() {
        if(currentPics != null && currentPics.getPics() != null && picFilter != null){
            if(picFilter.getSubIndex() >= 1){
                picFilter.setSubIndex((picFilter.getSubIndex() - 1));
                picFilter.setHasMore(true);
                setWallper();
            }else if(picFilter.getIndex() >= 1){
                picFilter.setIndex((picFilter.getIndex() - 1));
                currentPics = pics.get(picFilter.getIndex());
                picFilter.setSubIndex((currentPics.getPics().size() - 1));
                picFilter.setHasMore(true);
                setWallper();
            }else{
                if(picFilter.getPage() >= 1){
                    reqPage = picFilter.getPage() - 1;
                    reqMode = 2;
                    reqPics();
                }
            }
        }
    }


    private void nextPic() {
        if(currentPics != null && currentPics.getPics() != null && picFilter != null){
            if(picFilter.getSubIndex() < (currentPics.getPics().size() - 1)){
                picFilter.setSubIndex((picFilter.getSubIndex() + 1));
                setWallper();
            }else if(picFilter.getIndex() < (pics.size() - 1)){
                picFilter.setIndex((picFilter.getIndex() + 1));
                picFilter.setSubIndex(0);
                currentPics = pics.get(picFilter.getIndex());
                setWallper();
            }else {
                if(picFilter.isHasMore()){
                    reqPage = (picFilter.getPage() + 1);
                    reqPics();
                }
            }
        }
    }


    public class BatteryBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
                battery = ((level * 100) / scale);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (filterLl != null && !isFilterAni) {
            hidePicFilter();
        }
    }

    @Override
    public void onTick(long l) {
        if (shimmer != null && (System.currentTimeMillis() - lastShimmerTime) > 80 * 1000) {
            shimmer.start(unlock);
            lastShimmerTime = System.currentTimeMillis();
        }
        if (time != null) {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            String hourStr = (hour < 10?("0" + hour):(hour + ""));
            int minute = calendar.get(Calendar.MINUTE);
            String minuteStr = (minute<10?("0" + minute):("" + minute));
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            String weekDayStr = "";
            if (weekDay == 1) {
                weekDayStr = "天";
            } else if (weekDay == 2) {
                weekDayStr = "一";
            } else if (weekDay == 3) {
                weekDayStr = "二";
            } else if (weekDay == 4) {
                weekDayStr = "三";
            } else if (weekDay == 5) {
                weekDayStr = "四";
            } else if (weekDay == 6) {
                weekDayStr = "五";
            } else if (weekDay == 7) {
                weekDayStr = "六";
            }
            /*BatteryManager batteryManager = (BatteryManager)getSystemService(BATTERY_SERVICE);
            int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);*/
            String timeStr = month + "月" + day + "日" + "  星期" + weekDayStr + "  " + battery + "%电量";
            time.setText(timeStr);
            timeClock.setText((hourStr + ":" + minuteStr));
        }
    }

    @Override
    public void OnFinish() {
    }

    @Override
    protected void onDestroy() {
        if (customTimeDown != null) {
            customTimeDown.cancel();
            customTimeDown.setOnTimeDownListener(null);
            customTimeDown = null;
        }
        reqCallBack = null;
        callBackEmpty = null;
        LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
        if(picFilter != null){
            SPUtil.getInstance().saveLockSctreenFiler(picFilter);
        }
        if(pics != null){
            SPUtil.getInstance().saveLastLockPics(pics);
        }
        if(flashLightUtil != null){
            flashLightUtil.onDestory();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNav(true);
    }

    private MyHandler mHandler = new MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<LockScreenActivity> activity;

        MyHandler(LockScreenActivity activity) {
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
                case SET_FILTER:
                    activity.get().setFilter();
                    break;
            }
        }
    }

}

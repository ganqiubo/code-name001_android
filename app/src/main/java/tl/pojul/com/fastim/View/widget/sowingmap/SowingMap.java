package tl.pojul.com.fastim.View.widget.sowingmap;

import android.app.Application;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import java.util.List;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.TransitImage.Transit.LeftSlowOutTransit;
import tl.pojul.com.fastim.View.widget.TransitImage.Transit.RightAlphaInTransit;
import tl.pojul.com.fastim.View.widget.TransitImage.Transit.Transit;
import tl.pojul.com.fastim.View.widget.TransitImage.TransitImageView;
import tl.pojul.com.fastim.util.CustomTimeDown;
import tl.pojul.com.fastim.util.DensityUtil;
import tl.pojul.com.fastim.util.GlideUtil;

public class SowingMap extends RelativeLayout implements CustomTimeDown.OnTimeDownListener{

    private static final String TAG = "SowingMap";
    private List<String> imgs;
    private TransitImageView[] containers;
    /**
     *图片停留时间
     */
    private long retention = 6000;
    private CustomTimeDown customTimeDown;
    private int currentPosition;
    private TransitLis transitListener;
    private int customDownInit = -1;
    private float prevDownX = 0.0f;
    private float moveSensibility = 0.0009f;
    private float moveDsProgress = 0.008f;
    private float prevDx;
    private boolean reversed;
    private int positionAdd;
    private LinearLayout linearLayout;
    private View selectedViewPoint;
    private float selectedViewPointX = -1;
    private float selectedDsX = -1;
    private boolean isPaused;
    private SowingProgressListener sowingProgressLis;
    private boolean isLoop;
    private long touchDownMilli;
    private float rawTouchDownX;
    public OnItemClickListener onItemClickListener;

    /*private boolean isSwitch;
    *//**
     *切换方向：0 向左滑动切换； 1 向右滑动切换
     *//*
    private int switchDirection;*/

    public SowingMap(Context context) {
        super(context);
    }

    public SowingMap(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SowingMap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
        createContainers();
    }

    private void createContainers() {
        if (imgs == null || imgs.size() <= 0) {
            return;
        }
        if (containers == null) {
            containers = new TransitImageView[3];
            containers[0] = new TransitImageView(getContext());
            containers[1] = new TransitImageView(getContext());
            containers[2] = new TransitImageView(getContext());
            this.addView(containers[0], LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.addView(containers[1], LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.addView(containers[2], LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            transitListener = new TransitLis();
            containers[0].setViewTag("prev");
            containers[1].setViewTag("current");
            containers[2].setViewTag("next");
            containers[0].setTransitListener(transitListener);
            containers[1].setTransitListener(transitListener);
            containers[2].setTransitListener(transitListener);
            addPointView();
            /*containers[0].setOnClickListener(v->{
                Log.e("containers click: " , "0");
            });
            containers[1].setOnClickListener(v->{
                Log.e("containers click: " , "1");
            });
            containers[2].setOnClickListener(v->{
                Log.e("containers click: " , "2");
            });*/
            //this.addView();
        }
        currentPosition = 0;
        containers[0].setTranslationX(0);
        containers[1].setTranslationX(0);
        containers[2].setTranslationX(0);
        selectedViewPoint.setTranslationX(selectedViewPointX);
        GlideUtil.setImageBitmapNoOptions(imgs.get(currentPosition), containers[1]);
        //Glide.with(this).load(imgs.get(currentPosition)).into(containers[1]);
        if(imgs.size() >= 2){
            GlideUtil.setImageBitmapNoOptions(imgs.get(1), containers[2]);
            GlideUtil.setImageBitmapNoOptions(imgs.get((imgs.size() -1)), containers[0]);
            //Glide.with(this).load(imgs.get(1)).into(containers[2]);
            //Glide.with(this).load(imgs.get((imgs.size() -1))).into(containers[0]);
        }
        containers[2].setVisibility(View.INVISIBLE);
        containers[0].setVisibility(View.INVISIBLE);
    }

    private void addPointView() {
        if(this.getChildCount() >= 5){
            this.removeViewAt(4);
        }
        if(this.getChildCount() >= 4){
            this.removeViewAt(3);
            linearLayout = null;
        }
        if(imgs == null || imgs.size() <= 0){
            return;
        }

        linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        for(int i =0; i< imgs.size(); i++){
            View view = new View(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DensityUtil.dp2px(getContext(), 8),
                    DensityUtil.dp2px(getContext(), 4));
            int margin = DensityUtil.dp2px(getContext(), 2);
            layoutParams.setMargins(margin, margin, margin, margin);
            //view.setLayoutParams(layoutParams);
            view.setBackground(getResources().getDrawable(R.drawable.selector_sowing_point));
            linearLayout.addView(view,layoutParams);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = DensityUtil.dp2px(getContext(), 10);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        this.addView(linearLayout, layoutParams);

        selectedViewPoint = new View(getContext());
        selectedViewPoint.setBackground(getResources().getDrawable(R.drawable.selector_sowing_point));
        selectedViewPoint.setSelected(true);
        this.addView(selectedViewPoint, DensityUtil.dp2px(getContext(), 8),
                DensityUtil.dp2px(getContext(), 4));
        selectedViewPointX = -1;
        linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(SowingMap.this.selectedViewPointX == -1){
                    //以区域的左上角的坐标原点
                    //先获取右侧区域的坐标
                    float x = linearLayout.getLeft();
                    float y = linearLayout.getTop();
                    selectedViewPointX = (x + DensityUtil.dp2px(getContext(), 2));
                    selectedDsX = DensityUtil.dp2px(getContext(), 12);
                    selectedViewPoint.setTranslationX(selectedViewPointX);
                    selectedViewPoint.setTranslationY((y + DensityUtil.dp2px(getContext(), 2)));
                }
            }
        });
    }

    public void startLoop(){
        if(imgs == null || imgs.size() <= 0 || isLoop){
            return;
        }
        if(customTimeDown == null){
            customTimeDown = new CustomTimeDown(Integer.MAX_VALUE, retention);
            customTimeDown.setOnTimeDownListener(this);
        }
        customTimeDown.start();
        customDownInit = -1;
        isLoop = true;
    }

    public void stopLoop(){
        isLoop = false;
        if(customTimeDown == null){
            return;
        }
        customTimeDown.cancel();
    }

    public void setRetention(long retention){
        this.retention = retention;
        if(imgs == null || imgs.size() <= 0){
            return;
        }
        if(customTimeDown != null){
            customTimeDown.cancel();
            customTimeDown = new CustomTimeDown(Integer.MAX_VALUE, retention);
            customTimeDown.setOnTimeDownListener(this);
            customTimeDown.start();
            customDownInit = -1;
        }
    }

    class TransitLis implements TransitImageView.TransitListener {
        @Override
        public void onFinish(String tag) {
            if(imgs == null || imgs.size() <= 1 || tag == null){
                return;
            }
            switch (tag){
                case "prev":
                    break;
                case "current":
                    containers[0].stopAni();
                    containers[1].stopAni();
                    containers[2].stopAni();
                    if(positionAdd != 0){
                        currentPosition = ((currentPosition + positionAdd) < 0? (imgs.size() -1) : (currentPosition + positionAdd))%imgs.size();
                        GlideUtil.setImageBitmapNoOptions(imgs.get(currentPosition), containers[1]);
                        //Glide.with(SowingMap.this).load(imgs.get(currentPosition)).into(containers[1]);
                        int nextPosition = (currentPosition + 1)%imgs.size();
                        GlideUtil.setImageBitmapNoOptions(imgs.get(nextPosition), containers[2]);
                        //Glide.with(SowingMap.this).load(imgs.get(nextPosition)).into(containers[2]);
                        int prevPosition = ((currentPosition - 1) < 0? (imgs.size() -1) : (currentPosition - 1))%imgs.size();
                        GlideUtil.setImageBitmapNoOptions(imgs.get(prevPosition), containers[0]);
                        //Glide.with(SowingMap.this).load(imgs.get(prevPosition)).into(containers[0]);
                    }
                    containers[1].setTranslationX(0);
                    containers[2].setTranslationX(0);
                    containers[2].setVisibility(INVISIBLE);
                    containers[0].setTranslationX(0);
                    containers[0].setVisibility(INVISIBLE);
                    positionAdd = 0;
                    if(selectedViewPoint != null){
                        selectedViewPoint.setTranslationX(selectedViewPointX + currentPosition * selectedDsX);
                    }
                    if(sowingProgressLis != null){
                        sowingProgressLis.onFinish(currentPosition);
                    }
                    break;
                case "next":
                    break;
            }
        }

        @Override
        public void onProgress(String tag, float progress) {
            if(imgs == null || imgs.size() <= 1 || tag == null){
                return;
            }
            switch (tag){
                case "current":
                    if(sowingProgressLis != null){
                        sowingProgressLis.onProgress(currentPosition, positionAdd, progress, reversed);
                    }
                    updateSelectedPoint(progress);
                    break;
            }
        }
    }

    public void updateSelectedPoint(float progress){
        /*if(SowingMap.this.getChildAt(3) != null){
            LinearLayout linearLayout = (LinearLayout) SowingMap.this.getChildAt(3);
            for(int i = 0; i< linearLayout.getChildCount(); i++){
                if(i == currentPosition){
                    linearLayout.getChildAt(i).setSelected(true);
                }else{
                    linearLayout.getChildAt(i).setSelected(false);
                }
            }
        }*/
        float x = selectedViewPointX;
        if(reversed && selectedViewPoint != null){
            if(currentPosition != 0){
                x = selectedViewPointX + currentPosition * selectedDsX - selectedDsX * (1 - progress);
            }
            selectedViewPoint.setTranslationX(x);
        }else{
            if(currentPosition != (imgs.size() -1)){
                x = selectedViewPointX + currentPosition * selectedDsX + selectedDsX * progress;
            }
            selectedViewPoint.setTranslationX(x);
        }
    }

    private void nextPic() {
        containers[1].startTransitAnimator(new LeftSlowOutTransit());
        containers[2].startTransitAnimator(new RightAlphaInTransit());
        containers[2].setVisibility(View.VISIBLE);
        positionAdd = 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.e(TAG, "onTouchEvent");
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                SowingMap.this.getParent().requestDisallowInterceptTouchEvent(true);
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                SowingMap.this.getParent().requestDisallowInterceptTouchEvent(true);
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                SowingMap.this.getParent().requestDisallowInterceptTouchEvent(false);
                onTouchUp(event);
                prevDx = 0;
                break;
            case MotionEvent.ACTION_UP:
                SowingMap.this.getParent().requestDisallowInterceptTouchEvent(false);
                onTouchUp(event);
                prevDx = 0;
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onTouchMove(MotionEvent event) {
        if(containers == null || imgs == null || imgs.size() <= 1){
            return;
        }
        float dx = event.getX() - prevDownX;
        if(dx == 0 || (prevDx / dx) <= 0){
            prevDownX = event.getX();
            prevDx = dx;
            return;
        }
        prevDx = dx;
        if(dx > getWidth() * moveSensibility){
            if(containers[2].getVisibility() == VISIBLE){
                rightMove(dx);
            }else{
                leftMove(dx);
            }
            prevDownX = event.getX();
        }else if(dx < - getWidth() * moveSensibility){
            if(containers[0].getVisibility() == VISIBLE){
                leftMove(dx);
            }else{
                rightMove(dx);
            }
            prevDownX = event.getX();
        }

    }

    /***
     * containers[1]、containers[2]
     * progress 0 -> 1
     */
    private void rightMove(float dx) {
        reversed = false;
        if(dx < 0){
            containers[1].moveByProgress(Transit.LEFT_SLOW_OUT, moveDsProgress, reversed);
            containers[2].moveByProgress(Transit.RIGHT_ALPHA_IN, moveDsProgress, reversed);
        }else{
            containers[1].moveByProgress(Transit.LEFT_SLOW_OUT, -moveDsProgress, reversed);
            containers[2].moveByProgress(Transit.RIGHT_ALPHA_IN, -moveDsProgress, reversed);
        }
    }

    /***
     * containers[0]、containers[1]
     * progress 1 -> 0
     */
    private void leftMove(float dx) {
        reversed = true;
        if(dx < 0){
            containers[0].moveByProgress(Transit.LEFT_SLOW_OUT, moveDsProgress, reversed);
            containers[1].moveByProgress(Transit.RIGHT_ALPHA_IN, moveDsProgress, reversed);
        }else{
            containers[0].moveByProgress(Transit.LEFT_SLOW_OUT, -moveDsProgress, reversed);
            containers[1].moveByProgress(Transit.RIGHT_ALPHA_IN, -moveDsProgress, reversed);
        }
    }

    private void onTouchUp(MotionEvent event) {
        if(containers == null || imgs == null){
            return;
        }
        if(imgs.size() > 0 && Math.abs((event.getX() - rawTouchDownX)*1.0/ MyApplication.SCREEN_WIDTH) < 0.01f && (System.currentTimeMillis() - touchDownMilli) < 100){
            if(onItemClickListener != null){
                onItemClickListener.onClick(currentPosition);
            }//Log.e("currentPosition click: " , "" + currentPosition);
            containers[2].isTouched = false;
            containers[1].isTouched = false;
            containers[0].isTouched = false;
        }
        if(imgs.size() <= 1){
            return;
        }
        if (!reversed) {
            float lastMoveRate = (prevDx * 1.0f / getWidth());
            float progress = containers[1].getProgress();
            if(lastMoveRate < -0.015f || (progress > 0.15f && progress < 1.0f)){
                positionAdd = 1;
                containers[1].onTouchUp(new LeftSlowOutTransit(), 1, reversed);
                containers[2].onTouchUp(new RightAlphaInTransit(), 1, reversed);
            }else if(progress != 0.0f && progress != 1.0f){
                if(positionAdd == 1){
                    positionAdd = 0;
                }
                containers[1].onTouchUp(new LeftSlowOutTransit(), -1, reversed);
                containers[2].onTouchUp(new RightAlphaInTransit(), -1, reversed);
            }
            //Log.e("onTouchUp", "lastMoveRate: " + lastMoveRate + "; progress: " + progress);
        } else {
            float lastMoveRate = (prevDx * 1.0f / getWidth());
            float progress = containers[1].getProgress();
            if(lastMoveRate > 0.015f || (progress  < 0.85f && progress > 0.0f)){
                positionAdd = -1;
                containers[0].onTouchUp(new LeftSlowOutTransit(), -1, reversed);
                containers[1].onTouchUp(new RightAlphaInTransit(), -1, reversed);
            }else if(progress != 0.0f && progress != 1.0f){
                if(positionAdd == -1){
                    positionAdd = 0;
                }
                containers[0].onTouchUp(new LeftSlowOutTransit(), 1, reversed);
                containers[1].onTouchUp(new RightAlphaInTransit(), 1, reversed);
            }
            //Log.e("onTouchUp", "lastMoveRate: " + lastMoveRate + "; progress: " + progress);
        }

        customTimeDown = new CustomTimeDown(Integer.MAX_VALUE, retention);
        customTimeDown.setOnTimeDownListener(this);
        customTimeDown.start();
        customDownInit = -1;
    }

    private void onTouchDown(MotionEvent event) {
        prevDownX = event.getX();
        touchDownMilli = System.currentTimeMillis();
        rawTouchDownX = event.getX();
        if(containers == null || imgs == null || imgs.size() <= 1){
            return;
        }
        if(customTimeDown != null){
            customTimeDown.cancel();
        }

        if(containers[0].getVisibility() == VISIBLE){
            containers[0].onTouchDown();
        }
        if(containers[1].getVisibility() == VISIBLE){
            containers[1].onTouchDown();
        }

        if(containers[2].getVisibility() == VISIBLE){
            containers[2].onTouchDown();
        }
    }

    public void setSowingProgressLis(SowingProgressListener sowingProgressLis) {
        this.sowingProgressLis = sowingProgressLis;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onTick(long l) {
        if(imgs == null || imgs.size() <= 1 || isPaused){
            return;
        }
        if(customDownInit == -1){
            customDownInit = 0;
            return;
        }
        nextPic();
    }

    @Override
    public void OnFinish() {
    }

    public void onDestory(){
        if(customTimeDown != null){
            customTimeDown.cancel();
            customTimeDown = null;
        }
    }

    public void onResume(boolean resume){
        if(resume && customTimeDown != null){
            customDownInit = -1;
        }
        this.isPaused = !resume;
    }

    public interface SowingProgressListener{
        void onFinish(int currentPosition);
        void onProgress(int currentPosition, int nextPosition, float progress, boolean reversed);
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

}

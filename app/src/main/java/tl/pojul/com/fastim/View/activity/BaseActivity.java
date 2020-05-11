package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.DensityUtil;

public class BaseActivity extends FragmentActivity {

    private List<PauseListener> pauseListeners = new ArrayList<>();
    public int AniExit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            /*window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);*/
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.parseColor("#55000000"));

            //getWindow().getDecorView().findViewById(android.R.id.content)
                    //.setPadding(0,0,0, DensityUtil.getNavigationBarHeight(this));
            hideNav(true);
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
                new Handler(Looper.getMainLooper()).postDelayed(()->{
                    hideNav(true);
                }, 0);
            });
        }
        /*int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.flags |= flagTranslucentNavigation;
            window.setAttributes(attributes);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/
        super.onCreate(savedInstanceState);

    }

    public void hideNav(boolean hidden) {
        if (hidden) {
            if (Build.VERSION.SDK_INT >= 19) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                //| View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 19) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    public void showShortToas(String msg){
        if(msg == null || "未查询到数据".equals(msg) || "success".equals(msg)){
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLongToas(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void resisterPauseListener(PauseListener pauseListener){
        if(pauseListener != null){
            synchronized (pauseListeners){
                pauseListeners.add(pauseListener);
            }
        }
    }

    public void unResisterPauseListener(PauseListener pauseListener){
        if(pauseListener != null){
            synchronized (pauseListeners){
                pauseListeners.remove(pauseListener);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i = 0 ; i < pauseListeners.size(); i++){
            PauseListener pauseListener = pauseListeners.get(i);
            if(pauseListener != null){
                pauseListener.onPause();
            }
        }
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0 ; i < pauseListeners.size(); i++){
            PauseListener pauseListener = pauseListeners.get(i);
            if(pauseListener != null){
                pauseListener.onResume();
            }
        }
        MobclickAgent.onResume(this);
    }

    public void startActivityAndFinish(Class cls){
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        super.finish();
    }

    public void startActivity(Class cls, Bundle bundle){
        Intent intent = new Intent(this, cls);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.activity_move_enter_anim, R.anim.activity_scale_out_anim);
    }

    public void startActivityForResult(Class cls, Bundle bundle, int resultCode){
        Intent intent = new Intent(this, cls);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, resultCode);
        overridePendingTransition(R.anim.activity_move_enter_anim, R.anim.activity_scale_out_anim);
    }

    @Override
    public void finish(){
        super.finish();
        if(AniExit == 0){
            overridePendingTransition(R.anim.activity_scale_in_anim, R.anim.activity_move_outr_anim);
        }
    }

    public void finishNoAni(){
        super.finish();
    }

    public interface PauseListener{
        void onPause();
        void onResume();
    }
}

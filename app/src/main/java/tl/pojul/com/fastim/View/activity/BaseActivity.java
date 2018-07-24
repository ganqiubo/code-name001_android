package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.R;

public class BaseActivity extends FragmentActivity {

    private List<PauseListener> pauseListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

    }

    public void showShortToas(String msg){
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

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_scale_in_anim, R.anim.activity_move_outr_anim);
    }

    public interface PauseListener{
        void onPause();
        void onResume();
    }
}

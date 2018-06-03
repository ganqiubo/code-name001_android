package tl.pojul.com.fastim.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;

public class MainActivity extends BaseActivity {

    private long firstBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstBackTime > 2000) {
                showShortToas("再按一次退出程序");
                firstBackTime = secondTime;
                return true;
            } else {
                MyApplication.getApplication().closeConn();
                System.exit(0);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

}

package tl.pojul.com.fastim.View.activity;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.fragment.ConversationFragment;
import tl.pojul.com.fastim.View.fragment.FriendsFragment;
import tl.pojul.com.fastim.View.fragment.TrendsFragment;
import tl.pojul.com.fastim.View.widget.MyViewPager;

public class MainActivity extends BaseActivity {

    private long firstBackTime = 0;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MyViewPager mViewPager;
    public ArrayList<Fragment> fragments = new ArrayList<>();
    private TabLayout tabLayout;
    private TextView unreadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_main);

        applyPermission();

        initView();
    }

    private void applyPermission() {
        new RxPermissions(this).requestEach(Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if(!permission.granted){
                            showLongToas("无法获取文件读写权限");
                            MyApplication.getApplication().closeConn();
                            System.exit(0);
                        }
                    }
                });
    }

    private void initView() {

        fragments.add(new ConversationFragment());
        fragments.add(new FriendsFragment());
        fragments.add(new TrendsFragment());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.page_tabs);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        View tab1 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab1Icon = tab1.findViewById(R.id.iv);
        TextView tab1Name = tab1.findViewById(R.id.tv);
        unreadMessage =  tab1.findViewById(R.id.unread_message);
        tabLayout.getTabAt(0).setCustomView(tab1);
        tab1Name.setText("会话");
        tab1Icon.setImageResource(R.drawable.selector_conversation_icon);

        View tab2 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab3Icon = tab2.findViewById(R.id.iv);
        TextView tab3Name = tab2.findViewById(R.id.tv);
        tabLayout.getTabAt(1).setCustomView(tab2);
        tab3Name.setText("好友");
        tab3Icon.setImageResource(R.drawable.selector_friends_icon);

        View tab3 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab2Icon = tab3.findViewById(R.id.iv);
        TextView tab2Name = tab3.findViewById(R.id.tv);
        tabLayout.getTabAt(2).setCustomView(tab3);
        tab2Name.setText("动态");
        tab2Icon.setImageResource(R.drawable.selector_trends_icon);

        mViewPager.setHorizontalScrollBarEnabled(false);
        mViewPager.setCurrentItem(1);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        MyApplication.SCREEN_WIDTH = dm.widthPixels;
        MyApplication.SCREEN_HEIGHT = dm.heightPixels;

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
        }
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

    public void unreadUnmChanged(int total){
        int unReadNum;
        unReadNum = total;
        unreadMessage.setText((unReadNum + ""));
        if(unReadNum > 0){
            unreadMessage.setVisibility(View.VISIBLE);
        }else{
            unreadMessage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

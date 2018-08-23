package tl.pojul.com.fastim.View.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pojul.fastIM.message.request.GetTagMessLabelsReq;
import com.pojul.fastIM.message.response.GetTagMessLabelsResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.tbruyelle.rxpermissions2.RxPermissions;
import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.fragment.ChatFragment;
import tl.pojul.com.fastim.View.fragment.CommunityFragment;
import tl.pojul.com.fastim.View.fragment.MoreFragment;
import tl.pojul.com.fastim.View.fragment.TakePicFragment;
import tl.pojul.com.fastim.View.widget.MyViewPager;
import tl.pojul.com.fastim.util.ArrayUtil;

public class MainActivity extends BaseActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MyViewPager mViewPager;
    public ArrayList<Fragment> fragments = new ArrayList<>();
    private TabLayout tabLayout;
    private TextView unreadMessage;
    //public HomeFragment homeFragment;
    public ChatFragment chatFragment;
    public TakePicFragment takePicFragment;
    public CommunityFragment communityFragment;
    public MoreFragment moreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyPermission();
        
        initView();
    }

    private void applyPermission() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe(permission -> {
                    if(!permission.granted){
                        showLongToas("无法获取所需权限");
                        MyApplication.getApplication().closeConn();
                        System.exit(0);
                    }
                });
    }

    private void initView() {
        /*homeFragment = new HomeFragment();
        chatFragment = new ChatFragment();
        takePicFragment = new TakePicFragment();
        communityFragment = new CommunityFragment();
        userFragment = new UserFragment();*/
        /*fragments.add(homeFragment);
        fragments.add(chatFragment);
        fragments.add(takePicFragment);
        fragments.add(communityFragment);
        fragments.add(userFragment);*/
        fragments.add(null);
        fragments.add(null);
        fragments.add(null);
        fragments.add(null);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.page_tabs);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);
        /*View tab1 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab1Icon = tab1.findViewById(R.id.iv);
        TextView tab1Tv = tab1.findViewById(R.id.note);
        tab1Tv.setText("首页");
        //unreadMessage =  tab1.findViewById(R.id.unread_message);
        tabLayout.getTabAt(0).setCustomView(tab1);
        tab1Icon.setImageResource(R.drawable.selector_tab_home);*/

        View tab2 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab2Icon = tab2.findViewById(R.id.iv);
        TextView tab2Tv = tab2.findViewById(R.id.note);
        unreadMessage =  tab2.findViewById(R.id.unread_message);
        tab2Tv.setText("聊天");
        tabLayout.getTabAt(0).setCustomView(tab2);
        tab2Icon.setImageResource(R.drawable.selector_tab_chat);

        View tab3 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab3Icon = tab3.findViewById(R.id.iv);
        TextView tab3Tv = tab3.findViewById(R.id.note);
        tab3Tv.setText("上传");
        tabLayout.getTabAt(1).setCustomView(tab3);
        tab3Icon.setImageResource(R.drawable.selector_tab_take_pic);

        View tab4 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab4Icon = tab4.findViewById(R.id.iv);
        TextView tab4Tv = tab4.findViewById(R.id.note);
        tab4Tv.setText("社区");
        tabLayout.getTabAt(2).setCustomView(tab4);
        tab4Icon.setImageResource(R.drawable.selector_tab_community);

        View tab5 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab5Icon = tab5.findViewById(R.id.iv);
        TextView tab5Tv = tab5.findViewById(R.id.note);
        tab5Tv.setText("更多");
        tabLayout.getTabAt(3).setCustomView(tab5);
        tab5Icon.setImageResource(R.drawable.select_more_tab);

        mViewPager.setHorizontalScrollBarEnabled(false);
        mViewPager.setOffscreenPageLimit(4);
        //mViewPager.setCurrentItem(1);

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
        MyApplication.getApplication().setMainActivity(this);

        ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);

        updateTagMessLabels();
    }

    private void updateTagMessLabels() {
        GetTagMessLabelsReq req = new GetTagMessLabelsReq();
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                if(mResponse.getCode() == 200){
                    List<String> labels = ArrayUtil.toCommaSplitList(((GetTagMessLabelsResp)mResponse).getTagMessLabels());
                    if(labels.size() > 0){
                        MyApplication.tagMessLabels = labels;
                    }
                }
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                /*case 0:
                    if(fragments.get(position) == null){
                        homeFragment = new HomeFragment();
                        fragments.set(0, homeFragment);
                    }
                    break;*/
                case 0:
                    if(fragments.get(position) == null){
                        chatFragment = new ChatFragment();
                        fragments.set(0, chatFragment);
                    }
                    break;
                case 1:
                    if(fragments.get(position) == null){
                        takePicFragment = new TakePicFragment();
                        fragments.set(1, takePicFragment);
                    }
                    break;
                case 2:
                    if(fragments.get(position) == null){
                        communityFragment = new CommunityFragment();
                        fragments.set(2, communityFragment);
                    }
                    break;
                case 3:
                    if(fragments.get(position) == null){
                        moreFragment = new MoreFragment();
                        fragments.set(3, moreFragment);
                    }
                    break;
            }
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
            /*long secondTime = System.currentTimeMillis();
            if (secondTime - firstBackTime > 2000) {
                showShortToas("再按一次退出程序");
                firstBackTime = secondTime;
                return true;
            } else {
                MyApplication.getApplication().closeConn();
                System.exit(0);
            }*/
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
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

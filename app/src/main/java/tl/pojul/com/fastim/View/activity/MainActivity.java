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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pojul.fastIM.entity.NewVersion;
import com.pojul.fastIM.message.request.CheckTagEffictiveReq;
import com.pojul.fastIM.message.request.GetNewVersionReq;
import com.pojul.fastIM.message.request.GetTagMessLabelsReq;
import com.pojul.fastIM.message.request.PicFilterLabelReq;
import com.pojul.fastIM.message.response.GetNewVersionResp;
import com.pojul.fastIM.message.response.GetTagMessLabelsResp;
import com.pojul.fastIM.message.response.PicFilterLabelResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.tbruyelle.rxpermissions2.RxPermissions;
import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.fragment.ChatFragment;
import tl.pojul.com.fastim.View.fragment.CommunityFragment;
import tl.pojul.com.fastim.View.fragment.DailyPicFragment;
import tl.pojul.com.fastim.View.fragment.HomeFragment;
import tl.pojul.com.fastim.View.fragment.MeFragment;
import tl.pojul.com.fastim.View.fragment.MoreFragment;
import tl.pojul.com.fastim.View.fragment.NearByCommunityFragment;
import tl.pojul.com.fastim.View.fragment.TakePicFragment;
import tl.pojul.com.fastim.View.widget.MyViewPager;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DensityUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;
import tl.pojul.com.fastim.util.VersionUtil;

public class MainActivity extends BaseActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MyViewPager mViewPager;
    public ArrayList<Fragment> fragments = new ArrayList<>();
    private TabLayout tabLayout;
    private TextView unreadMessage;
    //public HomeFragment homeFragment;
    public ChatFragment chatFragment;
    public TakePicFragment takePicFragment;
    //public CommunityFragment communityFragment;
    public NearByCommunityFragment nearByCommunityFragment;
    //public MoreFragment moreFragment;
    public MeFragment meFragment;
    //public HomeFragment homeFragment;
    public DailyPicFragment dailyPicFragment;
    public TextView recomdsTv;

    public static NewVersion newVersion;

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
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)
                .subscribe(permission -> {
                    if(!permission.granted){
                        showLongToas("无法获取所需权限");
                        /*MyApplication.getApplication().closeConn();
                        System.exit(0);*/
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
        fragments.add(null);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.page_tabs);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCanSlide(false);

        tabLayout.setupWithViewPager(mViewPager);
        View tab1 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab1Icon = tab1.findViewById(R.id.iv);
        TextView tab1Tv = tab1.findViewById(R.id.note);
        tab1Tv.setText("首页");
        tabLayout.getTabAt(0).setCustomView(tab1);
        tab1Icon.setImageResource(R.drawable.selector_tab_home);

        View tab2 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab2Icon = tab2.findViewById(R.id.iv);
        TextView tab2Tv = tab2.findViewById(R.id.note);
        unreadMessage =  tab2.findViewById(R.id.unread_message);
        tab2Tv.setText(R.string.chat);
        tabLayout.getTabAt(1).setCustomView(tab2);
        tab2Icon.setImageResource(R.drawable.selector_tab_chat);

        View tab3 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab3Icon = tab3.findViewById(R.id.iv);
        TextView tab3Tv = tab3.findViewById(R.id.note);
        tab3Tv.setText(R.string.upload);
        tabLayout.getTabAt(2).setCustomView(tab3);
        tab3Icon.setImageResource(R.drawable.selector_tab_take_pic);

        View tab4 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab4Icon = tab4.findViewById(R.id.iv);
        TextView tab4Tv = tab4.findViewById(R.id.note);
        tab4Tv.setText(R.string.community);
        tabLayout.getTabAt(3).setCustomView(tab4);
        tab4Icon.setImageResource(R.drawable.selector_tab_community);

        View tab5 = LayoutInflater.from(this).inflate(R.layout.tab_main, null);
        ImageView tab5Icon = tab5.findViewById(R.id.iv);
        TextView tab5Tv = tab5.findViewById(R.id.note);
        recomdsTv = tab5.findViewById(R.id.unread_message);
        //recomdsTv.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recomdsTv.getLayoutParams();
        params.width = DensityUtil.dp2px(this, 6);
        params.height = DensityUtil.dp2px(this, 6);
        params.topMargin = DensityUtil.dp2px(this, 5);
        params.leftMargin = -DensityUtil.dp2px(this, 9);
        recomdsTv.setLayoutParams(params);
        tab5Tv.setText("我的");
        tabLayout.getTabAt(4).setCustomView(tab5);
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
        updatePicLabels();

        checkNewVersion();

        if(SPUtil.getInstance().getInt(SPUtil.SHOW_SCREEN_GUIDE, 0)==0){
            DialogUtil.getInstance().showGuideDialog(this);
        }
    }

    private void checkNewVersion() {
        GetNewVersionReq req = new GetNewVersionReq();
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {}

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    if(mResponse.getCode() == 200){
                        newVersion = ((GetNewVersionResp)mResponse).getNewVersion();
                        String currentVersion = "" + new VersionUtil().packageCode(MainActivity.this);
                        try{
                            if(Integer.parseInt(newVersion.getVersionCode()) > Integer.parseInt(currentVersion)){
                                showUpdateDialog();
                            }
                        }catch(Exception e){

                        }
                    }
                });
            }
        });

    }

    private void showUpdateDialog() {
        if(newVersion == null){
            return;
        }
        DialogUtil.getInstance().showPromptDialog(this, "发现新版本", "发现新版本，是否现在更新？" +
                "\n更新说明：\n" + newVersion.getNote());
        DialogUtil.getInstance().setDialogClick(str -> {
            if("确定".equals(str)){
                MyApplication.getApplication().updateApk();
            }
        });
    }

    private void updatePicLabels() {
        PicFilterLabelReq req = new PicFilterLabelReq();
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {

            @Override
            public void onError(String msg) {
                runOnUiThread(()->{});
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    if(mResponse.getCode() == 200){
                        List<String> labels = ((PicFilterLabelResp)mResponse).getLabels();
                        if(labels.size() > 0){
                            MyApplication.picLabels = labels;
                        }
                    }
                });
            }
        });
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
                case 0:
                    if(fragments.get(position) == null){
                        dailyPicFragment = new DailyPicFragment();
                        fragments.set(0, dailyPicFragment);
                    }
                    break;
                case 1:
                    if(fragments.get(position) == null){
                        chatFragment = new ChatFragment();
                        fragments.set(1, chatFragment);
                    }
                    break;
                case 2:
                    if(fragments.get(position) == null){
                        takePicFragment = new TakePicFragment();
                        fragments.set(2, takePicFragment);
                    }
                    break;
                case 3:
                    if(fragments.get(position) == null){
                        nearByCommunityFragment = new NearByCommunityFragment();
                        fragments.set(3, nearByCommunityFragment);
                    }
                    break;
                case 4:
                    if(fragments.get(position) == null){
                        meFragment = new MeFragment();
                        fragments.set(4, meFragment);
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

    @Override
    protected void onResume() {
        super.onResume();
        hideNav(true);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(meFragment != null){
            meFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.getApplication().setMainActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

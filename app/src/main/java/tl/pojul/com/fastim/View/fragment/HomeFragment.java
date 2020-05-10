package tl.pojul.com.fastim.View.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.SettingActivity;
import tl.pojul.com.fastim.View.widget.MyViewPager;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.container)
    MyViewPager viewPage;
    @BindView(R.id.page_tabs)
    TabLayout pageTabs;
    @BindView(R.id.filter)
    ImageView filter;
    @BindView(R.id.setting)
    ImageView setting;

    private View view;
    private Unbinder unbinder;
    public HomeChoiceFragment homeChoiceFragment;
    private DailyPicFragment dailyPicFragment;
    private List<Fragment> fragments = new ArrayList<>();
    private SectionsPagerAdapter mSectionsPagerAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view!=null){
            ViewGroup parent =(ViewGroup)view.getParent();
            parent.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeChoiceFragment = new HomeChoiceFragment();
        dailyPicFragment = new DailyPicFragment();
        //trendsFragment = new TrendsFragment();
        fragments.add(homeChoiceFragment);
        fragments.add(dailyPicFragment);
        //fragments.add(trendsFragment);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        viewPage.setAdapter(mSectionsPagerAdapter);
        pageTabs.setupWithViewPager(viewPage);

        View tab1 = LayoutInflater.from(getActivity()).inflate(R.layout.tab_chat, null);
        TextView tab1Name = tab1.findViewById(R.id.tv);
        //unreadMessage =  tab1.findViewById(R.id.unread_message);
        pageTabs.getTabAt(0).setCustomView(tab1);
        tab1Name.setText("精选");

        View tab2 = LayoutInflater.from(getActivity()).inflate(R.layout.tab_chat, null);
        TextView tab2Name = tab2.findViewById(R.id.tv);
        pageTabs.getTabAt(1).setCustomView(tab2);
        tab2Name.setText("每日一图");

        /*View tab3 = LayoutInflater.from(getActivity()).inflate(R.layout.tab_chat, null);
        TextView tab3Name = tab3.findViewById(R.id.tv);
        chatPageTabs.getTabAt(2).setCustomView(tab3);
        tab3Name.setText("动态");*/

        viewPage.setHorizontalScrollBarEnabled(true);
        viewPage.setCurrentItem(0);
        viewPage.setOffscreenPageLimit(2);
        viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    setting.setVisibility(View.VISIBLE);
                    filter.setVisibility(View.GONE);
                }else{
                    setting.setVisibility(View.GONE);
                    filter.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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

    @OnClick({R.id.filter, R.id.setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.filter:
                dailyPicFragment.onFilterClick(filter);
                break;
            case R.id.setting:
                ((BaseActivity) getContext()).startActivity(SettingActivity.class, null);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}

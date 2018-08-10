package tl.pojul.com.fastim.View.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import tl.pojul.com.fastim.View.activity.CommunityMapActivity;
import tl.pojul.com.fastim.View.activity.MainActivity;
import tl.pojul.com.fastim.View.widget.MyViewPager;

public class CommunityFragment extends BaseFragment {

    @BindView(R.id.community_set)
    ImageView communitySet;
    @BindView(R.id.community_mode)
    ImageView communityListMode;
    Unbinder unbinder;
    @BindView(R.id.page_tabs)
    TabLayout pageTabs;
    @BindView(R.id.container)
    MyViewPager container;

    private List<Fragment> fragments = new ArrayList<>();
    public NearByCommunityFragment nearByCommunityFragment;
    public FollowCommunityFragment followCommunityFragment;
    private View view;
    private SectionsPagerAdapter mSectionsPagerAdapter;


    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_community, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        fragments.add(null);
        fragments.add(null);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        container.setAdapter(mSectionsPagerAdapter);
        pageTabs.setupWithViewPager(container);

        View tab1 = LayoutInflater.from(getActivity()).inflate(R.layout.tab_chat, null);
        TextView tab1Name = tab1.findViewById(R.id.tv);
        pageTabs.getTabAt(0).setCustomView(tab1);
        tab1Name.setText("附近");

        View tab2 = LayoutInflater.from(getActivity()).inflate(R.layout.tab_chat, null);
        TextView tab2Name = tab2.findViewById(R.id.tv);
        pageTabs.getTabAt(1).setCustomView(tab2);
        tab2Name.setText("关注");
        container.setOffscreenPageLimit(2);
    }

    @OnClick({R.id.community_set, R.id.community_mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.community_set:
                break;
            case R.id.community_mode:
                ((BaseActivity) getActivity()).startActivity(CommunityMapActivity.class, null);
                break;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (fragments.get(position) == null) {
                        nearByCommunityFragment = new NearByCommunityFragment();
                        fragments.set(0, nearByCommunityFragment);
                    }
                    break;
                case 1:
                    if (fragments.get(position) == null) {
                        followCommunityFragment = new FollowCommunityFragment();
                        fragments.set(1, followCommunityFragment);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

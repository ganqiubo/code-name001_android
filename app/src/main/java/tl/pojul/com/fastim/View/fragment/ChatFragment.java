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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.MyViewPager;

public class ChatFragment extends BaseFragment {

    @BindView(R.id.chat_container)
    MyViewPager chatViewPage;
    @BindView(R.id.chat_page_tabs)
    TabLayout chatPageTabs;
    Unbinder unbinder;
    public ArrayList<Fragment> fragments = new ArrayList<>();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    //private TextView unreadMessage;
    public ConversationFragment conversationFragment;
    public FriendsFragment friendsFragment;
    //public TrendsFragment trendsFragment;
    private View view;

    public ChatFragment() {
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
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        conversationFragment = new ConversationFragment();
        friendsFragment = new FriendsFragment();
        //trendsFragment = new TrendsFragment();
        fragments.add(conversationFragment);
        fragments.add(friendsFragment);
        //fragments.add(trendsFragment);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        chatViewPage.setAdapter(mSectionsPagerAdapter);
        chatPageTabs.setupWithViewPager(chatViewPage);

        View tab1 = LayoutInflater.from(getActivity()).inflate(R.layout.tab_chat, null);
        TextView tab1Name = tab1.findViewById(R.id.tv);
        //unreadMessage =  tab1.findViewById(R.id.unread_message);
        chatPageTabs.getTabAt(0).setCustomView(tab1);
        tab1Name.setText("会话");

        View tab2 = LayoutInflater.from(getActivity()).inflate(R.layout.tab_chat, null);
        TextView tab2Name = tab2.findViewById(R.id.tv);
        chatPageTabs.getTabAt(1).setCustomView(tab2);
        tab2Name.setText("好友");

        /*View tab3 = LayoutInflater.from(getActivity()).inflate(R.layout.tab_chat, null);
        TextView tab3Name = tab3.findViewById(R.id.tv);
        chatPageTabs.getTabAt(2).setCustomView(tab3);
        tab3Name.setText("动态");*/

        chatViewPage.setHorizontalScrollBarEnabled(true);
        chatViewPage.setCurrentItem(1);
        chatViewPage.setOffscreenPageLimit(2);
        chatViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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

    /*public void unreadUnmChanged(int total) {
        int unReadNum;
        unReadNum = total;
        if(unreadMessage == null){
            return;
        }
        unreadMessage.setText((unReadNum + ""));
        if (unReadNum > 0) {
            unreadMessage.setVisibility(View.VISIBLE);
        } else {
            unreadMessage.setVisibility(View.GONE);
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

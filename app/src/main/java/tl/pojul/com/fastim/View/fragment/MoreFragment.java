package tl.pojul.com.fastim.View.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pojul.fastIM.entity.ResourceIdTitle;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.TabAdapter;
import tl.pojul.com.fastim.View.activity.AddFriendActivity;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.MainActivity;
import tl.pojul.com.fastim.View.activity.NearByMessActivity;
import tl.pojul.com.fastim.View.activity.NearByPeopleActivity;
import tl.pojul.com.fastim.View.activity.SettingActivity;
import tl.pojul.com.fastim.View.widget.MyGridLayoutManager;

public class MoreFragment extends BaseFragment {


    @BindView(R.id.mine_note_lists)
    RecyclerView mineNoteLists;
    @BindView(R.id.beauty_pic_note_lists)
    RecyclerView beautyPicNoteLists;
    @BindView(R.id.bored_note_lists)
    RecyclerView boredNoteLists;
    Unbinder unbinder;
    @BindView(R.id.mine_note)
    TextView mineNote;
    @BindView(R.id.beauty_pic_note)
    TextView beautyPicNote;
    @BindView(R.id.recommend_note)
    TextView recommendNote;
    @BindView(R.id.nearby_message)
    RelativeLayout nearbyMessage;
    @BindView(R.id.setting)
    RelativeLayout setting;
    @BindView(R.id.nearby_people)
    RelativeLayout nearbyPeople;
    @BindView(R.id.has_unread_message)
    TextView hasUnreadMessage;
    @BindView(R.id.has_unread_people)
    TextView hasUnreadPeople;
    @BindView(R.id.has_unread_friendreq)
    TextView hasUnreadFriendreq;
    @BindView(R.id.friend_req)
    RelativeLayout friendReq;
    @BindView(R.id.has_unread_setting)
    TextView hasUnreadSetting;

    private ArrayList mineDatas = new ArrayList() {{
        add(new ResourceIdTitle(R.drawable.my_page, "主页"));
        add(new ResourceIdTitle(R.drawable.my_pic, "自拍/写真"));
        add(new ResourceIdTitle(R.drawable.publish_mess, "发布的消息"));
        add(new ResourceIdTitle(R.drawable.like_man, "喜欢的人"));
        add(new ResourceIdTitle(R.drawable.follow, "关注的人"));
        //add(new ResourceIdTitle(R.drawable.walk, "足迹"));
    }};

    private ArrayList picDatas = new ArrayList() {{
        add(new ResourceIdTitle(R.drawable.choiceness_normal, "精选"));
        add(new ResourceIdTitle(R.drawable.home_location_normal, "附近"));
        add(new ResourceIdTitle(R.drawable.scenery, "风景"));
        add(new ResourceIdTitle(R.drawable.life, "生活"));
        add(new ResourceIdTitle(R.drawable.community_normal, "建筑"));
        add(new ResourceIdTitle(R.drawable.more_tab, "更多"));
    }};

    private ArrayList boredData = new ArrayList() {{
        add(new ResourceIdTitle(R.drawable.chat, "聊天"));
        add(new ResourceIdTitle(R.drawable.saunter, "逛街"));
        add(new ResourceIdTitle(R.drawable.movie, "看电影"));
        add(new ResourceIdTitle(R.drawable.eating, "吃饭"));
        add(new ResourceIdTitle(R.drawable.play_ball, "打球"));
        add(new ResourceIdTitle(R.drawable.swim, "游泳"));
        add(new ResourceIdTitle(R.drawable.walk, "散步"));
        add(new ResourceIdTitle(R.drawable.body_build, "健身"));
        add(new ResourceIdTitle(R.drawable.lovers, "对象"));
        add(new ResourceIdTitle(R.drawable.partner, "伴侣"));
        add(new ResourceIdTitle(R.drawable.more_tab, "更多"));
    }};

    private TabAdapter mineAdapter;
    private TabAdapter picAdapter;
    private TabAdapter boredAdapter;

    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GridLayoutManager gridLayoutManager = new MyGridLayoutManager(getContext(), 3);
        mineNoteLists.setLayoutManager(gridLayoutManager);
        mineAdapter = new TabAdapter(getContext(), mineDatas, "mine");
        mineNoteLists.setAdapter(mineAdapter);

        GridLayoutManager gridLayoutManager1 = new MyGridLayoutManager(getContext(), 3);
        beautyPicNoteLists.setLayoutManager(gridLayoutManager1);
        picAdapter = new TabAdapter(getContext(), picDatas, "pic");
        beautyPicNoteLists.setAdapter(picAdapter);

        /*GridLayoutManager gridLayoutManager2 = new MyGridLayoutManager(getContext(), 3);
        boredNoteLists.setLayoutManager(gridLayoutManager2);
        boredAdapter = new TabAdapter(getContext(), boredData, "bored");
        boredNoteLists.setAdapter(boredAdapter);*/

        notifyHasRecomds();

    }

    public void notifyHasRecomds(){
        if(MyApplication.hasRecomdMess){
            hasUnreadMessage.setVisibility(View.VISIBLE);
        }else{
            hasUnreadMessage.setVisibility(View.GONE);
        }
        if(MyApplication.hasRecomdProple){
            hasUnreadPeople.setVisibility(View.VISIBLE);
        }else{
            hasUnreadPeople.setVisibility(View.GONE);
        }
        MainActivity mainActivity = ((MainActivity)getActivity());
        if(mainActivity != null && mainActivity.recomdsTv != null){
            if(MyApplication.hasRecomdMess || MyApplication.hasRecomdProple){
                mainActivity.recomdsTv.setVisibility(View.VISIBLE);
            }else{
                mainActivity.recomdsTv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.nearby_message, R.id.nearby_people, R.id.setting, R.id.friend_req})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.nearby_message:
                ((BaseActivity) getContext()).startActivity(NearByMessActivity.class, null);
                break;
            case R.id.nearby_people:
                ((BaseActivity) getContext()).startActivity(NearByPeopleActivity.class, null);
                break;
            case R.id.setting:
                ((BaseActivity) getContext()).startActivity(SettingActivity.class, null);
                break;
            case R.id.friend_req:
                ((BaseActivity) getContext()).startActivity(AddFriendActivity.class, null);
                break;
        }
    }
}

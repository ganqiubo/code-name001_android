package tl.pojul.com.fastim.View.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.View.widget.sowingmap.SowingMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.home_choiceness_icon)
    ImageView homeChoicenessIcon;
    @BindView(R.id.chioseness_pics)
    SowingMap chiosenessPics;
    Unbinder unbinder;
    @BindView(R.id.chioseness_nearby)
    RecyclerView chiosenessNearby;
    @BindView(R.id.chioseness_photo)
    PolygonImageView chiosenessPhoto;
    @BindView(R.id.chioseness_nickname)
    TextView chiosenessNickname;
    @BindView(R.id.chioseness_user_info)
    LinearLayout chiosenessUserInfo;
    @BindView(R.id.chioseness_location_icon)
    ImageView chiosenessLocationIcon;
    @BindView(R.id.chioseness_location)
    TextView chiosenessLocation;
    @BindView(R.id.chioseness_location_ll)
    LinearLayout chiosenessLocationLl;
    @BindView(R.id.chioseness_date)
    TextView chiosenessDate;
    @BindView(R.id.home_choiceness_nearby)
    ImageView homeChoicenessNearby;
    @BindView(R.id.recommend)
    RecyclerView recommend;

    private View view;
    private HashMap<String, MoveTargetPos> sowingViewMpvePos;
    private static final String TAG = "HomeFragment";


    private List<String> chiosenesses = new ArrayList<String>() {{
        add("http://e.hiphotos.baidu.com/image/pic/item/fcfaaf51f3deb48fd0e9be27fc1f3a292cf57842.jpg");
        add("http://f.hiphotos.baidu.com/image/pic/item/3812b31bb051f8195bf514a9d6b44aed2f73e705.jpg");
        add("http://c.hiphotos.baidu.com/image/pic/item/09fa513d269759eeef490028befb43166d22df3c.jpg");
        add("http://imglf0.nosdn0.126.net/img/Sk5OZVhRaUZtSFg5bVR3SGtOeTlIQzJCdFRRUUpQYUNRTllSUzNKVVpTcXBMSmNZU2Q5T1pRPT0.jpg?imageView&thumbnail=500x0&quality=96&stripmeta=0&type=jpg");
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1532179773763&di=9f843b3e3a13e0f103711a7ba1a911cf&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20161018%2F3bf24a8ceb974b0f9cf354fdb86d1271_th.jpg");
    }};

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

        initView();

    }

    private void initView() {
        ((BaseActivity) getActivity()).resisterPauseListener(pauseListener);
        chiosenessPics.setImgs(chiosenesses);
        chiosenessPics.startLoop();
        chiosenessPics.setSowingProgressLis(sowingProgressLis);
    }

    private SowingMap.SowingProgressListener sowingProgressLis = new SowingMap.SowingProgressListener() {
        @Override
        public void onFinish(int currentPosition) {}

        @Override
        public void onProgress(int currentPosition, int nextPosition, float progress, boolean reversed) {
            if(reversed){
                if(progress <= 0.4f){
                    float moveProgress = (0.4f - progress) / 0.4f;
                    SowingNoteViewIn(moveProgress);
                }else if(progress >= 0.6f){
                    float moveProgress = (1- progress) / 0.4f;
                    SowingNoteViewOut(moveProgress);
                }else{

                }
            }else{
                if(progress <= 0.4f){
                    float moveProgress = progress / 0.4f;
                    SowingNoteViewOut(moveProgress);
                }else if(progress >= 0.6f){
                    float moveProgress = (progress - 0.6f) / 0.4f;
                    SowingNoteViewIn(moveProgress);
                }else{

                }
            }
            //chiosenessUserInfo.setTranslationX();
        }
    };

    private void SowingNoteViewIn(float progress) {
        if(sowingViewMpvePos == null){
            setSowingViewMpvePos();
        }
        float UserInfoPosX = sowingViewMpvePos.get("UserInfoPosX").rawPosX - (sowingViewMpvePos.get("UserInfoPosX").dsX * (1 - progress));
        float LocationLlY = sowingViewMpvePos.get("LocationLlY").rawPosY + (sowingViewMpvePos.get("LocationLlY").dsY * (1 - progress));
        float DateX = sowingViewMpvePos.get("DateX").rawPosX + (sowingViewMpvePos.get("UserInfoPosX").dsX * (1 - progress));
        //Log.e(TAG, "SowingNoteViewIn------>UserInfoPosX: " + UserInfoPosX + "; LocationLlY: " + LocationLlY + "; DateX: " + DateX + "; progress: " + progress);
        chiosenessUserInfo.setTranslationX(UserInfoPosX);
        chiosenessLocationLl.setTranslationY(LocationLlY);
        chiosenessDate.setTranslationX(DateX);
    }

    private void SowingNoteViewOut(float progress) {
        if(sowingViewMpvePos == null){
            setSowingViewMpvePos();
        }
        float UserInfoPosX = sowingViewMpvePos.get("UserInfoPosX").rawPosX - (sowingViewMpvePos.get("UserInfoPosX").dsX * progress);
        float LocationLlY = sowingViewMpvePos.get("LocationLlY").rawPosY + (sowingViewMpvePos.get("LocationLlY").dsY * progress);
        float DateX = sowingViewMpvePos.get("DateX").rawPosX + (sowingViewMpvePos.get("UserInfoPosX").dsX * progress);
        //Log.e(TAG, "SowingNoteViewOut------>UserInfoPosX: " + UserInfoPosX + "; LocationLlY: " + LocationLlY + "; DateX: " + DateX + "; progress: " + progress);
        chiosenessUserInfo.setTranslationX(UserInfoPosX);
        chiosenessLocationLl.setTranslationY(LocationLlY);
        chiosenessDate.setTranslationX(DateX);
    }

    private void setSowingViewMpvePos(){
        sowingViewMpvePos = new HashMap<>();
        MoveTargetPos moveTargetPosUser = new MoveTargetPos();
        moveTargetPosUser.rawPosX = chiosenessUserInfo.getTranslationX();
        moveTargetPosUser.dsX = (chiosenessUserInfo.getMeasuredWidth() + chiosenessUserInfo.getX() + 2);
        sowingViewMpvePos.put("UserInfoPosX", moveTargetPosUser);

        MoveTargetPos moveTargetPosLocation = new MoveTargetPos();
        moveTargetPosLocation.rawPosY = chiosenessLocationLl.getTranslationY();
        moveTargetPosLocation.dsY = (chiosenessPics.getMeasuredHeight() + chiosenessLocationLl.getHeight() + 2);
        sowingViewMpvePos.put("LocationLlY", moveTargetPosLocation);

        MoveTargetPos moveTargetPosDate = new MoveTargetPos();
        moveTargetPosDate.rawPosX = chiosenessDate.getTranslationX();
        moveTargetPosDate.dsX = (chiosenessPics.getMeasuredWidth() + chiosenessDate.getMeasuredWidth() + 2);
        sowingViewMpvePos.put("DateX", moveTargetPosDate);
    }

    private BaseActivity.PauseListener pauseListener = new BaseActivity.PauseListener() {
        @Override
        public void onPause() {
            if (chiosenessPics == null) {
                return;
            }
            chiosenessPics.onResume(false);
        }

        @Override
        public void onResume() {
            if (chiosenessPics == null) {
                return;
            }
            chiosenessPics.onResume(true);
        }
    };

    @Override
    public void onDestroyView() {
        try{
            unbinder.unbind();
            sowingProgressLis = null;
            chiosenessPics.onDestory();
            ((BaseActivity) getActivity()).unResisterPauseListener(pauseListener);
        }catch (Exception e){}
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (chiosenessPics == null) {
            return;
        }
        if (!isVisibleToUser) {
            chiosenessPics.stopLoop();
        } else {
            chiosenessPics.startLoop();
        }
    }

    class MoveTargetPos{
        public float rawPosX;
        public float rawPosY;
        public float targetPosX;
        public float targetPosY;
        public float dsX;
        public float dsY;
    }
}

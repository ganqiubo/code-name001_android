package tl.pojul.com.fastim.View.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.pojul.fastIM.entity.LocUser;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.UserSelectFilter;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.request.GetNearByPeopleReq;
import com.pojul.fastIM.message.request.GetRecomdUserReq;
import com.pojul.fastIM.message.response.GetNearByPeopleResp;
import com.pojul.fastIM.message.response.GetRecomdUserResp;
import com.pojul.fastIM.message.response.GetTagMessNearByResp;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.NearByUserAdapter;
import tl.pojul.com.fastim.View.Adapter.TagMessAdapter;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.socket.Converter.NearByUserConverter;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.MyDistanceUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class NearByPeopleActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.filter)
    ImageView filter;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.prople_list)
    RecyclerView propleList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private static final int INIT = 177;
    private User user;
    private UserSelectFilter userSelectFilter = new UserSelectFilter();
    private BDLocation mBDLocation;
    private NearByUserAdapter nearByUserAdapter;
    private List<LocUser> mList = new ArrayList<>();
    private boolean newStart = true;
    private boolean showDialog = true;
    private int num = 20;
    private int page = 0;
    private List<LocUser> recomdLocUsers = new ArrayList<>();
    @BindView(R.id.empty_ll)
    LinearLayout emptyLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_people);
        ButterKnife.bind(this);

        user = SPUtil.getInstance().getUser();
        if(user == null){
            finish();
            return;
        }
        mHandler.sendEmptyMessageDelayed(INIT, 150);

    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        propleList.setLayoutManager(linearLayoutManager);
        nearByUserAdapter = new NearByUserAdapter(this, mList);
        propleList.setAdapter(nearByUserAdapter);

        smartRefresh.setPrimaryColors(Color.parseColor("#e5e5e5"), Color.parseColor("#898989"));
        smartRefresh.setEnableRefresh(true);
        smartRefresh.setEnableLoadmore(true);
        smartRefresh.setOnLoadmoreListener(refreshlayout -> {
            getNearByUser();
        });
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            newStart = true;
            nearByUserAdapter.clearData();
            getNearByUser();
        });
        getNearByUser();
    }

    private void getNearByUser(){
        if(newStart){
            page = 0;
            smartRefresh.setEnableLoadmore(true);
            newStart = false;
        }
        if(showDialog){
            DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
            showDialog = false;
        }
        LocationManager.getInstance().registerLocationListener(iLocationListener);
    }

    public void reqRecomdUser(){
        if (mBDLocation == null) {
            DialogUtil.getInstance().dimissLoadingDialog();
            showLongToas(getString(R.string.get_location_fail));
            return;
        }
        GetRecomdUserReq req = new GetRecomdUserReq();
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    reqNearbyUser();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    if(mResponse.getCode() == 200){
                        recomdLocUsers = new NearByUserConverter().locUserConverter(((GetRecomdUserResp)mResponse).getRecomdUsers(), mBDLocation);
                        nearByUserAdapter.addDatas(recomdLocUsers);
                        MyApplication.hasRecomdProple = false;
                        isInit = false;
                        /*if(MyApplication.getApplication().getMainActivity() != null || MyApplication.getApplication().getMainActivity().moreFragment != null){
                            MyApplication.getApplication().getMainActivity().moreFragment.notifyHasRecomds();
                        }*/
                    }
                    reqNearbyUser();
                });
            }
        });
    }

    private void reqNearbyUser() {
        if(mBDLocation == null){
            smartRefresh.finishLoadmore();
            smartRefresh.finishRefresh();
            DialogUtil.getInstance().dimissLoadingDialog();
            return;
        }
        GetNearByPeopleReq req = new GetNearByPeopleReq();
        req.setUserSelectFilter(userSelectFilter);
        req.setNum(num);
        req.setStartNum((page * num));
        req.setLatLonRange(MyDistanceUtil.getLatLonRange(mBDLocation.getLongitude(), mBDLocation.getLatitude(), 10));
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    smartRefresh.finishLoadmore();
                    smartRefresh.finishRefresh();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas(msg);
                    updateView();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    if(mResponse.getCode() == 200){
                        List<LocUser> rawLocUsers = ((GetNearByPeopleResp)mResponse).getLocUsers();
                        if(rawLocUsers.size() < num){
                            smartRefresh.setEnableLoadmore(false);
                        }
                        page = page + 1;
                        List<LocUser> locUsers = new NearByUserConverter().locUserConverter(rawLocUsers, mBDLocation, recomdLocUsers);
                        nearByUserAdapter.addDatas(locUsers);
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                    smartRefresh.finishLoadmore();
                    smartRefresh.finishRefresh();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    updateView();
                });
            }
        });
    }

    private boolean isInit = true;
    private LocationManager.ILocationListener iLocationListener = new LocationManager.ILocationListener() {
        @Override
        public void onReceive(BDLocation bdLocation) {
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            NearByPeopleActivity.this.mBDLocation = bdLocation;
            if(MyApplication.hasRecomdProple && isInit){
                reqRecomdUser();
            }else {
                reqNearbyUser();
            }
        }

        @Override
        public void onFail(String msg) {
            smartRefresh.finishLoadmore();
            smartRefresh.finishRefresh();
            DialogUtil.getInstance().dimissLoadingDialog();
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            showShortToas(msg);
        }
    };

    @OnClick({R.id.back, R.id.filter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.filter:
                if (DialogUtil.getInstance().isUserSelectFilterPopShow()) {
                    return;
                }
                DialogUtil.getInstance().showUserFilterDialog(NearByPeopleActivity.this, filter, userSelectFilter);
                DialogUtil.getInstance().setUserSelectFilterPopClick(filter -> {
                    if (filter != null) {
                        NearByPeopleActivity.this.userSelectFilter = filter;
                        newStart = true;
                        showDialog = true;
                        nearByUserAdapter.clearData();
                        getNearByUser();
                    }
                });
                break;
        }
    }

    private void updateView() {
        if(mList.size()<=0){
            propleList.setVisibility(View.GONE);
            emptyLl.setVisibility(View.VISIBLE);
        }else{
            propleList.setVisibility(View.VISIBLE);
            emptyLl.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
        super.onDestroy();
    }

    private NearByPeopleActivity.MyHandler mHandler = new NearByPeopleActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<NearByPeopleActivity> activity;

        MyHandler(NearByPeopleActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activity.get() == null) {
                return;
            }
            switch (msg.what) {
                case INIT:
                    activity.get().init();
                    break;
            }
        }
    }

}

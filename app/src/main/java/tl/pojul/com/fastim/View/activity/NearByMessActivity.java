package tl.pojul.com.fastim.View.activity;

import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.fastIM.entity.MessageFilter;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.fastIM.message.request.GetRecomdMessReq;
import com.pojul.fastIM.message.request.GetTagMessNearByReq;
import com.pojul.fastIM.message.response.GetRecomdMessResp;
import com.pojul.fastIM.message.response.GetTagMessNearByResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.TagMessAdapter;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.socket.Converter.TagMessageConverter;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.MyDistanceUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class NearByMessActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.filter)
    ImageView filter;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.mess_list)
    RecyclerView messList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private static final int INIT = 182;
    private User user;

    private List<TagCommuMessage> mList = new ArrayList<>();
    private int page;
    private int num = 20;
    private TagMessAdapter tagMessAdapter;
    private boolean newStart = true;
    private BDLocation bdLocation;
    private MessageFilter messageFilter = new MessageFilter();
    private List<TagCommuMessage> recomdTagMesses = new ArrayList<>();
    private boolean isInit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_mess);
        ButterKnife.bind(this);

        user = SPUtil.getInstance().getUser();
        if (user == null) {
            finish();
            return;
        }

        mHandler.sendEmptyMessageDelayed(INIT, 100);
    }


    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        messList.setLayoutManager(linearLayoutManager);
        tagMessAdapter = new TagMessAdapter(this, mList);
        messList.setAdapter(tagMessAdapter);

        smartRefresh.setPrimaryColors(Color.parseColor("#898989"));
        smartRefresh.setEnableRefresh(false);
        smartRefresh.setEnableLoadmore(true);
        smartRefresh.setOnLoadmoreListener(refreshlayout -> {
            getTagMess();
        });
        getTagMess();
    }

    private void reqRecomdMess() {
        if (bdLocation == null) {
            DialogUtil.getInstance().dimissLoadingDialog();
            showLongToas(getString(R.string.get_location_fail));
            return;
        }
        GetRecomdMessReq req = new GetRecomdMessReq();
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    reqTagMess();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    if(mResponse.getCode() == 200){
                        recomdTagMesses = new TagMessageConverter().converterTagMess(((GetRecomdMessResp)mResponse).getCommunityMessEntities(), bdLocation);
                        tagMessAdapter.addDatas(recomdTagMesses);
                        MyApplication.hasRecomdMess = false;
                        isInit = false;
                        if(MyApplication.getApplication().getMainActivity() != null || MyApplication.getApplication().getMainActivity().moreFragment != null){
                            MyApplication.getApplication().getMainActivity().moreFragment.notifyHasRecomds();
                        }
                    }
                    reqTagMess();
                });
            }
        });
    }

    private void getTagMess() {
        DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
        LocationManager.getInstance().registerLocationListener(iLocationListener);
    }

    private void reqTagMess() {
        if (bdLocation == null) {
            DialogUtil.getInstance().dimissLoadingDialog();
            smartRefresh.finishLoadmore();
            showLongToas(getString(R.string.get_location_fail));
            return;
        }
        if (newStart) {
            page = 0;
            newStart = false;
        }
        GetTagMessNearByReq req = new GetTagMessNearByReq();
        req.setFilter(messageFilter);
        req.setLatLonRange(MyDistanceUtil.getLatLonRange(bdLocation.getLongitude(), bdLocation.getLatitude(), 10));
        req.setNum(num);
        req.setStartNum((page * num));
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishLoadmore();
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    if (mResponse.getCode() == 200) {
                        List<CommunityMessEntity> communityMessEntities = ((GetTagMessNearByResp) mResponse).getCommunityMessEntities();
                        if (communityMessEntities.size() < num) {
                            smartRefresh.setEnableLoadmore(false);
                        }
                        List<TagCommuMessage> tagMesses = new TagMessageConverter().converterTagMess(communityMessEntities, bdLocation, recomdTagMesses);
                        tagMessAdapter.addDatas(tagMesses);
                        page = page + 1;
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishLoadmore();
                });
            }
        });
    }

    private LocationManager.ILocationListener iLocationListener = new LocationManager.ILocationListener() {
        @Override
        public void onReceive(BDLocation bdLocation) {
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            NearByMessActivity.this.bdLocation = bdLocation;
            if(MyApplication.hasRecomdMess && isInit){
                reqRecomdMess();
            }else{
                reqTagMess();
            }

        }

        @Override
        public void onFail(String msg) {
            smartRefresh.finishLoadmore();
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
                if (DialogUtil.getInstance().isScreenPopShow()) {
                    return;
                }
                DialogUtil.getInstance().showScreenPop(NearByMessActivity.this, filter, messageFilter, true);
                DialogUtil.getInstance().setScreenPopClick(messageFilter -> {
                    if (messageFilter != null) {
                        NearByMessActivity.this.messageFilter = messageFilter;
                        tagMessAdapter.clearData();
                        newStart = true;
                        smartRefresh.setEnableLoadmore(true);
                        LocationManager.getInstance().registerLocationListener(iLocationListener);
                        DialogUtil.getInstance().showLoadingSimple(NearByMessActivity.this, getWindow().getDecorView());
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
        super.onDestroy();
    }

    private NearByMessActivity.MyHandler mHandler = new NearByMessActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<NearByMessActivity> activity;

        MyHandler(NearByMessActivity activity) {
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

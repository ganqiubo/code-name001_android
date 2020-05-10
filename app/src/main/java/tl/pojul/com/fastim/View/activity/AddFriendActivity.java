package tl.pojul.com.fastim.View.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pojul.fastIM.entity.AddFriend;
import com.pojul.fastIM.entity.LocUser;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.GetFriendReqs;
import com.pojul.fastIM.message.response.GetFriendReqsResp;
import com.pojul.fastIM.message.response.GetNearByPeopleResp;
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
import tl.pojul.com.fastim.View.Adapter.AddFriendReqAdapter;
import tl.pojul.com.fastim.View.Adapter.NearByUserAdapter;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.socket.Converter.NearByUserConverter;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class AddFriendActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.prople_list)
    RecyclerView propleList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private static final int INIT = 22;
    private User user;
    private AddFriendReqAdapter addFriendReqAdapter;
    private List<AddFriend> mList = new ArrayList<>();
    private boolean newStart = true;
    private int page;
    private boolean showDialog = true;
    private int num = 10;
    @BindView(R.id.empty_ll)
    LinearLayout emptyLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);

        user = SPUtil.getInstance().getUser();
        if(user == null){
            finish();
            return;
        }
        mHandler.sendEmptyMessageDelayed(INIT, 100);

    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        propleList.setLayoutManager(linearLayoutManager);
        addFriendReqAdapter = new AddFriendReqAdapter(this, mList);
        propleList.setAdapter(addFriendReqAdapter);

        smartRefresh.setPrimaryColors(Color.parseColor("#e5e5e5"), Color.parseColor("#898989"));
        smartRefresh.setEnableRefresh(true);
        smartRefresh.setEnableLoadmore(true);
        smartRefresh.setOnLoadmoreListener(refreshlayout -> {
            getAddFriendReqs();
        });
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            newStart = true;
            addFriendReqAdapter.clearData();
            getAddFriendReqs();
        });
        getAddFriendReqs();
    }

    private void getAddFriendReqs() {
        if(newStart){
            page = 0;
            addFriendReqAdapter.clearData();
            smartRefresh.setEnableLoadmore(true);
            newStart = false;
        }
        if(showDialog){
            DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
            showDialog = false;
        }
        GetFriendReqs req = new GetFriendReqs();
        req.setNum(num);
        req.setStartNum((page * num));
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
                        List<AddFriend> addFriends = ((GetFriendReqsResp)mResponse).getAddFriends();
                        if(addFriends.size() < num){
                            smartRefresh.setEnableLoadmore(false);
                        }
                        page = page + 1;
                        addFriendReqAdapter.addDatas(addFriends);
                    }else{
                        //showShortToas(mResponse.getMessage());
                    }
                    smartRefresh.finishLoadmore();
                    smartRefresh.finishRefresh();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    updateView();
                });
            }
        });

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

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private AddFriendActivity.MyHandler mHandler = new AddFriendActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<AddFriendActivity> activity;

        MyHandler(AddFriendActivity activity) {
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

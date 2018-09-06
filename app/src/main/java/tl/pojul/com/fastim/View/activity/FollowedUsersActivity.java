package tl.pojul.com.fastim.View.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pojul.fastIM.entity.ExtendUser;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.FollowUserReq;
import com.pojul.fastIM.message.request.FollowedUsersReq;
import com.pojul.fastIM.message.request.LikedUsersReq;
import com.pojul.fastIM.message.response.FollowedUsersResp;
import com.pojul.fastIM.message.response.LikedUsersResp;
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
import tl.pojul.com.fastim.View.Adapter.UserListAdapter;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class FollowedUsersActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.note)
    TextView note;
    @BindView(R.id.go)
    ImageView go;
    @BindView(R.id.note_close)
    Button noteClose;
    @BindView(R.id.be_followed)
    RelativeLayout beFollowed;
    @BindView(R.id.user_list)
    RecyclerView userList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private static final int INIT = 120;
    private User own;
    private long lastUserId = -1;
    private List<ExtendUser> mList = new ArrayList<>();
    private UserListAdapter userListAdapter;
    private int num = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_users);
        ButterKnife.bind(this);

        own = SPUtil.getInstance().getUser();
        if (own == null) {
            finish();
            return;
        }

        mHandler.sendEmptyMessageDelayed(INIT, 100);

    }

    private void init() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        userList.setLayoutManager(linearLayoutManager);
        userListAdapter = new UserListAdapter(this, mList);
        userList.setAdapter(userListAdapter);

        smartRefresh.setPrimaryColors(Color.parseColor("#898989"));
        smartRefresh.setEnableRefresh(false);
        smartRefresh.setEnableLoadmore(true);
        smartRefresh.setOnLoadmoreListener(refreshlayout -> {
            reqFollowedUser();
        });
        reqFollowedUser();

    }

    private void reqFollowedUser() {
        FollowedUsersReq req = new FollowedUsersReq();
        if (mList.size() > 0) {
            lastUserId = mList.get((mList.size() - 1)).getExtendId();
        }
        req.setLastFollowedId(lastUserId);
        req.setNum(num);
        req.setUserName(own.getUserName());
        DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if (mResponse.getCode() == 200) {
                        FollowedUsersResp resp = (FollowedUsersResp) mResponse;
                        if (resp.getExtendUsers().size() < num) {
                            smartRefresh.setEnableLoadmore(false);
                        }
                        if (resp.getBeFollowedCount() > 0) {
                            beFollowed.setVisibility(View.VISIBLE);
                        }
                        userListAdapter.addDatas(resp.getExtendUsers());
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    @OnClick({R.id.go, R.id.note_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.go:
                startActivity(BeFollowedUsersActivity.class, null);
                break;
            case R.id.note_close:
                beFollowed.setVisibility(View.GONE);
                break;
        }
    }

    private FollowedUsersActivity.MyHandler mHandler = new FollowedUsersActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<FollowedUsersActivity> activity;

        MyHandler(FollowedUsersActivity activity) {
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

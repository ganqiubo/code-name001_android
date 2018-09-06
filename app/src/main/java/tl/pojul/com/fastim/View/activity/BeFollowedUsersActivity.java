package tl.pojul.com.fastim.View.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.pojul.fastIM.entity.ExtendUser;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.BeFollowedUsersReq;
import com.pojul.fastIM.message.request.BeLikedUsersReq;
import com.pojul.fastIM.message.response.BeFollowedUsersResp;
import com.pojul.fastIM.message.response.BeLikedUsersResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.UserListAdapter;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class BeFollowedUsersActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.user_list)
    RecyclerView userList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private static final int INIT = 712;
    private User own;
    private UserListAdapter userListAdapter;
    private List<ExtendUser> mList = new ArrayList<>();
    private long lastUserId = -1;
    private int num = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_followed_users);
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
            reqBeFollowedUsers();
        });
        reqBeFollowedUsers();
    }

    private void reqBeFollowedUsers() {
        BeFollowedUsersReq req = new BeFollowedUsersReq();
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
                        BeFollowedUsersResp resp = (BeFollowedUsersResp) mResponse;
                        if (resp.getExtendUsers().size() < num) {
                            smartRefresh.setEnableLoadmore(false);
                        }
                        userListAdapter.addDatas(resp.getExtendUsers());
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }


    private BeFollowedUsersActivity.MyHandler mHandler = new BeFollowedUsersActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<BeFollowedUsersActivity> activity;

        MyHandler(BeFollowedUsersActivity activity) {
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

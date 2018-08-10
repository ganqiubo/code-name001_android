package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.UserSelect;
import com.pojul.fastIM.entity.WhiteBlack;
import com.pojul.fastIM.message.request.GetFriendsRequest;
import com.pojul.fastIM.message.response.GetFriendsResponse;
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
import tl.pojul.com.fastim.View.Adapter.SelectUserAdapter;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class UserSelectActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.user_list)
    RecyclerView userList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.empty)
    ImageView empty;
    @BindView(R.id.select_all)
    ImageView selectAll;
    @BindView(R.id.select_all_tv)
    TextView selectAllTv;
    @BindView(R.id.sure)
    TextView sure;
    @BindView(R.id.line4)
    View line4;

    private List<UserSelect> userSelects = new ArrayList<>();
    private String type;
    private SelectUserAdapter selectUserAdapter;
    private static final int INIT = 101;
    private boolean canRemove;
    private boolean canSelect;
    private String whiteblackKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
        ButterKnife.bind(this);

        type = getIntent().getStringExtra("type");
        if (type == null || (!"friend".equals(type) && !"ViewWhiteBlack".equals(type))) {
            finish();
            return;
        }

        switch (type) {
            case "friend":
                canSelect = true;
                break;
            case "ViewWhiteBlack":
                whiteblackKey = getIntent().getStringExtra("key");
                smartRefresh.setEnableRefresh(false);
                selectAll.setVisibility(View.GONE);
                selectAllTv.setVisibility(View.GONE);
                line4.setVisibility(View.GONE);
                break;
        }

        mHandler.sendEmptyMessageDelayed(INIT, 200);

    }

    private void init() {

        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        userList.setLayoutManager(layoutmanager);
        selectUserAdapter = new SelectUserAdapter(this, userSelects, canRemove, canSelect);
        userList.setAdapter(selectUserAdapter);

        smartRefresh.setEnableLoadmore(false);
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            userSelects.clear();
            requestRefresh(false);
        });
        requestRefresh(true);
    }

    private void requestRefresh(boolean showDialog) {
        switch (type) {
            case "friend":
                getFriends(showDialog);
                break;
            case "ViewWhiteBlack":
                getWhiteblack();
                break;
        }
    }

    public void getWhiteblack() {
        WhiteBlack whiteBlack = SPUtil.getInstance().getWhiteBlack(whiteblackKey);
        if (whiteBlack == null || whiteBlack.getUsers() == null) {
            return;
        }
        for (int i = 0; i < whiteBlack.getUsers().size(); i++) {
            if (whiteBlack.getUsers().get(i) == null) {
                continue;
            }
            UserSelect userSelect = new UserSelect();
            userSelect.setUser(whiteBlack.getUsers().get(i));
            userSelects.add(userSelect);
        }
        refreshData();
    }

    private void getFriends(boolean showDialog) {
        if (!MyApplication.getApplication().isConnected()) {
            //重新连接服务器，暂不做处理
            showShortToas("请重新连接服务器");
            return;
        }
        GetFriendsRequest getFriendsRequest = new GetFriendsRequest();
        getFriendsRequest.setRequestUrl("GetFriends");
        getFriendsRequest.setUserName(SPUtil.getInstance().getUser().getUserName());
        if (showDialog) {
            DialogUtil.getInstance().showLoadingDialog(this, "加载中...");
        }
        new SocketRequest().request(MyApplication.ClientSocket, getFriendsRequest, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishRefresh(false);
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishRefresh(true);
                    GetFriendsResponse getFriendsResponse = (GetFriendsResponse) mResponse;
                    if (mResponse.getCode() == 200) {
                        if (getFriendsResponse.getFriends() != null && getFriendsResponse.getFriends().size() > 0) {
                            refreshData(getFriendsResponse.getFriends());
                        } else {
                            showShortToas("未查询到数据");
                        }
                    } else {
                        userList.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void refreshData() {
        if (userSelects.size() > 0) {
            userList.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
            userList.setVisibility(View.GONE);
        }
        selectUserAdapter.notifyDataSetChanged();
    }

    private void refreshData(List<Friend> friends) {
        for (int i = 0; i < friends.size(); i++) {
            Friend friend = friends.get(i);
            User user = new User();
            user.setUserName(friend.getUserName());
            user.setNickName(friend.getNickName());
            user.setPhoto(friend.getPhoto());
            user.setSex(friend.getSex());
            UserSelect userSelect = new UserSelect();
            userSelect.setUser(user);
            synchronized (userSelects) {
                userSelects.add(userSelect);
            }
        }
        refreshData();
    }

    @OnClick({R.id.select_all, R.id.sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.select_all:
                selectAll.setSelected(!selectAll.isSelected());
                if (selectAll.isSelected()) {
                    selectUserAdapter.selectedAll();
                    selectAllTv.setText("取消全选");
                } else {
                    selectUserAdapter.unSelectedAll();
                    selectAllTv.setText("全选");
                }
                break;
            case R.id.sure:
                if ("friend".equals(type)) {
                    Intent intent = new Intent();
                    intent.putExtra("UserSelect", new Gson().toJson(selectUserAdapter.getSeletedUser()));
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<UserSelectActivity> activity;

        MyHandler(UserSelectActivity activity) {
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

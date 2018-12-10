package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.GetUsersByNickReq;
import com.pojul.fastIM.message.response.GetUsersByNickResp;
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
import tl.pojul.com.fastim.View.Adapter.SearchUserAdapter;
import tl.pojul.com.fastim.util.DialogUtil;

public class UserSearchActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.search_et)
    EditText searchEt;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.result)
    RecyclerView result;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private static final int INIT = 832;
    private List<User> mList = new ArrayList<>();
    private SearchUserAdapter searchUserAdapter;
    private int page;
    private int num = 10;
    private boolean newStart;
    private String searchKewword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        ButterKnife.bind(this);

        mHandler.sendEmptyMessageDelayed(INIT, 100);

    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        result.setLayoutManager(linearLayoutManager);
        searchUserAdapter = new SearchUserAdapter(this, mList);
        result.setAdapter(searchUserAdapter);

        smartRefresh.setPrimaryColors(Color.parseColor("#898989"));
        smartRefresh.setEnableRefresh(false);
        smartRefresh.setEnableLoadmore(false);
        smartRefresh.setOnLoadmoreListener(refreshlayout -> {
            if(searchKewword.isEmpty()){
                smartRefresh.finishLoadmore();
                smartRefresh.setEnableLoadmore(false);
                return;
            }
            searchUsers();
        });

        searchUserAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent();
            intent.putExtra("user", new Gson().toJson(mList.get(position)));
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void searchUsers() {
        if(newStart){
            page = 0;
            newStart = false;
            searchUserAdapter.clearData();
            DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
        }
        GetUsersByNickReq req = new GetUsersByNickReq();
        req.setNickName(searchKewword);
        req.setNum(num);
        req.setStartNum((page * num));
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    showShortToas(msg);
                    smartRefresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    if(mResponse.getCode() == 200){
                        GetUsersByNickResp resp = (GetUsersByNickResp) mResponse;
                        if(resp.getUsers().size() < num){
                            smartRefresh.setEnableLoadmore(false);
                        }
                        if(resp.getUsers().size() <= 0){
                            showShortToas("未搜索到结果");
                        }
                        searchUserAdapter.addDatas(resp.getUsers());
                        page = page + 1;
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                    smartRefresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                });
            }
        });
    }


    @OnClick({R.id.back, R.id.search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.search:
                if(searchEt.getText().toString().isEmpty()){
                    showLongToas("搜索内容不能为空");
                    return;
                }
                searchKewword = searchEt.getText().toString();
                newStart = true;
                smartRefresh.setEnableLoadmore(true);
                searchUsers();
                break;
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<UserSearchActivity> activity;

        MyHandler(UserSearchActivity activity) {
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

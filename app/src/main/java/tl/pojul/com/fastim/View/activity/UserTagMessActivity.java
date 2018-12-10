package tl.pojul.com.fastim.View.activity;

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

import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.fastIM.message.request.GetTagMessByUserReq;
import com.pojul.fastIM.message.request.GetUserInfoReq;
import com.pojul.fastIM.message.response.GetTagMessByUserResp;
import com.pojul.fastIM.message.response.GetUserInfoResp;
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
import tl.pojul.com.fastim.View.Adapter.TagMessAdapter;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.socket.Converter.TagMessageConverter;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class UserTagMessActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.own_photo)
    PolygonImageView ownPhoto;
    @BindView(R.id.own_nick_name)
    TextView ownNickName;
    @BindView(R.id.owner_certificate)
    TextView ownerCertificate;
    @BindView(R.id.owner_sex)
    ImageView ownerSex;
    @BindView(R.id.age)
    TextView age;
    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.header_line)
    View headerLine;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.tag_mess_list)
    RecyclerView tagMessList;

    private static final int INIT = 182;
    private String visitedUserName;
    private User visitedUser;

    private List<TagCommuMessage> mList = new ArrayList<>();
    private long lastTagMessId = -1;
    private int num = 20;
    private TagMessAdapter tagMessAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tag_mess);
        ButterKnife.bind(this);


        visitedUserName = getIntent().getStringExtra("userName");
        if (visitedUserName == null) {
            finish();
            return;
        }

        mHandler.sendEmptyMessageDelayed(INIT, 100);
    }

    private void init() {
        User visitUser = SPUtil.getInstance().getUser();
        if (visitUser.getUserName().equals(visitedUserName)) {
            visitedUser = visitUser;
            initView();
            reqTagMess();
        } else {
            reqUserInfo();
        }
    }

    private void initView() {
        if (visitedUser == null) {
            DialogUtil.getInstance().dimissLoadingDialog();
            finish();
            return;
        }
        GlideUtil.setImageBitmapNoOptions(visitedUser.getPhoto().getFilePath(), ownPhoto);
        ownNickName.setText(visitedUser.getNickName());
        //ownerCertificate.setText(visitedUser.getCertificate() == 0 ? "未实名认证" : "已实名认证");
        if(visitedUser.getOccupation() != null){
            ownerCertificate.setText(visitedUser.getOccupation());
        }
        ownerSex.setImageResource(visitedUser.getSex() == 0 ? R.drawable.woman : R.drawable.man);
        age.setText((visitedUser.getAge() + "岁"));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        tagMessList.setLayoutManager(linearLayoutManager);
        tagMessAdapter = new TagMessAdapter(this, mList);
        tagMessList.setAdapter(tagMessAdapter);

        smartRefresh.setPrimaryColors(Color.parseColor("#898989"));
        smartRefresh.setEnableRefresh(false);
        smartRefresh.setEnableLoadmore(true);
        smartRefresh.setOnLoadmoreListener(refreshlayout -> {
            reqTagMess();
        });
    }


    private void reqUserInfo() {
        GetUserInfoReq req = new GetUserInfoReq();
        req.setUserName(visitedUserName);
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
                        visitedUser = ((GetUserInfoResp) mResponse).getUser();
                        initView();
                        reqTagMess();
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private void reqTagMess() {
        if (visitedUser == null) {
            DialogUtil.getInstance().dimissLoadingDialog();
            finish();
            return;
        }
        if (mList.size() > 0) {
            lastTagMessId = mList.get((mList.size() - 1)).getId();
        }
        GetTagMessByUserReq req = new GetTagMessByUserReq();
        req.setLastTagMessid(lastTagMessId);
        req.setNum(num);
        req.setUserName(visitedUser.getUserName());
        DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    smartRefresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishLoadmore();
                    if (mResponse.getCode() == 200) {
                        List<CommunityMessEntity> communityMessEntities = ((GetTagMessByUserResp) mResponse).getCommunityMessEntities();
                        if (communityMessEntities.size() < num) {
                            smartRefresh.setEnableLoadmore(false);
                        }
                        List<TagCommuMessage> tagMesses = new TagMessageConverter().converterUserTagMess(communityMessEntities, visitedUser);
                        tagMessAdapter.addDatas(tagMesses);
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private MyHandler mHandler = new MyHandler(this);

    @OnClick({R.id.back, R.id.own_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.own_photo:
                Bundle bundle = new Bundle();
                bundle.putString("userName", visitedUser.getUserName());
                UserTagMessActivity.this.startActivity(MyPageActivity.class, bundle);
                break;
        }
    }

    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<UserTagMessActivity> activity;

        MyHandler(UserTagMessActivity activity) {
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

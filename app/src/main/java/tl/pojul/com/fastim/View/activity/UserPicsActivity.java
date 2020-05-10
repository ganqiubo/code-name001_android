package tl.pojul.com.fastim.View.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.UploadPic;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.UserUploadPicReq;
import com.pojul.fastIM.message.response.UserUploadPicResp;
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
import tl.pojul.com.fastim.View.Adapter.UserPicsAdapter;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.socket.Converter.UploadPicConverter;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;

public class UserPicsActivity extends BaseActivity {

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
    @BindView(R.id.upload_pic_list)
    RecyclerView uploadPicList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private static final int INIT = 182;
    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.header_line)
    View headerLine;
    @BindView(R.id.tag_reply_rl)
    RelativeLayout tagReplyRl;
    private User visitedUser;
    private int num = 10;
    private int page;
    private List<UploadPic> uploadPics = new ArrayList<>();
    private UserPicsAdapter userPicsAdapter;
    @BindView(R.id.empty_ll)
    LinearLayout emptyLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pics);
        ButterKnife.bind(this);

        String json = getIntent().getStringExtra("user");
        if (json == null || json.isEmpty()) {
            finish();
            return;
        }
        visitedUser = new Gson().fromJson(json, User.class);
        if (visitedUser == null) {
            finish();
            return;
        }
        mHandler.sendEmptyMessageDelayed(INIT, 200);

    }

    private void init() {

        GlideUtil.setImageBitmapNoOptions(visitedUser.getPhoto().getFilePath(), ownPhoto);
        ownNickName.setText(visitedUser.getNickName());
        //ownerCertificate.setText(visitedUser.getCertificate() == 0 ? "未实名认证" : "已实名认证");
        if(visitedUser.getOccupation() != null){
            ownerCertificate.setText(visitedUser.getOccupation());
        }
        ownerSex.setImageResource(visitedUser.getSex() == 0 ? R.drawable.woman : R.drawable.man);
        age.setText((visitedUser.getAge() + "岁"));

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        uploadPicList.setLayoutManager(staggeredGridLayoutManager);
        userPicsAdapter = new UserPicsAdapter(this, uploadPics);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        uploadPicList.setAdapter(userPicsAdapter);

        smartRefresh.setPrimaryColors(Color.parseColor("#898989"));
        smartRefresh.setEnableRefresh(false);
        smartRefresh.setEnableLoadmore(true);
        smartRefresh.setOnLoadmoreListener(refreshlayout -> {
            reqUploadPics();
        });
        reqUploadPics();

    }

    private void reqUploadPics() {
        UserUploadPicReq req = new UserUploadPicReq();
        req.setNum(num);
        req.setUserId(visitedUser.getId());
        req.setStartNum(page * num);
        DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas(msg);
                    updateView();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    page = page + 1;
                    if (mResponse.getCode() == 200) {
                        List<UploadPic> tempUploadPics = ((UserUploadPicResp) mResponse).getUploadPics();
                        userPicsAdapter.addDatas(new UploadPicConverter().converter(tempUploadPics));
                        if (tempUploadPics.size() < num) {
                            smartRefresh.setEnableLoadmore(false);
                        }
                    } else {
                        //showShortToas(mResponse.getMessage());
                    }
                    updateView();
                });
            }
        });
    }

    private void updateView() {
        if(uploadPics.size()<=0){
            uploadPicList.setVisibility(View.GONE);
            emptyLl.setVisibility(View.VISIBLE);
        }else{
            uploadPicList.setVisibility(View.VISIBLE);
            emptyLl.setVisibility(View.GONE);
        }
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
                UserPicsActivity.this.startActivity(MyPageActivity.class, bundle);
                break;
        }
    }

    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<UserPicsActivity> activity;

        MyHandler(UserPicsActivity activity) {
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

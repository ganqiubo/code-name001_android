package tl.pojul.com.fastim.View.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.ClaimCommunReq;
import com.pojul.fastIM.message.request.EditCommuDetailReq;
import com.pojul.fastIM.message.request.EditCommuPhoneReq;
import com.pojul.fastIM.message.request.EditCommuPhotoReq;
import com.pojul.fastIM.message.request.FellowCommuReq;
import com.pojul.fastIM.message.request.GetCommuDetailReq;
import com.pojul.fastIM.message.request.GetUserInfoReq;
import com.pojul.fastIM.message.request.MakeQRCodeReq;
import com.pojul.fastIM.message.request.SetNotLevelReq;
import com.pojul.fastIM.message.response.ClaimCommunResp;
import com.pojul.fastIM.message.response.EditCommuPhotoResp;
import com.pojul.fastIM.message.response.GetCommuDetailResp;
import com.pojul.fastIM.message.response.GetUserInfoResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.pojul.objectsocket.utils.FileClassUtil;
import com.wyp.avatarstudio.AvatarStudio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.SPUtil;
import tl.pojul.com.fastim.util.ZxingUtils;

public class CommuDeatilActivity extends BaseActivity implements View.OnLongClickListener {

    private static final String TAG = "CommuDeatilActivity";
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.commun_photo)
    ImageView communPhoto;
    @BindView(R.id.commun_name)
    TextView communName;
    @BindView(R.id.scan)
    ImageView scan;
    @BindView(R.id.edit_detail)
    ImageView editDetail;
    @BindView(R.id.detail)
    TextView detail;
    @BindView(R.id.manager)
    TextView manager;
    @BindView(R.id.phone_note)
    TextView phoneNote;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.edit_phone)
    ImageView editPhone;
    @BindView(R.id.getQR_code)
    TextView getQRCode;
    @BindView(R.id.QR_code_img)
    ImageView QRCodeImg;
    @BindView(R.id.message)
    ImageView message;
    @BindView(R.id.call)
    ImageView call;
    @BindView(R.id.hot)
    ImageView hot;
    @BindView(R.id.follows)
    TextView follows;
    @BindView(R.id.follows_ll)
    LinearLayout followsLl;
    @BindView(R.id.follow)
    TextView follow;
    @BindView(R.id.manager_note)
    TextView managerNote;
    @BindView(R.id.notify_set_note)
    TextView notifySetNote;
    @BindView(R.id.level)
    TextView level;


    private CommunityRoom communityRoom;
    private User user;
    private User managerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_commu_deatil);
        ButterKnife.bind(this);

        String jsonStr = getIntent().getStringExtra("commun_room");
        user = SPUtil.getInstance().getUser();
        try {
            communityRoom = new Gson().fromJson(jsonStr, CommunityRoom.class);
        } catch (Exception e) {
        }
        if (communityRoom == null || communityRoom.getCommunityUid() == null
                || communityRoom.getCommunityUid().split("_").length != 2 || user == null) {
            finish();
            return;
        }
        if (!user.getUserName().equals("460079878303087")) {
            getQRCode.setVisibility(View.GONE);
        }
        communPhoto.getLayoutParams().height = MyApplication.SCREEN_WIDTH;
        communPhoto.requestLayout();
        communName.setText(communityRoom.getCommunityUid().split("_")[1]);
        phone.setOnLongClickListener(this);
        if (communityRoom.getPhoto() != null && !communityRoom.getPhoto().isEmpty()) {
            GlideUtil.setImageBitmapNoOptions(communityRoom.getPhoto(), communPhoto);
        }

        //showAdminInfo("3243435454545", "fdi7wq0e");

        getCommunitySeatail();
    }

    private void initData() {
        if (communityRoom.getPhoto() != null && !communityRoom.getPhoto().isEmpty()) {
            GlideUtil.setImageBitmapNoOptions(communityRoom.getPhoto(), communPhoto);
        }
        if (communityRoom.getDetail() != null && !communityRoom.getDetail().isEmpty()) {
            detail.setText(("\u3000\u3000" + communityRoom.getDetail()));
        }
        if (communityRoom.getManager() != null && !communityRoom.getManager().isEmpty()) {
            manager.setText(communityRoom.getCommunityUid().split("_")[1] + "管理员");
        }
        if (communityRoom.getPhone() != null && !communityRoom.getPhone().isEmpty()) {
            phone.setText(communityRoom.getPhone());
        }
        if (communityRoom.getManager() == null || communityRoom.getManager().isEmpty() ||
                !communityRoom.getManager().equals(user.getUserName())) {
            editDetail.setVisibility(View.GONE);
            editPhone.setVisibility(View.GONE);
            if (communityRoom.getManager() != null && !communityRoom.getManager().isEmpty()) {
                message.setVisibility(View.VISIBLE);
            } else {
                message.setVisibility(View.GONE);
            }
            if (communityRoom.getPhone() != null && !communityRoom.getPhone().isEmpty()) {
                call.setVisibility(View.VISIBLE);
            } else {
                call.setVisibility(View.GONE);
            }
        } else {
            editDetail.setVisibility(View.VISIBLE);
            editPhone.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
        }
        if (communityRoom.getHasFollowed() > 0) {
            follow.setText("取消关注");
            notifySetNote.setEnabled(true);
            level.setEnabled(true);
        } else {
            follow.setText("关注");
            notifySetNote.setEnabled(false);
            level.setEnabled(false);
        }
        follows.setText(("" + communityRoom.getFollows()));
        if(communityRoom.getNotifyLevel() == 1){
            level.setText("紧急");
        }else if(communityRoom.getNotifyLevel() == 2){
            level.setText("重要");
        }else{
            level.setText("普通");
        }
    }

    @OnClick({R.id.back, R.id.scan, R.id.edit_detail, R.id.edit_phone, R.id.getQR_code,
            R.id.call, R.id.commun_photo, R.id.follows_ll, R.id.follow, R.id.question,
            R.id.level, R.id.message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.scan:
                new IntentIntegrator(this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是ScanActivity
                        .initiateScan(); // 初始化扫描
                break;
            case R.id.edit_detail:
                DialogUtil.getInstance().showEditCommuDetail(CommuDeatilActivity.this);
                DialogUtil.getInstance().setDialogClick(str -> {
                    editDetail(str);
                });
                break;
            case R.id.edit_phone:
                DialogUtil.getInstance().showEditDialog(this, "修改社区联系方式", "联系方式", 20);
                DialogUtil.getInstance().setDialogClick(str -> {
                    editPhone(str);
                });
                break;
            case R.id.getQR_code:
                getQRCode();
                break;
            case R.id.call:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phone.getText().toString());
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.message:
                if(managerUser == null){
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("chat_room_type", 1);
                bundle.putString("chat_room_name", managerUser.getNickName());
                bundle.putString("friend", new Gson().toJson(managerUser));
                startActivity(SingleChatRoomActivity.class, bundle);
                break;
            case R.id.commun_photo:
                if (communityRoom.getManager() != null && communityRoom.getManager().equals(user.getUserName())) {
                    updatePhoto();
                }
                break;
            case R.id.follows_ll:
                //hot.performClick();
                //follows.performClick();
                break;
            case R.id.follow:
                if (communityRoom.getHasFollowed() > 0) {
                    followCommu(-1);
                } else {
                    followCommu(0);
                }
                break;
            case R.id.question:
                showQusetionDia();
                break;
            case R.id.level:
                showLevelPop();
                break;
        }
    }

    private void showQusetionDia() {
        DialogUtil.getInstance().showNoteDialog(this,
                "关于社区通知公告",
                "1、社区通知公告是由社区管理员发布的关于社区最新动态的内容;" +
                        "\n2、关注该社区后将会为关注用户推送该社区管理员发布的社区通知和公告，以便用户及时了解到社区最新的新闻动态" +
                        "\n3、关注用户可以设置接收社区通知公告的级别（紧急、重要、普通）" +
                        "\n4、取消关注社区将不再为用户提供社区通知公告推送服务");
    }

    private void showLevelPop(){
        List<String> datas = new ArrayList<>();
        datas.add("普通");
        datas.add("重要");
        datas.add("紧急");
        DialogUtil.getInstance().showPopList(this, datas, level);
        DialogUtil.getInstance().setDialogClick(str -> {
            if(!str.equals(level.getText().toString())){
                setNotLevel(str);
            }
            //level.setText(str);
        });
    }

    public void setNotLevel(String notLevel) {
        int levelNum = 3;
        switch(notLevel){
            case "普通":
                levelNum = 3;
                break;
            case "重要":
                levelNum = 2;
                break;
            case "紧急":
                levelNum = 1;
                break;
        }
        SetNotLevelReq req = new SetNotLevelReq();
        req.setLevel(levelNum);
        req.setRoomUid(communityRoom.getCommunityUid());
        DialogUtil.getInstance().showLoadingDialog(this, "请求中...");
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    showShortToas(msg);
                    DialogUtil.getInstance().dimissLoadingDialog();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                DialogUtil.getInstance().dimissLoadingDialog();
                runOnUiThread(()->{
                    if(mResponse.getCode() == 200){
                        level.setText(notLevel);
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private void followCommu(int mode) {
        FellowCommuReq req = new FellowCommuReq();
        req.setMode(mode);
        req.setRoomUid(communityRoom.getCommunityUid());
        DialogUtil.getInstance().showLoadingDialog(this, "请求中...");
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas("操作失败");
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if (mResponse.getCode() == 200) {
                        if (mode == 0) {
                            communityRoom.setHasFollowed(1);
                            follow.setText("取消关注");
                            communityRoom.setFollows((communityRoom.getFollows() + 1));
                            notifySetNote.setEnabled(true);
                            level.setEnabled(true);
                        } else {
                            communityRoom.setHasFollowed(0);
                            follow.setText("关注");
                            communityRoom.setFollows((communityRoom.getFollows() - 1));
                            notifySetNote.setEnabled(false);
                            level.setEnabled(false);
                        }
                        follows.setText(("" + communityRoom.getFollows()));
                    } else {
                        showShortToas("操作失败");
                    }
                });
            }
        });
    }

    private void updatePhoto() {
        int width = 1080;
        new AvatarStudio.Builder(this)
                .needCrop(true)//是否裁剪，默认裁剪
                .setTextColor(Color.parseColor("#787878"))
                .dimEnabled(true)//背景是否dim 默认true
                .setAspect(1, 1)//裁剪比例 默认1：1
                .setOutput(width, width)//裁剪大小 默认200*200
                .setText("打开相机", "从相册中选取", "取消")
                .show(uri -> {
                    //uri为图片路径
                    File file = new File(uri);
                    if (!file.exists()) {
                        showShortToas("图片不存在");
                        return;
                    }
                    EditCommuPhotoReq req = new EditCommuPhotoReq();
                    req.setPhoto(FileClassUtil.createStringFile(uri));
                    req.setRoomUid(communityRoom.getCommunityUid());
                    if (communityRoom.getPhoto() != null && !communityRoom.getPhoto().isEmpty()) {
                        req.setRawName(FileClassUtil.getNetFileName(communityRoom.getPhoto()));
                    }
                    DialogUtil.getInstance().showLoadingDialog(this, "上传中...");
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
                                    GlideUtil.setImageBitmapNoOptions(((EditCommuPhotoResp) mResponse).getPhotoPath(), communPhoto);
                                    communityRoom.setPhoto(((EditCommuPhotoResp) mResponse).getPhotoPath());
                                } else {
                                    showShortToas(mResponse.getMessage());
                                }
                            });
                        }
                    });
                });
    }

    private void editPhone(String str) {
        EditCommuPhoneReq req = new EditCommuPhoneReq();
        DialogUtil.getInstance().showLoadingDialog(this, "上传中...");
        req.setRoomUid(communityRoom.getCommunityUid());
        req.setPhone(str);
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
                        DialogUtil.getInstance().dimissLoadingDialog();
                        phone.setText(str);
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private void editDetail(String str) {
        EditCommuDetailReq req = new EditCommuDetailReq();
        DialogUtil.getInstance().showLoadingDialog(this, "上传中...");
        req.setRoomUid(communityRoom.getCommunityUid());
        req.setDetail(str);
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
                        DialogUtil.getInstance().dimissLoadingDialog();
                        detail.setText(("\u3000\u3000" + str));
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private void getCommunitySeatail() {
        GetCommuDetailReq req = new GetCommuDetailReq();
        if (communityRoom != null) {
            req.setCommuUid(communityRoom.getCommunityUid());
        }
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    if (mResponse.getCode() == 200 && communityRoom != null) {
                        communityRoom = ((GetCommuDetailResp) mResponse).getRoom();
                        //communityRoom.setManager(((GetCommuDetailResp)mResponse).getRoom().getManager());
                        initData();
                        getManager(communityRoom.getManager());
                    }
                });
            }
        });
    }

    private void getManager(String manager) {
        if(manager == null || manager.isEmpty()){
            return;
        }
        GetUserInfoReq req = new GetUserInfoReq();
        req.setUserName(manager);
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    managerUser = ((GetUserInfoResp)mResponse).getUser();
                });
            }
        });
    }

    private void getQRCode() {
        long milli = System.currentTimeMillis();
        String contents = "communroom=" +
                communityRoom.getCommunityUid() + ",milli=" + milli;
        MakeQRCodeReq req = new MakeQRCodeReq();
        req.setCommunRoomUid(communityRoom.getCommunityUid());
        req.setMilli(milli);
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    Bitmap bitmap = ZxingUtils.createBitmap(contents);
                    if (bitmap != null) {
                        QRCodeImg.setImageBitmap(bitmap);
                        QRCodeImg.setVisibility(View.VISIBLE);
                    } else {
                        showShortToas("生成二维码失败");
                    }
                });
            }
        });
    }


    @Override
    public boolean onLongClick(View v) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        switch (v.getId()) {
            case R.id.phone:
                ClipData mClipData = ClipData.newPlainText("Label", phone.getText().toString());
                cm.setPrimaryClip(mClipData);
                showShortToas("联系电话已被复制到剪贴板");
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "内容为空", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "扫描成功", Toast.LENGTH_LONG).show();
                String scanResult = intentResult.getContents();
                try {
                    String[] params = scanResult.split(",");
                    String roomUid = params[0].split("=")[1];
                    long milli = Long.parseLong(params[1].split("=")[1]);
                    ClaimCommunReq req = new ClaimCommunReq();
                    req.setMilli(milli);
                    req.setRoomUid(roomUid);
                    DialogUtil.getInstance().showLoadingDialog(CommuDeatilActivity.this, "加载中...");
                    new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
                        @Override
                        public void onError(String msg) {
                            runOnUiThread(() -> {
                                showShortToas(msg);
                                DialogUtil.getInstance().dimissLoadingDialog();
                            });
                        }

                        @Override
                        public void onFinished(ResponseMessage mResponse) {
                            runOnUiThread(() -> {
                                DialogUtil.getInstance().dimissLoadingDialog();
                                if (mResponse.getCode() == 200) {
                                    ClaimCommunResp resp = (ClaimCommunResp) mResponse;
                                    showAdminInfo(resp.getAccount(), resp.getPasswd());
                                } else {
                                    showShortToas(mResponse.getMessage());
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    showShortToas("数据错误");
                    return;
                }
                Log.e(TAG, scanResult);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showAdminInfo(String account, String passwd) {
        DialogUtil.getInstance().showAdminInfo(this, account, passwd);
    }

}

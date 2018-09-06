package tl.pojul.com.fastim.View.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.FollowUserReq;
import com.pojul.fastIM.message.request.LikeFollowInfoReq;
import com.pojul.fastIM.message.request.LikeUserReq;
import com.pojul.fastIM.message.request.UpdateAutographReq;
import com.pojul.fastIM.message.request.UpdateUserPhotoReq;
import com.pojul.fastIM.message.response.LikeFollowInfoResp;
import com.pojul.fastIM.message.response.UpdateUserPhotoResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.pojul.objectsocket.utils.FileClassUtil;
import com.wyp.avatarstudio.AvatarStudio;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.fragment.ChatFragment;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.util.DensityUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class MyPageActivity extends BaseActivity {

    @BindView(R.id.mypage_photo)
    ImageView mypagePhoto;
    @BindView(R.id.photo)
    PolygonImageView photo;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.edit)
    ImageView edit;
    @BindView(R.id.like_rl)
    LinearLayout likeRl;
    @BindView(R.id.nick_name)
    TextView nickName;
    @BindView(R.id.follow_rl)
    LinearLayout followRl;
    @BindView(R.id.say_hello)
    TextView sayHello;
    @BindView(R.id.add_friend)
    TextView addFriend;
    @BindView(R.id.chat)
    TextView chat;
    @BindView(R.id.autograph)
    TextView autograph;
    @BindView(R.id.autograph_edit)
    ImageView autographEdit;
    @BindView(R.id.birthday)
    TextView birthday;
    @BindView(R.id.hobby)
    TextView hobby;
    @BindView(R.id.height)
    TextView height;
    @BindView(R.id.weight)
    TextView weight;
    @BindView(R.id.occupation)
    TextView occupation;
    @BindView(R.id.educational_level)
    TextView educationalLevel;
    @BindView(R.id.graduate_school)
    TextView graduateSchool;
    @BindView(R.id.credit)
    TextView credit;
    @BindView(R.id.my_photos_rl)
    RelativeLayout myPhotosRl;
    @BindView(R.id.my_tag_mess_rl)
    RelativeLayout myTagMessRl;
    @BindView(R.id.my_be_reported_rl)
    RelativeLayout myBeReportedRl;
    @BindView(R.id.certificate_rl)
    RelativeLayout certificateRl;
    @BindView(R.id.operate_ll)
    LinearLayout operateLl;
    @BindView(R.id.age)
    TextView age;
    @BindView(R.id.sex)
    ImageView sex;
    @BindView(R.id.certificate)
    TextView certificate;
    @BindView(R.id.my_photos)
    TextView myPhotos;
    @BindView(R.id.my_tag_mess)
    TextView myTagMess;
    @BindView(R.id.my_be_reported)
    TextView myBeReported;
    @BindView(R.id.like_iv)
    ImageView likeIv;
    @BindView(R.id.like_tv)
    TextView likeTv;
    @BindView(R.id.follow_iv)
    ImageView followIv;
    @BindView(R.id.follow_tv)
    TextView followTv;
    @BindView(R.id.root_view)
    LinearLayout rootView;

    private String visitedUserName;
    private User visitedUser;
    private int visitType = 1; // 0: 访问自己; 1: 访问好友; 2: 访问陌生人
    private final static int REQUEST_CODE_USERINFO = 87;
    private static final int LIKE_FOLLOW_INFO = 969;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_my_page);
        ButterKnife.bind(this);

        init();

    }

    private void init() {
        User visitUser = SPUtil.getInstance().getUser();
        visitedUserName = getIntent().getStringExtra("userName");
        if (visitUser == null || visitedUserName == null) {
            finish();
            return;
        }
        if (visitUser.getUserName().equals(visitedUserName)) {
            visitedUser = visitUser;
            visitType = 0;
            initView();
        } else {
            MainActivity mainActivity = MyApplication.getApplication().getMainActivity();
            ChatFragment chatFragment = null;
            User friend = null;
            if (mainActivity != null) {
                chatFragment = mainActivity.chatFragment;
            }
            if (chatFragment != null && chatFragment.friendsFragment != null && chatFragment.friendsFragment.friendsAdapter != null) {
                friend = chatFragment.friendsFragment.friendsAdapter.getFriendByUserName(visitedUserName);
            }
            if (friend != null) {
                visitedUser = friend;
                visitType = 1;
                likeRl.setClickable(false);
                followRl.setClickable(false);
                mHandler.sendEmptyMessageDelayed(LIKE_FOLLOW_INFO, 200);
                //reqLikeFollow();
                initView();
            } else {
                reqUserInfo(visitedUserName);
            }
        }

    }

    private void reqLikeFollow() {
        LikeFollowInfoReq req = new LikeFollowInfoReq();
        req.setBeUserName(visitedUser.getUserName());
        DialogUtil.getInstance().showLoadingSimple(this, rootView);
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
                        likeRl.setClickable(true);
                        followRl.setClickable(true);
                        LikeFollowInfoResp resp = (LikeFollowInfoResp) mResponse;
                        if (resp.getLikeCount() > 0) {
                            likeIv.setSelected(true);
                            likeTv.setSelected(true);
                        } else {
                            likeIv.setSelected(false);
                            likeTv.setSelected(false);
                        }
                        if (resp.getFollowCount() > 0) {
                            followIv.setSelected(true);
                            followTv.setSelected(true);
                        } else {
                            followIv.setSelected(false);
                            followIv.setSelected(false);
                        }
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private void initView() {
        if (visitedUser == null) {
            finish();
            return;
        }
        mypagePhoto.getLayoutParams().height = MyApplication.SCREEN_WIDTH;
        ((RelativeLayout.LayoutParams) photo.getLayoutParams()).topMargin = (MyApplication.SCREEN_WIDTH - DensityUtil.dp2px(this, 30));
        mypagePhoto.requestLayout();
        photo.requestLayout();

        if (visitType != 0) {
            edit.setVisibility(View.GONE);
        }
        if (visitedUser.getMypagePhoto() == null) {
            GlideUtil.setImageBitmapNoOptions(visitedUser.getPhoto().getFilePath(), mypagePhoto);
        } else {
            GlideUtil.setImageBitmapNoOptions(visitedUser.getMypagePhoto().getFilePath(), mypagePhoto);
        }
        GlideUtil.setImageBitmapNoOptions(visitedUser.getPhoto().getFilePath(), photo);
        if (visitType == 0) {
            likeRl.setVisibility(View.GONE);
            followRl.setVisibility(View.GONE);
            operateLl.setVisibility(View.GONE);
        } else if (visitType == 1) {
            sayHello.setVisibility(View.GONE);
            addFriend.setVisibility(View.GONE);
        } else {
            chat.setVisibility(View.GONE);
        }
        if (SPUtil.getInstance().getUser().getSex() == visitedUser.getSex()) {
            likeRl.setVisibility(View.GONE);
            followRl.setVisibility(View.GONE);
        }

        nickName.setText(visitedUser.getNickName());
        String sexStr = visitedUser.getSex() == 0 ? "女" : "男";
        String certi = visitedUser.getCertificate() == 0 ? "未实名认证" : "已实名认证";
        age.setText(visitedUser.getAge() + " 岁•  " + sexStr + " ");
        sex.setImageResource(visitedUser.getSex() == 0 ? R.drawable.woman : R.drawable.man);
        certificate.setText("•  " + certi);
        if (visitedUser.getAutograph() != null && !visitedUser.getAutograph().isEmpty()) {
            autograph.setText("个性签名：" + visitedUser.getAutograph());
        }
        String birthdayType = visitedUser.getBirthdayType() == 0 ? "农历" : "阳历";
        birthday.setText("出生日期：" + visitedUser.getBirthday().split(" ")[0] + "(" + birthdayType + ")");
        if (visitedUser.getHobby() != null && !visitedUser.getHobby().isEmpty()) {
            hobby.setText("兴趣爱好：" + visitedUser.getHobby());
            hobby.setVisibility(View.VISIBLE);
        } else {
            hobby.setVisibility(View.GONE);
        }
        if (visitedUser.getHeight() <= 0) {
            height.setVisibility(View.GONE);
        } else {
            height.setText("身\u3000\u3000高：" + visitedUser.getHeight() + "cm");
            height.setVisibility(View.VISIBLE);
        }
        if (visitedUser.getWeight() <= 0) {
            weight.setVisibility(View.GONE);
        } else {
            weight.setText("体\u3000\u3000重：" + visitedUser.getWeight() + "Kg");
            weight.setVisibility(View.VISIBLE);
        }
        if (visitedUser.getOccupation() == null || visitedUser.getOccupation().isEmpty()) {
            occupation.setVisibility(View.GONE);
        } else {
            occupation.setText("职\u3000\u3000业：" + visitedUser.getOccupation());
            occupation.setVisibility(View.VISIBLE);
        }
        if (visitedUser.getEducationalLevel() == null || visitedUser.getEducationalLevel().isEmpty()) {
            educationalLevel.setVisibility(View.GONE);
        } else {
            educationalLevel.setText("学\u3000\u3000历：" + visitedUser.getEducationalLevel());
            educationalLevel.setVisibility(View.VISIBLE);
        }
        if (visitedUser.getGraduateSchool() == null || visitedUser.getGraduateSchool().isEmpty()) {
            graduateSchool.setVisibility(View.GONE);
        } else {
            graduateSchool.setText("毕业学校：" + visitedUser.getGraduateSchool());
            graduateSchool.setVisibility(View.VISIBLE);
        }
        credit.setText("诚信指数：" + visitedUser.getCredit());
        if (visitedUser.getCertificate() == 1) {
            certificateRl.setVisibility(View.GONE);
        }
        if (visitType != 0) {
            autographEdit.setVisibility(View.GONE);
        }
        if (visitType != 0) {
            myPhotos.setText("Ta的自拍/写真");
            myTagMess.setText("Ta发布的消息");
            myBeReported.setText("Ta的不良记录");
            certificateRl.setVisibility(View.GONE);
        }

    }

    private void reqUserInfo(String visitedUserName) {

    }


    @OnClick({R.id.photo, R.id.back, R.id.edit, R.id.like_rl, R.id.follow_rl, R.id.say_hello,
            R.id.add_friend, R.id.chat, R.id.autograph_edit, R.id.my_photos_rl, R.id.my_tag_mess_rl,
            R.id.my_be_reported_rl, R.id.certificate_rl, R.id.mypage_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.photo:
                if (visitType == 0) {
                    updatePhoto(0);
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                if (visitType == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("user", new Gson().toJson(visitedUser));
                    startActivityForResult(UserInfoEditActivity.class, bundle, REQUEST_CODE_USERINFO);
                }
                break;
            case R.id.like_rl:
                reqLike();
                break;
            case R.id.follow_rl:
                reqFollow();
                break;
            case R.id.say_hello:
                break;
            case R.id.add_friend:
                break;
            case R.id.chat:
                break;
            case R.id.autograph_edit:
                DialogUtil.getInstance().showEditDialog(MyPageActivity.this, "修改签名", "新签名");
                DialogUtil.getInstance().setDialogClick(str -> {
                    updateAutograph(str);
                });
                break;
            case R.id.my_photos_rl:
                break;
            case R.id.my_tag_mess_rl:
                break;
            case R.id.my_be_reported_rl:
                break;
            case R.id.certificate_rl:
                break;
            case R.id.mypage_photo:
                if (visitType == 0) {
                    updatePhoto(1);
                }
                break;
        }
    }

    private void reqFollow() {
        int type = 0;
        if (followIv.isSelected()) {
            type = 1;
        }
        FollowUserReq req = new FollowUserReq();
        req.setFollowedUserName(visitedUser.getUserName());
        req.setType(type);
        DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if(mResponse.getCode() == 200){
                        followIv.setSelected(!followIv.isSelected());
                        followTv.setSelected(followIv.isSelected());
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private void reqLike() {
        int type = 0;
        if (likeIv.isSelected()) {
            type = 1;
        }
        LikeUserReq req = new LikeUserReq();
        req.setLikedUserName(visitedUser.getUserName());
        req.setType(type);
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
                        likeIv.setSelected(!likeIv.isSelected());
                        likeTv.setSelected(likeIv.isSelected());
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private void updateAutograph(String str) {
        UpdateAutographReq req = new UpdateAutographReq();
        req.setAutograph(str);
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
                        visitedUser.setAutograph(str);
                        autograph.setText("个性签名：" + str);
                        SPUtil.getInstance().putUser(visitedUser);
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private void updatePhoto(int type) {
        int width = 500;
        if (type == 1) {
            width = 1080;
        }
        new AvatarStudio.Builder(MyPageActivity.this)
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
                    UpdateUserPhotoReq req = new UpdateUserPhotoReq();
                    req.setPhotoType(type);
                    req.setStringFile(FileClassUtil.createStringFile(uri));
                    if (type == 0) {
                        req.setRawPhotoName(FileClassUtil.getNetFileName(visitedUser.getPhoto().getFilePath()));
                    } else {
                        req.setRawPhotoName(FileClassUtil.getNetFileName(visitedUser.getMypagePhoto().getFilePath()));
                    }
                    DialogUtil.getInstance().showLoadingSimple(MyPageActivity.this, getWindow().getDecorView());
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
                                    if (type == 0) {
                                        GlideUtil.setImageBitmapNoOptions(((UpdateUserPhotoResp) mResponse).getPhotoPath(), photo);
                                        visitedUser.setPhoto(FileClassUtil
                                                .createStringFile(((UpdateUserPhotoResp) mResponse).getPhotoPath()));
                                        SPUtil.getInstance().putUser(visitedUser);
                                    } else {
                                        GlideUtil.setImageBitmapNoOptions(((UpdateUserPhotoResp) mResponse).getPhotoPath(), mypagePhoto);
                                        visitedUser.setMypagePhoto(FileClassUtil
                                                .createStringFile(((UpdateUserPhotoResp) mResponse).getPhotoPath()));
                                        SPUtil.getInstance().putUser(visitedUser);
                                    }
                                } else {
                                    showShortToas(mResponse.getMessage());
                                }
                            });
                        }
                    });
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_USERINFO && resultCode == Activity.RESULT_OK) {
            String json = data.getStringExtra("user");
            if (json == null || json.isEmpty()) {
                return;
            }
            User user = new Gson().fromJson(json, User.class);
            if (user != null) {
                visitedUser.setHeight(user.getHeight());
                visitedUser.setWeight(user.getWeight());
                visitedUser.setHobby(user.getHobby());
                visitedUser.setOccupation(user.getOccupation());
                visitedUser.setEducationalLevel(user.getEducationalLevel());
                visitedUser.setGraduateSchool(user.getGraduateSchool());
                SPUtil.getInstance().putUser(visitedUser);
                initView();
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<MyPageActivity> activity;

        MyHandler(MyPageActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activity.get() == null) {
                return;
            }
            switch (msg.what) {
                case LIKE_FOLLOW_INFO:
                    activity.get().reqLikeFollow();
                    break;
            }
        }
    }



}

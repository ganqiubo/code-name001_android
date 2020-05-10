package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.NearbyUserFilter;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.ExperienceReq;
import com.pojul.fastIM.message.request.GetNearbyUserFilterReq;
import com.pojul.fastIM.message.request.SuggestReq;
import com.pojul.fastIM.message.request.UpdateNUFilterReq;
import com.pojul.fastIM.message.response.ExperienceResp;
import com.pojul.fastIM.message.response.GetNearbyUserFilterResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.UserFilterView;
import tl.pojul.com.fastim.converter.NUFConverter;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.KeyguardGalleryUtil;
import tl.pojul.com.fastim.util.SPUtil;
import tl.pojul.com.fastim.util.VersionUtil;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.visiable_to_nearby)
    CheckBox visiableToNearby;
    @BindView(R.id.nearby_filter)
    UserFilterView nearbyFilter;
    @BindView(R.id.tag_mess_notify)
    CheckBox tagMessNotify;
    @BindView(R.id.tag_mess_vibrate)
    CheckBox tagMessVibrate;
    @BindView(R.id.add_friend_notify)
    CheckBox addFriendNotify;
    @BindView(R.id.keyguard_gallery)
    CheckBox keyguardGallery;
    /*@BindView(R.id.member_price)
    TextView memberPrice;*/
    /*@BindView(R.id.left_time)
    TextView leftTime;*/
    @BindView(R.id.suggestions)
    RelativeLayout suggestions;
    @BindView(R.id.version_number)
    RelativeLayout versionNumber;
    @BindView(R.id.copyright)
    RelativeLayout copyright;
    @BindView(R.id.login_out)
    RelativeLayout loginOut;
    @BindView(R.id.master)
    View master;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.lockscreen_question)
    ImageView lockscreenQuestion;

    private static final int INIT = 589;
    private static final int MASTER = 474;
    private User user;
    private NearbyUserFilter nearbyUserFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        user = SPUtil.getInstance().getUser();
        if (user == null) {
            finish();
            return;
        }
        mHandler.sendEmptyMessageDelayed(INIT, 100);
        mHandler.sendEmptyMessageDelayed(MASTER, 1000);
    }


    private void init() {
        reqNUFilter();
    }

    private void reqNUFilter() {
        GetNearbyUserFilterReq req = new GetNearbyUserFilterReq();
        DialogUtil.getInstance().showLoadingDialog(this, "加载中");
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    visiableToNearby.setEnabled(false);
                    nearbyFilter.setEnabled(false);
                    showShortToas(msg);
                    DialogUtil.getInstance().dimissLoadingDialog();
                    initView();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    if (mResponse.getCode() == 200) {
                        nearbyUserFilter = new NUFConverter().converter(((GetNearbyUserFilterResp) mResponse).getFilter());
                    } else {
                        visiableToNearby.setEnabled(false);
                        nearbyFilter.setEnabled(false);
                        showShortToas(mResponse.getMessage());
                    }
                    DialogUtil.getInstance().dimissLoadingDialog();
                    initView();
                });
            }
        });
    }

    private void initView() {
        if (nearbyUserFilter != null) {
            visiableToNearby.setChecked(nearbyUserFilter.getEnable() == 0 ? false : true);
            nearbyFilter.setUserFilter(nearbyUserFilter.getUserFilter());
        }
        if (SPUtil.getInstance().getInt(SPUtil.NOTIFY_TAGMESS_REPLY, 1) == 0) {
            tagMessNotify.setChecked(false);
        } else {
            tagMessNotify.setChecked(true);
        }
        if (SPUtil.getInstance().getInt(SPUtil.VIBRATE_TAGMESS_REPLY, 1) == 0) {
            tagMessVibrate.setChecked(false);
        } else {
            tagMessVibrate.setChecked(true);
        }
        if (SPUtil.getInstance().getInt(SPUtil.NOTIFY_ADDFRIEND_REQ, 1) == 0) {
            addFriendNotify.setChecked(false);
        } else {
            addFriendNotify.setChecked(true);
        }
        updateKeyguardGallery();

        visiableToNearby.setOnCheckedChangeListener(checkedChanged);
        tagMessNotify.setOnCheckedChangeListener(checkedChanged);
        tagMessVibrate.setOnCheckedChangeListener(checkedChanged);
        addFriendNotify.setOnCheckedChangeListener(checkedChanged);
        keyguardGallery.setOnCheckedChangeListener(checkedChanged);

    }

    private void updateKeyguardGallery() {
        KeyguardGalleryUtil keyguardGalleryUtil = new KeyguardGalleryUtil();
        long validStatus = keyguardGalleryUtil.validStatus();
        if(SPUtil.getInstance().getInt(SPUtil.SHOW_KEYGUARD_GALLERY, 1) == 1){
            keyguardGallery.setChecked(true);
            /*if(validStatus == 1){
                keyguardGallery.setChecked(false);
                //memberPrice.setVisibility(View.VISIBLE);
                leftTime.setVisibility(View.GONE);
            }else if(validStatus == 2){
                keyguardGallery.setChecked(true);
                //memberPrice.setVisibility(View.GONE);
                leftTime.setVisibility(View.GONE);
            }else if(validStatus == 3){
                keyguardGallery.setChecked(false);
                //memberPrice.setVisibility(View.VISIBLE);
                leftTime.setVisibility(View.GONE);
            }else{
                keyguardGallery.setChecked(true);
                //memberPrice.setVisibility(View.GONE);
                leftTime.setVisibility(View.VISIBLE);
                leftTime.setText("体验剩余时间: " + DateUtil.getLeftTime(validStatus));
            }*/
        }else{
            //memberPrice.setVisibility(View.GONE);
            //leftTime.setVisibility(View.GONE);
            keyguardGallery.setChecked(false);
        }
    }

    private void master() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) master.getLayoutParams();
        params.height = nearbyFilter.getHeight();
        master.setLayoutParams(params);
        if (visiableToNearby.isChecked()) {
            master.setVisibility(View.GONE);
        } else {
            master.setVisibility(View.VISIBLE);
        }
    }

    private CompoundButton.OnCheckedChangeListener checkedChanged = (buttonView, isChecked) -> {
        switch (buttonView.getId()) {
            case R.id.visiable_to_nearby:
                if (visiableToNearby.isChecked()) {
                    master.setVisibility(View.GONE);
                } else {
                    master.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tag_mess_notify:
                if (isChecked) {
                    SPUtil.getInstance().putInt(SPUtil.NOTIFY_TAGMESS_REPLY, 1);
                } else {
                    SPUtil.getInstance().putInt(SPUtil.NOTIFY_TAGMESS_REPLY, 0);
                }
                break;
            case R.id.tag_mess_vibrate:
                if (isChecked) {
                    SPUtil.getInstance().putInt(SPUtil.getInstance().VIBRATE_TAGMESS_REPLY, 1);
                } else {
                    SPUtil.getInstance().putInt(SPUtil.getInstance().VIBRATE_TAGMESS_REPLY, 0);
                }
                break;
            case R.id.add_friend_notify:
                if (isChecked) {
                    SPUtil.getInstance().putInt(SPUtil.NOTIFY_ADDFRIEND_REQ, 1);
                } else {
                    SPUtil.getInstance().putInt(SPUtil.NOTIFY_ADDFRIEND_REQ, 0);
                }
                break;
            case R.id.keyguard_gallery:
                if(isChecked){
                    //keyguardGalleryChecked();
                    SPUtil.getInstance().putInt(SPUtil.SHOW_KEYGUARD_GALLERY, 1);
                }else{
                    SPUtil.getInstance().putInt(SPUtil.SHOW_KEYGUARD_GALLERY, 0);
                }
                break;
        }
    };

    private void keyguardGalleryChecked() {
        /*KeyguardGalleryUtil keyguardGalleryUtil = new KeyguardGalleryUtil();
        //1：不可用; 2: 会员可用; 3: 可以体验; 其他: 体验过期时间
        long validStatus = keyguardGalleryUtil.validStatus();
        if(validStatus == 1 || validStatus == 3){
            keyguardGallery.setChecked(false);
            DialogUtil.getInstance().showKeyguardGalleryDialog(this, validStatus);
            DialogUtil.getInstance().setDialogClick(str -> {
                if("gotopay".equals(str)){
                    startActivity(PayMemberActivity.class, null);
                }else if("experience".equals(str)){
                    reqExperience();
                }
            });
        }else {*/
            SPUtil.getInstance().putInt(SPUtil.SHOW_KEYGUARD_GALLERY, 1);
            updateKeyguardGallery();
        //}
    }

    private void reqExperience() {
        ExperienceReq req = new ExperienceReq();
        DialogUtil.getInstance().showLoadingDialog(this, "加载中");
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
                    if(mResponse.getCode() == 200){
                        user.setCanExperience(0);
                        SPUtil.getInstance().putUser(user);
                        String validTime = ((ExperienceResp)mResponse).getValidTime();
                        SPUtil.getInstance().putExperienceValidTime(validTime);
                        updateKeyguardGallery();
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                    DialogUtil.getInstance().dimissLoadingDialog();
                });
            }
        });
    }

    private void updateNearByfilter(NearbyUserFilter newFilter) {
        UpdateNUFilterReq req = new UpdateNUFilterReq();
        req.setFilter(newFilter);
        DialogUtil.getInstance().showLoadingDialog(this, "保存设置中");
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas("保存设置失败");
                    finish();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if (mResponse.getCode() == 200) {
                    } else {
                        showShortToas("保存设置失败");
                    }
                    finish();
                });
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (DialogUtil.getInstance().isShowLoadingDialog()) {
            return;
        }
        if (nearbyUserFilter == null) {
            finish();
            return;
        }
        nearbyUserFilter.setFilter("");
        String rawJson = new Gson().toJson(nearbyUserFilter);

        NearbyUserFilter newFilter = new NearbyUserFilter();
        newFilter.setUserFilter(nearbyFilter.getUserFilter());
        newFilter.setEnable(visiableToNearby.isChecked() ? 1 : 0);
        newFilter.setFilter("");
        String newJson = new Gson().toJson(newFilter);
        if (rawJson.equals(newJson)) {
            finish();
            return;
        }
        updateNearByfilter(newFilter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        nearbyFilter.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.back, R.id.suggestions, R.id.version_number, R.id.copyright,R.id.login_out,
            R.id.pay_member, R.id.lockscreen_question,R.id.privacy,R.id.user_agree})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.suggestions:
                DialogUtil.getInstance().showSuggestDialog(SettingActivity.this);
                DialogUtil.getInstance().setDialogClick(str -> {
                    suggestReq(str);
                });
                break;
            case R.id.version_number:
                VersionUtil versionUtil = new VersionUtil();
                String versionCode = "version code: " + versionUtil.packageCode(SettingActivity.this);
                String versionName = "version name: " + versionUtil.packageName(SettingActivity.this);
                DialogUtil.getInstance().showPromptDialog(SettingActivity.this, "软件版本信息", versionCode + "\n" + versionName);
                break;
            case R.id.copyright:
                String content = "1、 所有上传至脚步图库的作品，作品提供者应保证对作品享有合法版权或版权授权，不得提供任何侵权作品。脚步图库无法保证上传作品的真实性、合法性\n" +
                        "2、如发现任何第三方提供给脚步图库的作品存在版权争议或纠纷，并侵害他人合法版权的，脚步图库将根据法律法规及双方协议约定，采取及时删除作品、要求提供者赔偿损失等惩罚措施，追究其法律责任\n" +
                        "3、使用脚步图库服务所存在的风险将完全由用户自己承担；因其使用创脚步图库服务而产生的一切后果也由其自己承担，脚步图库对用户不承担任何责任";
                DialogUtil.getInstance().showPromptDialog(SettingActivity.this, "版权声明", content);
                break;
            case R.id.login_out:
                DialogUtil.getInstance().showPromptDialog(SettingActivity.this, "注销", "确定退出当前账号？");
                DialogUtil.getInstance().setDialogClick(str -> {
                    if("确定".equals(str)){
                        loginOut();
                    }
                });
                break;
            case R.id.pay_member:
                startActivity(PayMemberActivity.class, null);
                break;
            case R.id.lockscreen_question:
                DialogUtil.getInstance().showGuideDialog(this);
                break;
            case R.id.privacy:
                Intent intent = new Intent(SettingActivity.this, WebviewActivity.class);
                intent.putExtra("url", "file:////android_asset/privacy.html");
                startActivity(intent);
                break;
            case R.id.user_agree:
                intent = new Intent(SettingActivity.this, WebviewActivity.class);
                intent.putExtra("url", "file:////android_asset/user_agreement.html");
                startActivity(intent);
                break;
        }
    }

    private void loginOut() {
        SPUtil.getInstance().clearArrays();
        MyApplication.getApplication().closeConn();
        MyApplication.loginOut = true;
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void suggestReq(String str) {
        SuggestReq req = new SuggestReq();
        req.setContent(str);
        DialogUtil.getInstance().showLoadingDialog(this, "加载中");
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
                runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if(mResponse.getCode() == 200){
                        showShortToas("感谢您的吐槽，我们将会根据您的意见反馈更好的完善脚步APP");
                        DialogUtil.getInstance().dimissSuggestDialog();
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private MyHandler mHandler = new MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<SettingActivity> activity;

        MyHandler(SettingActivity activity) {
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
                case MASTER:
                    activity.get().master();
                    break;
            }
        }
    }

}

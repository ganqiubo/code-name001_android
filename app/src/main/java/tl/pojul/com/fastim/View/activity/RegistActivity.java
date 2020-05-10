package tl.pojul.com.fastim.View.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lahm.library.EasyProtectorLib;
import com.lahm.library.EmulatorCheckCallback;
import com.pojul.fastIM.message.request.RegisterReq;
import com.pojul.objectsocket.message.RequestMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.ClientSocket;
import com.pojul.objectsocket.socket.SocketRequest;
import com.pojul.objectsocket.utils.Constant;
import com.pojul.objectsocket.utils.EncryptionUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.PhoneUtil;

public class RegistActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.nick_et)
    EditText nickEt;
    @BindView(R.id.nick_ll)
    LinearLayout nickLl;
    @BindView(R.id.birthday_tv)
    TextView birthdayTv;
    @BindView(R.id.birthday_ll)
    LinearLayout birthdayLl;
    @BindView(R.id.sex_tv)
    TextView sexTv;
    @BindView(R.id.sex_ll)
    LinearLayout sexLl;
    @BindView(R.id.passwd_et)
    EditText passwdEt;
    @BindView(R.id.passwd_ll)
    LinearLayout passwdLl;
    @BindView(R.id.passwd_sure_et)
    EditText passwdSureEt;
    @BindView(R.id.passwd_sure_ll)
    LinearLayout passwdSureLl;
    @BindView(R.id.effective_note)
    TextView effectiveNote;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.agree)
    CheckBox agree;
    @BindView(R.id.privacy_detail)
    TextView privacyDetail;

    private int birthdayType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);

        if(EasyProtectorLib.checkIsRunningInEmulator(this, emulatorInfo -> {})){
            showShortToas("不支持在模拟器上注册");
            finish();
            return;
        }
        if(EasyProtectorLib.checkIsXposedExist()){
            showShortToas("系统检测到XPOSE存在，请卸载XPOSE后在注册");
            finish();
            return;
        }

        applyPermission();

    }

    @OnClick({R.id.back, R.id.birthday_tv, R.id.sex_tv, R.id.register,R.id.privacy_detail,R.id.user_agree})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.birthday_tv:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistActivity.this, (view1, year, month, dayOfMonth) -> {
                    month = month + 1;
                    String birthday = year + "-" + (month < 10 ? ("0" + month):month) + "-" + (dayOfMonth<10?("0" + dayOfMonth):dayOfMonth);
                    getBirthdayType(birthday);
                }, (calendar.get(Calendar.YEAR) - 20),0,1);
                datePickerDialog.show();
                break;
            case R.id.sex_tv:
                DialogUtil.getInstance().showSexDialog(RegistActivity.this);
                DialogUtil.getInstance().setDialogClick(str -> {
                    sexTv.setText(str);
                });
                break;
            case R.id.register:
                if(!agree.isChecked()){
                    showShortToas("请勾选隐私协议");
                    return;
                }
                conn();
                break;
            case R.id.privacy_detail:
                Intent intent = new Intent(RegistActivity.this, WebviewActivity.class);
                intent.putExtra("url", "file:////android_asset/privacy.html");
                startActivity(intent);
                break;
            case R.id.user_agree:
                intent = new Intent(RegistActivity.this, WebviewActivity.class);
                intent.putExtra("url", "file:////android_asset/user_agreement.html");
                startActivity(intent);
                break;
        }
    }

    private void registerReq(RequestMessage req) {
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
                        showShortToas("注册成功");
                        finish();
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                    DialogUtil.getInstance().dimissLoadingDialog();
                });
            }
        });
    }

    private void conn() {
        if(MyApplication.getApplication().isConnected()){
            MyApplication.getApplication().closeConn();
        }
        if(MyApplication.isConnecting){
            return;
        }
        if(nickEt.getText().toString().isEmpty()){
            showShortToas("昵称不能为空");
            return;
        }
        if(birthdayTv.getText().toString().isEmpty()){
            showShortToas("生日不能为空");
            return;
        }
        if(sexTv.getText().toString().isEmpty()){
            showShortToas("性别不能为空");
            return;
        }
        if(passwdEt.getText().toString().isEmpty()){
            showShortToas("密码不能为空");
            return;
        }
        if(!passwdEt.getText().toString().equals(passwdSureEt.getText().toString())){
            showShortToas("两次密码输入不一致");
            return;
        }
        if(passwdEt.getText().toString().length() < 6){
            showShortToas("密码长度不能小于六位");
            return;
        }
        PhoneUtil phoneUtil = new PhoneUtil();
        String imei = phoneUtil.getIMEI(this);
        if(imei == null || imei.isEmpty()){
            showShortToas("获取设备信息异常");
            return;
        }
        String imsi = phoneUtil.getIMSI(this);
        if(imsi == null || imsi.isEmpty()){
            showShortToas("未检测到sim卡");
            return;
        }
        RegisterReq req = new RegisterReq();
        req.setBirthday(birthdayTv.getText().toString());
        req.setBirthdayType(birthdayType);
        req.setImei(imei);
        req.setImsi(imsi);
        req.setNickName(nickEt.getText().toString());
        req.setPasswd(EncryptionUtil.md5Encryption(passwdEt.getText().toString()));
        req.setSex(sexTv.getText().equals("男生")?1:0);
        req.setFrom(imsi);

        MyApplication.isConnecting = true;
        DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
        new SocketRequest().requestConn(new SocketRequest.IRequestConn() {
            @Override
            public void onError(String msg) {
                MyApplication.isConnecting = false;
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showLongToas(msg);
                });
            }

            @Override
            public void onFinished(ClientSocket clientSocket) {
                runOnUiThread(() -> {
                    if (clientSocket == null || clientSocket.getmSocket() == null) {
                        showLongToas("网络连接失败");
                        MyApplication.isConnecting = false;
                        DialogUtil.getInstance().dimissLoadingDialog();
                        return;
                    }
                    MyApplication.ClientSocket = clientSocket;
                    MyApplication.isConnecting = false;
                    //MyApplication.ClientSocket.setHeartbeat(30 * 1000);
                    /*MyApplication.getApplication().registerSocketRecListerer();
                    MyApplication.getApplication().registerSocketSendListerer();
                    MyApplication.getApplication().registerSocketStatusListerer();
                    MyApplication.getApplication().registSendProgListerer();*/
                    registerReq(req);
                });
            }
        }, Constant.HOST, Constant.PORT);
    }

    private void getBirthdayType(String birthday) {
        DialogUtil.getInstance().showBirthdayTypeDialog(this);
        DialogUtil.getInstance().setDialogClick(str -> {
           if("农历".equals(str)){
               birthdayType = 0;
           }else{
               birthdayType = 1;
           }
           birthdayTv.setText(birthday);
        });
    }

    @SuppressLint("CheckResult")
    private void applyPermission() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)
                .subscribe(permission -> {
                    if(!permission.granted){
                        showShortToas("无法获取所需权限");
                        /*MyApplication.getApplication().closeConn();
                        System.exit(0);*/
                    }
                });
    }
}

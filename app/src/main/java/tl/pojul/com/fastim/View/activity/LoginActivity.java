package tl.pojul.com.fastim.View.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pojul.fastIM.message.request.LoginMessage;
import com.pojul.fastIM.message.response.LoginResponse;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.ClientSocket;
import com.pojul.objectsocket.socket.SocketRequest;
import com.pojul.objectsocket.utils.Constant;
import com.pojul.objectsocket.utils.EncryptionUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.PhoneUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.login_account)
    EditText loginAccount;
    @BindView(R.id.login_passwd)
    EditText loginPasswd;
    @BindView(R.id.login_passwd_visiable)
    CheckBox loginPasswdVisiable;
    @BindView(R.id.login_submit)
    Button loginSubmit;
    @BindView(R.id.login_register)
    TextView loginRegister;
    @BindView(R.id.other_login)
    TextView otherLogin;
    @BindView(R.id.activity_login)
    RelativeLayout activityLogin;
    @BindView(R.id.login_bg)
    ImageView loginBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        applyPermission();
    }

    private void conn() {
        if(MyApplication.isConnecting){
            return;
        }
        MyApplication.isConnecting = true;
        DialogUtil.getInstance().showLoadingDialog(this, "连接服务器中...");
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
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if (clientSocket == null || clientSocket.getmSocket() == null) {
                        showLongToas("网络连接失败");
                        MyApplication.isConnecting = false;
                        return;
                    }
                    MyApplication.isConnecting = false;
                    MyApplication.ClientSocket = clientSocket;
                    MyApplication.ClientSocket.setHeartbeat(60 * 1000);
                    MyApplication.getApplication().registerSocketRecListerer();
                    MyApplication.getApplication().registerSocketSendListerer();
                    MyApplication.getApplication().registerSocketStatusListerer();
                    MyApplication.getApplication().registSendProgListerer();
                    login();
                });
            }
        }, Constant.HOST, Constant.PORT);
    }

    private void login() {
        String userName = "";
        if("其他账号登陆".equals(otherLogin.getText().toString())){
            userName = new PhoneUtil().getIMSI(this);
        }else{
            if(loginAccount.getText().toString().isEmpty()){
                showShortToas("请输入账号");
                return;
            }
            userName = loginAccount.getText().toString();
        }
        DialogUtil.getInstance().showLoadingDialog(this, "登陆中...");
        LoginMessage mLoginMessage = new LoginMessage();
        mLoginMessage.setUserName(userName);
        mLoginMessage.setPassWd(EncryptionUtil.md5Encryption(loginPasswd.getText().toString()));
        mLoginMessage.setDeviceType("Android");
        mLoginMessage.setFrom(userName);

        //test
        /*mLoginMessage.setFrom(loginAccount.getText().toString());
        mLoginMessage.setPassWd(loginPasswd.getText().toString());
        mLoginMessage.setUserName(loginAccount.getText().toString());*/

        new SocketRequest().request(MyApplication.ClientSocket, mLoginMessage, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                MyApplication.isConnecting = false;
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas("网络连接失败");
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    LoginResponse loginResponse = (LoginResponse) mResponse;
                    showShortToas(mResponse.getMessage());
                    if (loginResponse.getCode() == 200) {
                        SPUtil.getInstance().putUser(loginResponse.getUser());
                        SPUtil.getInstance().putArrays(loginResponse.getTokenId(), loginResponse.getUser().getUserName());
                        MyApplication.ClientSocket.setTokenId(loginResponse.getTokenId());
                        if(MyApplication.getApplication().getMainActivity() == null){
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        finish();
                    }else{
                        showLongToas(mResponse.getMessage());
                    }
                    MyApplication.isConnecting = false;
                });
            }
        });
    }

    @OnClick({R.id.login_submit, R.id.login_passwd_visiable, R.id.login_register, R.id.other_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_submit:
                PhoneUtil phoneUtil = new PhoneUtil();
                if("其他账号登陆".equals(otherLogin.getText().toString()) && (phoneUtil.getIMSI(LoginActivity.this) == null || phoneUtil.getIMSI(LoginActivity.this).isEmpty()
                        || !phoneUtil.hasSimCard(LoginActivity.this)) ){
                    showShortToas("未检测到手机卡");
                    return;
                }
                /*if ("".equals(loginAccount.getText().toString())) {
                    showShortToas("手机号不能为空");
                    return;
                }*/
                if ("".equals(loginPasswd.getText().toString())) {
                    showShortToas("密码不能为空");
                    return;
                }
                conn();
                break;
            case R.id.login_passwd_visiable:
                String digits = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                if (loginPasswdVisiable.isChecked()) {
                    loginPasswd.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    loginPasswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                loginPasswd.setKeyListener(DigitsKeyListener.getInstance(digits));
                break;
            case R.id.login_register:
                startActivity(RegistActivity.class, null);
                break;
            case R.id.other_login:
                if("其他账号登陆".equals(otherLogin.getText().toString())){
                    otherLogin.setText("普通登陆");
                    loginAccount.setVisibility(View.VISIBLE);
                }else{
                    otherLogin.setText("其他账号登陆");
                    loginAccount.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /*if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && MyApplication.isConnecting) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyUp(keyCode, event);*/
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyUp(keyCode, event);
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

package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.login_account)
    EditText loginAccount;
    @BindView(R.id.login_passwd)
    EditText loginPasswd;
    @BindView(R.id.login_passwd_visiable)
    CheckBox loginPasswdVisiable;
    @BindView(R.id.login_autologin)
    CheckBox loginAutologin;
    @BindView(R.id.login_forgetPasswd)
    TextView loginForgetPasswd;
    @BindView(R.id.login_submit)
    Button loginSubmit;
    @BindView(R.id.login_register)
    TextView loginRegister;
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
    }

    private void conn() {
        DialogUtil.getInstance().showLoadingDialog(this, "连接服务器中...");
        new SocketRequest().requestConn(new SocketRequest.IRequestConn() {
            @Override
            public void onError(String msg) {
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
                        return;
                    }
                    MyApplication.ClientSocket = clientSocket;
                    MyApplication.ClientSocket.setHeartbeat(3 * 1000);
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
        DialogUtil.getInstance().showLoadingDialog(this, "登陆中...");
        LoginMessage mLoginMessage = new LoginMessage();
        mLoginMessage.setUserName(loginAccount.getText().toString());
        mLoginMessage.setPassWd(loginPasswd.getText().toString());
        mLoginMessage.setDeviceType("Android");
        mLoginMessage.setFrom(loginAccount.getText().toString());
        new SocketRequest().request(MyApplication.ClientSocket, mLoginMessage, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
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
                        MyApplication.ClientSocket.setTokenId(loginResponse.getTokenId());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    @OnClick({R.id.login_submit, R.id.login_passwd_visiable})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_submit:
                if ("".equals(loginAccount.getText().toString())) {
                    showShortToas("用户名不能为空");
                    return;
                }
                if ("".equals(loginPasswd.getText().toString())) {
                    showShortToas("密码不能为空");
                    return;
                }
                conn();
                break;
            case R.id.login_passwd_visiable:
                if (loginPasswdVisiable.isChecked()) {
                    loginPasswd.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    loginPasswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;
        }
    }
}

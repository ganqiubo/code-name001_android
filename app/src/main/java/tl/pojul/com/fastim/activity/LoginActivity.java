package tl.pojul.com.fastim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.Constant;
import tl.pojul.com.fastim.util.DialogUtil;

import com.pojul.fastIM.message.request.LoginMessage;
import com.pojul.fastIM.message.response.LoginResponse;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.ClientSocket;
import com.pojul.objectsocket.socket.SocketRequest;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Button loginSubmit;
    private EditText loginAccount;
    private EditText loginPasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginAccount = findViewById(R.id.activity_login_account);
        loginPasswd = findViewById(R.id.activity_login_passwd);
        loginSubmit = findViewById(R.id.activity_login_submit);
        loginSubmit.setOnClickListener(this);
    }

    private void conn(){
        DialogUtil.getInstance().showLoadingDialog(this, "连接服务器中...");
        new SocketRequest().resuestConn(new SocketRequest.IRequestConn() {
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
                    if(clientSocket == null || clientSocket.getmSocket() == null){
                        showLongToas("网络连接失败");
                        return;
                    }
                    MyApplication.ClientSocket = clientSocket;
                    login();
                });
            }
        }, Constant.HOST, Constant.PORT);
    }

    private void login(){
        DialogUtil.getInstance().showLoadingDialog(this, "登陆中...");
        LoginMessage mLoginMessage = new LoginMessage();
        mLoginMessage.setUserName(loginAccount.getText().toString());
        mLoginMessage.setPassWd(loginPasswd.getText().toString());
        mLoginMessage.setDeviceType("Android");
        new SocketRequest().resuest(MyApplication.ClientSocket, mLoginMessage, new SocketRequest.IRequest(){
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showLongToas("网络连接失败");
                });
            }
            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    /*if(MyApplication.ClientSocket == null || MyApplication.ClientSocket.getmSocket() == null){
                        showLongToas("与服务器连接已断开");
                        return;
                    }*/
                    LoginResponse loginResponse = (LoginResponse)mResponse;
                    showLongToas(mResponse.getMessage());
                    if(loginResponse.getCode() == 200) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_login_submit:
                conn();
                break;
        }
    }
}

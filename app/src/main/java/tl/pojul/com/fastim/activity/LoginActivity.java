package tl.pojul.com.fastim.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pojul.fastIM.message.login.LoginMessage;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.LoadingDialog;
import tl.pojul.com.fastim.net.SocketRequest;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Button loginSubmit;
    private EditText loginAccount;
    private EditText loginPasswd;
    private LoadingDialog dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginAccount = findViewById(R.id.activity_login_account);
        loginPasswd = findViewById(R.id.activity_login_passwd);
        loginSubmit = findViewById(R.id.activity_login_submit);
        loginSubmit.setOnClickListener(this);
    }

    private void login(){
        LoadingDialog.Builder builder1=new LoadingDialog.Builder(this)
                .setMessage("加载中...")
                .setCancelable(false);
        dialog1=builder1.create();
        dialog1.show();
        new SocketRequest().initClientSocket(new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                dialog1.dismiss();
                showLongToas(msg);
            }

            @Override
            public void onFinished() {
                dialog1.dismiss();
                if(MyApplication.ClientSocket == null || MyApplication.ClientSocket.getmSocket() == null){
                    showLongToas("网络连接失败");
                    return;
                }
                LoginMessage mLoginMessage = new LoginMessage();
                mLoginMessage.setUserName(loginAccount.getText().toString());
                mLoginMessage.setPassWd(loginPasswd.getText().toString());
                mLoginMessage.setDeviceType("windows");
                MyApplication.ClientSocket.sendData(mLoginMessage);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_login_submit:
                login();
                break;
        }
    }
}

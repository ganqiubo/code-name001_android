package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.UpdateUserInfoReq;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.DialogUtil;

public class UserInfoEditActivity extends BaseActivity {

    @BindView(R.id.hobby_et)
    EditText hobbyEt;
    @BindView(R.id.occupation_et)
    EditText occupationEt;
    @BindView(R.id.educational_level_et)
    EditText educationalLevelEt;
    @BindView(R.id.graduate_school_et)
    EditText graduateSchoolEt;
    @BindView(R.id.upload)
    TextView upload;
    @BindView(R.id.height_et)
    EditText heightEt;
    @BindView(R.id.weight_et)
    EditText weightEt;

    private User modifyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        ButterKnife.bind(this);

        init();

    }

    private void init(){
        String json = getIntent().getStringExtra("user");
        if(json == null || json.isEmpty()){
            finish();
            return;
        }
        modifyUser = new Gson().fromJson(json, User.class);
        if(modifyUser == null){
            finish();
           return;
        }
        heightEt.setText(("" + modifyUser.getHeight()));
        weightEt.setText(("" + modifyUser.getWeight()));
        hobbyEt.setText(modifyUser.getHobby());
        occupationEt.setText(modifyUser.getOccupation());
        educationalLevelEt.setText(modifyUser.getEducationalLevel());
        graduateSchoolEt.setText(modifyUser.getGraduateSchool());
    }

    @OnClick({R.id.upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.upload:

                uploadUserInfo();

                break;
        }
    }

    private void uploadUserInfo() {
        User user = new User();
        try{
            user.setHeight(Integer.parseInt(heightEt.getText().toString()));
        }catch(Exception e){}
        try{
            user.setWeight(Integer.parseInt(weightEt.getText().toString()));
        }catch(Exception e){}
        user.setHobby(hobbyEt.getText().toString());
        user.setOccupation(occupationEt.getText().toString());
        user.setEducationalLevel(educationalLevelEt.getText().toString());
        user.setGraduateSchool(graduateSchoolEt.getText().toString());
        UpdateUserInfoReq req = new UpdateUserInfoReq();
        req.setUser(user);
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
                        showShortToas("修改成功");
                        Intent intent = new Intent();
                        intent.putExtra("user", new Gson().toJson(user));
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }
}

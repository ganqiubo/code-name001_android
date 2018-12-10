package tl.pojul.com.fastim.View.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.MakeQRCodeReq;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.SPUtil;
import tl.pojul.com.fastim.util.ZxingUtils;

public class CommuDeatilActivity extends BaseActivity implements View.OnLongClickListener{

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

    private CommunityRoom communityRoom;
    private User user;

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
        }catch (Exception e){
        }
        if (communityRoom == null || communityRoom.getCommunityUid() == null
                || communityRoom.getCommunityUid().split("_").length != 2 || user == null) {
            finish();
            return;
        }
        communPhoto.getLayoutParams().height = MyApplication.SCREEN_WIDTH;
        communPhoto.requestLayout();
        communName.setText(communityRoom.getCommunityUid().split("_")[1]);
        phone.setOnLongClickListener(this);
        if(!user.getUserName().equals("460079878303087")){
            getQRCode.setVisibility(View.GONE);
        }
        if(communityRoom.getManager() == null || communityRoom.getManager().isEmpty() ||
                !communityRoom.getManager().equals(user.getUserName())){
            editDetail.setVisibility(View.GONE);
            editPhone.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.back, R.id.scan, R.id.edit_detail, R.id.edit_phone, R.id.getQR_code})
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
                break;
            case R.id.edit_phone:
                break;
            case R.id.getQR_code:
                getQRCode();
                break;
        }
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
                runOnUiThread(()->{
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    Bitmap bitmap = ZxingUtils.createBitmap(contents);
                    if(bitmap != null){
                        QRCodeImg.setImageBitmap(bitmap);
                        QRCodeImg.setVisibility(View.VISIBLE);
                    }else{
                        showShortToas("生成二维码失败");
                    }
                });
            }
        });
    }


    @Override
    public boolean onLongClick(View v) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        switch (v.getId()){
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
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null) {
            if(intentResult.getContents() == null) {
                Toast.makeText(this,"内容为空",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"扫描成功",Toast.LENGTH_LONG).show();
                String scanResult = intentResult.getContents();
                try{
                    String[] params = scanResult.split(",");
                    String roomUid = params[0].split("=")[1];
                    long milli = Long.parseLong(params[1].split("=")[1]);

                }catch(Exception e){
                    showShortToas("数据错误");
                    return;
                }
                Log.e(TAG, scanResult);
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
}

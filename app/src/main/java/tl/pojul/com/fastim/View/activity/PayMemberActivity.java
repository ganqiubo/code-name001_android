package tl.pojul.com.fastim.View.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.PackageUtil;
import tl.pojul.com.fastim.util.PhoneUtil;

public class PayMemberActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.note)
    TextView note;
    @BindView(R.id.copy_account)
    ImageView copyAccount;
    @BindView(R.id.imsi)
    TextView imsi;
    @BindView(R.id.copy_imsi)
    ImageView copyImsi;
    @BindView(R.id.open_alipay)
    TextView openAlipay;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.account)
    TextView account;

    private static final int INIT = 9837;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_member);
        ButterKnife.bind(this);

        mHandler.sendEmptyMessageDelayed(INIT, 100);
    }

    private void init() {
        applyPermission();
        smartRefresh.setEnableLoadmore(false);
        smartRefresh.setEnableRefresh(false);
        String imsiStr = new PhoneUtil().getIMSI(this);
        if (imsiStr == null || imsiStr.isEmpty()) {
            showShortToas("获取IMSI号失败，请检查SIM卡是否已插入");
        } else {
            imsi.setText(imsiStr);
        }
        if (new PackageUtil().applicationExit(this, "com.eg.android.AlipayGphone")) {
            openAlipay.setVisibility(View.VISIBLE);
        } else {
            openAlipay.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.back, R.id.copy_account, R.id.copy_imsi, R.id.open_alipay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.copy_account:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", account.getText().toString());
                cm.setPrimaryClip(mClipData);
                showShortToas("收款账户已被复制到剪贴板");
                break;
            case R.id.copy_imsi:
                if(imsi.getText().toString().isEmpty()){
                    showShortToas("获取IMSI号失败，请检查SIM卡是否已插入");
                    return;
                }
                cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                mClipData = ClipData.newPlainText("Label", imsi.getText().toString());
                cm.setPrimaryClip(mClipData);
                showShortToas("IMSI号已被复制到剪贴板");
                break;
            case R.id.open_alipay:
                PackageManager packageManager = getPackageManager();
                String packageName = "com.eg.android.AlipayGphone";
                Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
                if (launchIntentForPackage != null)
                    startActivity(launchIntentForPackage);
                else
                    Toast.makeText(this, "未检测到支付宝客户端", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<PayMemberActivity> activity;

        MyHandler(PayMemberActivity activity) {
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
            }
        }
    }

    @SuppressLint("CheckResult")
    private void applyPermission() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)
                .subscribe(permission -> {
                    if (!permission.granted) {
                        showShortToas("无法获取所需权限");
                        /*MyApplication.getApplication().closeConn();
                        System.exit(0);*/
                    }
                });
    }

}

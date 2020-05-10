package tl.pojul.com.fastim.View.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pojul.objectsocket.utils.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.CustomTimeDown;
import tl.pojul.com.fastim.util.DensityUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class StartActivity extends BaseActivity implements CustomTimeDown.OnTimeDownListener {

    @BindView(R.id.skip)
    TextView skip;
    @BindView(R.id.start_bg)
    ImageView startBg;
    private CustomTimeDown mCustomTimeDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        skip.setVisibility(View.GONE);

        if(SPUtil.getInstance().getInt(SPUtil.SHOW_PRIVACY_AGREEMENT, 0)==1){
            if(MyApplication.getApplication().isConnected() || MyApplication.getApplication().getMainActivity() != null){
                startActivityAndFinish(MainActivity.class);
                return;
            }
        }

        mCustomTimeDown = new CustomTimeDown(6000, 1000);
        mCustomTimeDown.setOnTimeDownListener(this);
        mCustomTimeDown.start();
        //Glide.with(this).load("https://images.unsplash.com/photo-1519008138718-6fb663213611?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=03a13a9c68857afd60a030c058fe4678&auto=format&fit=crop&w=334&q=80").into(startBg);
    }

    @Override
    public void onTick(long l) {
        /*skip.setText(("跳过 " + l/1000 ));*/
    }

    @OnClick({R.id.skip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip:
                break;
        }
    }

    private void showAgreeMentDialog() {
            String content = "\u3000\u3000欢迎使用脚步圈app。我们非常重视您的个人信息和隐私保护，" +
                    "在您使用“脚步圈”服务之前，请您务必审慎阅读《隐私政策》和《用户协议》，并充分" +
                    "理解协议条款内容。我们将严格按照您同意的各项条款使用您的个人信息，以便为您提供更好的服务";
            String arg1 = "《隐私政策》";
            String arg2 = "《用户协议》";
            SpannableString msg =formatMessage(content,
                    content.indexOf(arg1), (content.indexOf(arg1)+arg1.length()),
                    content.indexOf(arg2), (content.indexOf(arg2)+arg2.length())
            );//处理字符串
            TextView textView = new TextView(this);
            textView.setText(msg);//tv设置处理过的字符串
            textView.setTextColor(Color.parseColor("#333333"));//要设置一个默认颜色，否则点击会闪烁.
            textView.setHighlightColor(this.getResources().getColor(android.R.color.transparent));
            textView.setPadding(
                    DensityUtil.dp2px(MyApplication.getApplication(), 15),
                    DensityUtil.dp2px(MyApplication.getApplication(),  10),
                    DensityUtil.dp2px(MyApplication.getApplication(),  15),
                    DensityUtil.dp2px(MyApplication.getApplication(),  10)
            );
            //可以根据自己需求设置textview的padding值，字体大小
            textView.setMovementMethod(LinkMovementMethod.getInstance());//要设置，不然蓝色字体点击事件不响应
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
            builder.setTitle("用户协议和隐私政策")
                    .setView(textView);//把自己的textview给dialog设置进去
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            //添加"Yes"按钮
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,"同意", (dialogInterface, i) -> {
                SPUtil.getInstance().putInt(SPUtil.SHOW_PRIVACY_AGREEMENT, 1);
                next();
            });
            //添加取消
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"暂不使用", (dialogInterface, i) -> {
                //System.exit(0);
                finish();
            });
            dialog.show();
    }

    public SpannableString formatMessage(String content, int start1, int end1, int start2, int end2) {
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                //蓝色字体的点击事件
                LogUtil.e("msg1 click");
                Intent intent = new Intent(StartActivity.this, WebviewActivity.class);
                intent.putExtra("url", "file:////android_asset/privacy.html");
                startActivity(intent);
            }

            @Override
            public CharacterStyle getUnderlying() {
                return super.getUnderlying();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.blue));
                ds.clearShadowLayer();
            }
        }, start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                //蓝色字体的点击事件
                LogUtil.e("msg2 click");
                Intent intent = new Intent(StartActivity.this, WebviewActivity.class);
                intent.putExtra("url", "file:////android_asset/user_agreement.html");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.blue));
                ds.clearShadowLayer();
            }
        }, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void next(){
        if(MyApplication.getApplication().isConnected()){
            startActivityAndFinish(MainActivity.class);
        }else{
            startActivityAndFinish(LoginActivity.class);
        }
    }

    @Override
    public void OnFinish() {
        //Intent intent = new Intent(this, LoginActivity.class);
        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
        if(SPUtil.getInstance().getInt(SPUtil.SHOW_PRIVACY_AGREEMENT, 0)==0){
            showAgreeMentDialog();
        }else{
            next();
        }
    }

    @Override
    protected void onDestroy() {
        if(mCustomTimeDown != null){
            mCustomTimeDown.setOnTimeDownListener(null);
            mCustomTimeDown = null;

        }
        super.onDestroy();
    }
}

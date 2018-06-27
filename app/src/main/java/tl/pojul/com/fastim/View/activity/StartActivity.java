package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.CustomTimeDown;

public class StartActivity extends BaseActivity implements CustomTimeDown.OnTimeDownListener {

    @BindView(R.id.skip)
    TextView skip;
    @BindView(R.id.start_bg)
    ImageView startBg;
    private CustomTimeDown mCustomTimeDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        mCustomTimeDown = new CustomTimeDown(8000, 1000);
        mCustomTimeDown.setOnTimeDownListener(this);
        mCustomTimeDown.start();
        Glide.with(this).load("https://images.unsplash.com/photo-1519008138718-6fb663213611?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=03a13a9c68857afd60a030c058fe4678&auto=format&fit=crop&w=334&q=80").into(startBg);
    }

    @Override
    public void onTick(long l) {
        skip.setText(("跳过 " + l/1000 ));
    }

    @Override
    public void OnFinish() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

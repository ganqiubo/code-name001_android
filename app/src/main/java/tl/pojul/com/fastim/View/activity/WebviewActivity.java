package tl.pojul.com.fastim.View.activity;

import android.os.Bundle;
import android.webkit.WebView;
import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;

public class WebviewActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        String url = getIntent().getStringExtra("url");
        if(url==null||url.isEmpty()){
            finish();
            return;
        }
        webview.loadUrl(url);
    }
}


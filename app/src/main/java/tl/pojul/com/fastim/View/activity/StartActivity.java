package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.os.Bundle;

import tl.pojul.com.fastim.R;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();

    }
}

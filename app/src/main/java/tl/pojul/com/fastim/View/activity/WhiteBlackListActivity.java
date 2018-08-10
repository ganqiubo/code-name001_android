package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.WhiteBlack;
import com.pojul.fastIM.entity.WhiteBlackSelect;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.WhiteBlackAdapter;
import tl.pojul.com.fastim.util.SPUtil;

public class WhiteBlackListActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.white_black_list)
    RecyclerView whiteBlackList;
    @BindView(R.id.empty)
    ImageView empty;
    @BindView(R.id.line2)
    View line2;
    @BindView(R.id.sure)
    TextView sure;

    private List<WhiteBlackSelect> mList = new ArrayList<>();
    private String type;
    private String subType;
    private WhiteBlackAdapter whiteBlackAdapter;
    private boolean canSelect;
    private boolean canDelete;
    private static final int INIT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_black_list);
        ButterKnife.bind(this);

        type = getIntent().getStringExtra("type");
        if (type == null || (!"white".equals(type) && !"black".equals(type))) {
            finish();
            return;
        }

        if("black".equals(type)){
            title.setText("黑名单列表");
        }

        subType = getIntent().getStringExtra("subType");
        if (subType == null || (!"select".equals(subType))) {
            finish();
            return;
        }

        if ("select".equals(subType)) {
            canSelect = true;
            canDelete = true;
            line2.setVisibility(View.VISIBLE);
            sure.setVisibility(View.VISIBLE);
        }

        mHandler.sendEmptyMessageDelayed(INIT, 100);

    }

    private void init() {
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        whiteBlackList.setLayoutManager(layoutmanager);
        whiteBlackAdapter = new WhiteBlackAdapter(this, mList, type, canSelect, canDelete);
        whiteBlackList.setAdapter(whiteBlackAdapter);

        refreshData();
    }

    private void refreshData() {
        mList.clear();
        List<WhiteBlack> whiteBlacks = SPUtil.getInstance().getWhiteBlacks(type);
        for (int i = 0; i < whiteBlacks.size(); i++) {
            WhiteBlack whiteBlack = whiteBlacks.get(i);
            if (whiteBlack == null) {
                continue;
            }
            WhiteBlackSelect whiteBlackSelect = new WhiteBlackSelect();
            whiteBlackSelect.setWhiteBlack(whiteBlack);
            mList.add(whiteBlackSelect);
        }
        if (mList.size() <= 0) {
            whiteBlackList.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            whiteBlackList.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
        whiteBlackAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.sure})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.sure:
                if("select".equals(subType)){
                    Intent intent = new Intent();
                    intent.putExtra("UserSelect", new Gson().toJson(whiteBlackAdapter.getSelectUsers()));
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }


    private MyHandler mHandler = new MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<WhiteBlackListActivity> activity;

        MyHandler(WhiteBlackListActivity activity) {
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
}

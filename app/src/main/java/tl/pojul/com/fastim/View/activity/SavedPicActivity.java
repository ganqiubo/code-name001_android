package tl.pojul.com.fastim.View.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pojul.fastIM.entity.UploadPic;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.SavedDataAdapter;
import tl.pojul.com.fastim.dao.UploadPicDao;
import tl.pojul.com.fastim.util.SPUtil;

public class SavedPicActivity extends BaseActivity {

    @BindView(R.id.saved_datas)
    RecyclerView savedDatas;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.saved_datas_empty)
    RelativeLayout savedDatasEmpty;
    @BindView(R.id.save_data_back)
    ImageView saveDataBack;
    @BindView(R.id.save_data_add)
    TextView saveDataAdd;

    private SavedDataAdapter savedDataAdapter;
    private List<UploadPic> uploadPics;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_pic);
        ButterKnife.bind(this);

        init();

    }

    public void init() {
        try {
            userName = SPUtil.getInstance().getUser().getUserName();
        } catch (Exception e) {
        }
        if (userName == null) {
            showShortToas("用户不存在");
            finish();
            return;
        }
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        savedDatas.setLayoutManager(layoutmanager);
        refreshData();

        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        uploadPics = new UploadPicDao().queryMyUploadPic(userName, 0);
        smartRefresh.finishRefresh();
        if (savedDataAdapter == null) {
            savedDataAdapter = new SavedDataAdapter(this, uploadPics);
            savedDatas.setAdapter(savedDataAdapter);
        } else {
            savedDataAdapter.setDatas(uploadPics);
        }
        if (uploadPics.size() <= 0) {
            savedDatas.setVisibility(View.GONE);
            savedDatasEmpty.setVisibility(View.VISIBLE);
        } else {
            savedDatas.setVisibility(View.VISIBLE);
            savedDatasEmpty.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.save_data_back, R.id.save_data_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save_data_back:
                finish();
                break;
            case R.id.save_data_add:
                Bundle bundle = new Bundle();
                bundle.putInt("uploadPicType", 2);
                startActivity(UploadPicActivity.class, bundle);
                break;
        }
    }
}

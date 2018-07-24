package tl.pojul.com.fastim.View.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.pojul.fastIM.message.request.UploadPicReq;
import com.pojul.fastIM.message.response.UploadPicResp;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.UploadingPicAdapter;
import tl.pojul.com.fastim.socket.upload.PicUploadManager;
import tl.pojul.com.fastim.util.SPUtil;

public class UploadingPicActivity extends BaseActivity {

    @BindView(R.id.uploading_pics)
    RecyclerView uploadingPics;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.uploading_pic_empty)
    RelativeLayout uploadingPicEmpty;
    private UploadingPicAdapter uploadingPicAdapter;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading_pic);
        ButterKnife.bind(this);

        init();

    }


    private void init() {
        try {
            userName = SPUtil.getInstance().getUser().getUserName();
        } catch (Exception e) {
        }
        if (userName == null) {
            showShortToas("用户不存在");
            finish();
            return;
        }
        PicUploadManager.getInstance().registUploadPicProgress(iUploadPicProgress);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        uploadingPics.setLayoutManager(layoutmanager);
        refreshData();

        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshData();
            }
        });
    }

    private PicUploadManager.IUploadPicProgress iUploadPicProgress = new PicUploadManager.IUploadPicProgress() {
        @Override
        public void progress(PicUploadManager.UploadPicTask uploadPicTask) {
            runOnUiThread(()->{
                if(uploadingPicAdapter != null){
                    uploadingPicAdapter.updateItem(uploadPicTask.uid);
                }
            });
        }

        @Override
        public void onError(UploadPicReq message, String msg) {
            runOnUiThread(()->{
                if(uploadingPicAdapter != null){
                    uploadingPicAdapter.updateItem(message.getMessageUid());
                }
            });
        }

        @Override
        public void onFinished(PicUploadManager.UploadPicTask uploadPicTask) {
            runOnUiThread(()->{
                if(uploadingPicAdapter != null){
                    refreshData();
                    showShortToas("上传成功");
                }
            });
        }
    };

    private void refreshData() {
        List<PicUploadManager.UploadPicTask> uploadPicTasks = PicUploadManager.getInstance().getAllTasks();
        smartRefresh.finishRefresh();
        if (uploadingPicAdapter == null) {
            uploadingPicAdapter = new UploadingPicAdapter(this, uploadPicTasks);
            uploadingPics.setAdapter(uploadingPicAdapter);
        } else {
            uploadingPicAdapter.setDatas(uploadPicTasks);
        }
        if (uploadPicTasks.size() <= 0) {
            uploadingPics.setVisibility(View.GONE);
            uploadingPicEmpty.setVisibility(View.VISIBLE);
        } else {
            uploadingPics.setVisibility(View.VISIBLE);
            uploadingPicEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @OnClick({R.id.uploding_pic_back, R.id.uploding_pic_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.uploding_pic_back:
                finish();
                break;
            case R.id.uploding_pic_add:
                Bundle bundle = new Bundle();
                bundle.putInt("uploadPicType", 2);
                startActivity(UploadPicActivity.class, bundle);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PicUploadManager.getInstance().unRegistUploadPicProgress(iUploadPicProgress);
    }
}

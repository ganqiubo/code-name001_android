package tl.pojul.com.fastim.View.fragment;


import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pojul.fastIM.entity.UploadPic;
import com.pojul.fastIM.message.request.UploadPicRecordReq;
import com.pojul.fastIM.message.request.UploadPicReq;
import com.pojul.fastIM.message.response.UploadPicRecordResp;
import com.pojul.fastIM.message.response.UploadPicResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.UploadPicRecordAdapter;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.SavedPicActivity;
import tl.pojul.com.fastim.View.activity.UploadPicActivity;
import tl.pojul.com.fastim.View.activity.UploadingPicActivity;
import tl.pojul.com.fastim.View.widget.SmoothLinearLayoutManager;
import tl.pojul.com.fastim.socket.upload.PicUploadManager;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.SPUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class TakePicFragment extends BaseFragment {

    @BindView(R.id.upload_pic_normal_ll)
    LinearLayout uploadPicNormalLl;
    @BindView(R.id.upload_pic_location_ll)
    LinearLayout uploadPicLocationLl;
    Unbinder unbinder;
    @BindView(R.id.upload_records)
    RecyclerView uploadRecords;
    @BindView(R.id.saved_data_rl)
    RelativeLayout savedDataRl;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.record_empty)
    LinearLayout recordEmpty;

    private List<UploadPic> uploadPicRecords = new ArrayList<>();
    private UploadPicRecordAdapter uploadPicRecordAdapter;

    public TakePicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_pic, container, false);
        unbinder = ButterKnife.bind(this, view);
        PicUploadManager.getInstance().registUploadPicProgress(iUploadPicProgress);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        uploadRecords.setVisibility(View.GONE);
        smartRefresh.setEnableRefresh(false);
        smartRefresh.setPrimaryColors(Color.parseColor("#898989"));

        SmoothLinearLayoutManager layoutmanager = new SmoothLinearLayoutManager(getActivity());
        layoutmanager.setSppeed(80f);
        uploadRecords.setLayoutManager(layoutmanager);
        uploadPicRecordAdapter = new UploadPicRecordAdapter(getActivity(), uploadPicRecords);
        uploadRecords.setAdapter(uploadPicRecordAdapter);
        loadUploadPicRecord();
        smartRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadUploadPicRecord();
            }
        });
    }

    private void loadUploadPicRecord() {
        if (!MyApplication.getApplication().isConnected()) {
            smartRefresh.finishLoadmore();
            showShortToas(getString(R.string.disconnected));
            return;
        }
        UploadPicRecordReq uploadPicRecordReq = new UploadPicRecordReq();
        uploadPicRecordReq.setUserName(SPUtil.getInstance().getUser().getUserName());
        String lastTime ;
        if(uploadPicRecords == null || uploadPicRecords.size() <= 0) {
            lastTime = DateUtil.getFormatDate();
        }else{
            lastTime = uploadPicRecords.get((uploadPicRecords.size() - 1)).getUploadPicTime();
        }
        uploadPicRecordReq.setLastSendTime(lastTime);
        new SocketRequest().request(MyApplication.ClientSocket, uploadPicRecordReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(()->{
                    smartRefresh.finishLoadmore();
                    //showShortToas("加载失败");
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(()->{
                    smartRefresh.finishLoadmore();
                    if(mResponse.getCode() == 200){
                        addRecordsEnd(((UploadPicRecordResp)mResponse).getUploadPics());
                    }else{
                        smartRefresh.setEnableLoadmore(false);
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    public void addRecordsEnd(List<UploadPic> uploadPics){
        uploadPicRecordAdapter.addRecordsEnd(uploadPics);
        setEmptyRecord(uploadPics.size() <= 0 ? true: false);
    }

    public void addRecordTop(UploadPic uploadPic){
        uploadPicRecordAdapter.addRecordsTop(uploadPic);
        uploadRecords.smoothScrollToPosition(0);
        setEmptyRecord(false);
    }

    public void setEmptyRecord(boolean visiable){
        if(visiable){
            recordEmpty.setVisibility(View.VISIBLE);
            uploadRecords.setVisibility(View.GONE);
        }else{
            uploadRecords.setVisibility(View.VISIBLE);
            recordEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        PicUploadManager.getInstance().unRegistUploadPicProgress(iUploadPicProgress);
    }

    @OnClick({R.id.upload_pic_normal_ll, R.id.upload_pic_location_ll, R.id.saved_data_rl, R.id.upload_tasks_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.upload_pic_normal_ll:
                uploadPicNormalLl.getChildAt(0).performClick();
                uploadPicNormalLl.getChildAt(1).performClick();
                Bundle bundle = new Bundle();
                bundle.putInt("uploadPicType", 1);
                ((BaseActivity) getActivity()).startActivity(UploadPicActivity.class, bundle);
                break;
            case R.id.upload_pic_location_ll:
                uploadPicLocationLl.getChildAt(0).performClick();
                uploadPicLocationLl.getChildAt(1).performClick();
                bundle = new Bundle();
                bundle.putInt("uploadPicType", 2);
                ((BaseActivity) getActivity()).startActivity(UploadPicActivity.class, bundle);
                break;
            case R.id.saved_data_rl:
                ((BaseActivity) getActivity()).startActivity(SavedPicActivity.class, null);
                break;
            case R.id.upload_tasks_rl:
                ((BaseActivity) getActivity()).startActivity(UploadingPicActivity.class, null);
                break;
        }
    }

    private PicUploadManager.IUploadPicProgress iUploadPicProgress = new PicUploadManager.IUploadPicProgress() {

        @Override
        public void progress(PicUploadManager.UploadPicTask uploadPicTask) {}

        @Override
        public void onError(UploadPicReq message, String msg) {
            new Handler(Looper.getMainLooper()).post(() -> {
                showShortToas("上传失败");
            });
        }

        @Override
        public void onFinished(PicUploadManager.UploadPicTask uploadPicTask) {
            new Handler(Looper.getMainLooper()).post(() -> {
                showShortToas("上传成功");
                addRecordTop(uploadPicTask.uploadPicReq.getUploadPic());
            });
        }
    };

}

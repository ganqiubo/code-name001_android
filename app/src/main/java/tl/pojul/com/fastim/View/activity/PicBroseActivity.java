package tl.pojul.com.fastim.View.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pojul.fastIM.entity.ExtendUploadPic;
import com.pojul.fastIM.entity.PicFilter;
import com.pojul.fastIM.entity.ResourceIdTitle;
import com.pojul.fastIM.entity.ThirdPicLikes;
import com.pojul.fastIM.entity.UnsplashEntity;
import com.pojul.fastIM.entity.UnsplashResult;
import com.pojul.fastIM.entity.UnsplashUser;
import com.pojul.fastIM.entity.UploadPic;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.GetPicsReq;
import com.pojul.fastIM.message.request.ThirdPicLikesCountReq;
import com.pojul.fastIM.message.response.GetPicsResp;
import com.pojul.fastIM.message.response.ThirdPicLikesCountResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.PicsAdapter;
import tl.pojul.com.fastim.View.Adapter.UserPicsAdapter;
import tl.pojul.com.fastim.http.request.HttpRequestManager;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.socket.Converter.UploadPicConverter;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.ExtendUploadPicUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class PicBroseActivity extends BaseActivity {

    private static final int INIT = 153;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.filter)
    ImageView filter;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.pic_list)
    RecyclerView picList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private PicFilter picFilter = new PicFilter();
    private User user;
    private List<ExtendUploadPic> uploadPics = new ArrayList<>();
    private PicsAdapter picsAdapter;
    private String filterStr = "";
    private  boolean hideLabel;
    private boolean isChoice;
    private int page = 0;
    private int num = 10;
    private BDLocation bdLocation;
    private boolean newStart = true;
    private boolean enableThidrGally = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_brose);
        ButterKnife.bind(this);

        mHandler.sendEmptyMessageDelayed(INIT, 100);
    }


    private void init() {
        filterStr = getIntent().getStringExtra("filter");
        user = SPUtil.getInstance().getUser();
        if (filter == null || filterStr == null) {
            finish();
            return;
        }

        switch (filterStr){
            case "精选":
                hideLabel = false;
                isChoice = true;
                enableThidrGally = false;
                break;
            case "风景":
            case "生活":
            case "建筑":
                hideLabel = true;
                title.setText(filterStr);
                break;
            case "附近":
                hideLabel = false;
                picFilter.setNearBy(true);
                title.setText(filterStr);
                break;
            case "更多":
                hideLabel = false;
                break;
        }

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        picList.setLayoutManager(staggeredGridLayoutManager);
        picsAdapter = new PicsAdapter(this, uploadPics);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        picList.setAdapter(picsAdapter);

        smartRefresh.setPrimaryColors(Color.parseColor("#898989"));
        smartRefresh.setEnableRefresh(false);
        smartRefresh.setEnableLoadmore(true);
        smartRefresh.setOnLoadmoreListener(refreshlayout -> {
            if("附近".equals(filterStr)){
                DialogUtil.getInstance().showLoadingSimple(PicBroseActivity.this, getWindow().getDecorView());
                LocationManager.getInstance().registerLocationListener(iLocationListener);
            }else{
                reqPics();
            }
        });
        if("附近".equals(filterStr)){
            DialogUtil.getInstance().showLoadingSimple(PicBroseActivity.this, getWindow().getDecorView());
            LocationManager.getInstance().registerLocationListener(iLocationListener);
        }else{
            reqPics();
        }
    }

    private LocationManager.ILocationListener iLocationListener = new LocationManager.ILocationListener() {
        @Override
        public void onReceive(BDLocation bdLocation) {
            smartRefresh.finishLoadmore();
            DialogUtil.getInstance().dimissLoadingDialog();
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            PicBroseActivity.this.bdLocation = bdLocation;
            reqPics();
        }

        @Override
        public void onFail(String msg) {
            smartRefresh.finishLoadmore();
            DialogUtil.getInstance().dimissLoadingDialog();
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            showShortToas(msg);
        }
    };

    private void reqPics() {
        if(newStart){
            page = 0;
            newStart = false;
            DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
        }
        if("脚步".equals(picFilter.getGallery())){
            reqPicsFootStep();
        } else if ("unsplash".equals(picFilter.getGallery())) {
            reqPicsUnsplash();
        }else if("pexels".equals(picFilter.getGallery())){
            reqPicsPexels();
        }
    }

    private void reqPicsUnsplash() {
        HttpRequestManager.getInstance().getUnsplashSearchReq(picFilter, (page + 1), num, new HttpRequestManager.CallBack() {
            @Override
            public void fail(String message) {
                runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishLoadmore();
                    showShortToas(message);
                });
            }

            @Override
            public void success(String response) {
                runOnUiThread(()->{
                    List<UnsplashEntity> unsplashEntities = null;
                    try{
                        if(picFilter.getLabels() != null && picFilter.getLabels().size() > 0){
                            UnsplashResult unsplashResult = new Gson().fromJson(response, UnsplashResult.class);
                            if(unsplashResult != null){
                                unsplashEntities = unsplashResult.getResults();
                            }
                        }else{
                            unsplashEntities = new Gson().fromJson(response,
                                    new TypeToken<List<UnsplashEntity>>(){}.getType());
                        }
                        List<ExtendUploadPic> extendUploadPics = new UploadPicConverter().converterUnsplash(unsplashEntities);
                        if(extendUploadPics == null){
                            DialogUtil.getInstance().dimissLoadingDialog();
                            smartRefresh.finishLoadmore();
                            return;
                        }
                        reqLikes(extendUploadPics);
                    }catch (Exception e){}
                });
            }
        });
    }

    private void reqPicsPexels() {
        HttpRequestManager.getInstance().pexelsPicsReq(picFilter, (page + 1), new HttpRequestManager.CallBack(){
            @Override
            public void fail(String message) {
                runOnUiThread(()->{
                    smartRefresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showLongToas(message);
                });
            }

            @Override
            public void success(String response) {
                runOnUiThread(()->{
                    List<ExtendUploadPic> uploadPics = new UploadPicConverter().converterPexelsPics(response);
                    reqLikes(uploadPics);
                });
            }
        });
    }

    private void reqLikes(List<ExtendUploadPic> uploadPics){
        ThirdPicLikesCountReq req = new ThirdPicLikesCountReq();
        req.setGallery(picFilter.getGallery());
        if("unsplash".equals(picFilter.getGallery())){
            List<String> uids = ExtendUploadPicUtil.getUids(uploadPics);
            if(uids.size() <= 0){
                smartRefresh.finishLoadmore();
                DialogUtil.getInstance().dimissLoadingDialog();
                return;
            }
            req.setUids(uids);
        }else if("pexels".equals(picFilter.getGallery())){
            List<String> urls = ExtendUploadPicUtil.getUrls(uploadPics);
            if(urls.size() <= 0){
                smartRefresh.finishLoadmore();
                DialogUtil.getInstance().dimissLoadingDialog();
                return;
            }
            req.setUrls(urls);
        }
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    smartRefresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showLongToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    List<ThirdPicLikes> thirdPicLikes = ((ThirdPicLikesCountResp)mResponse).getThirdPicLikes();
                    setLikes(uploadPics, thirdPicLikes);
                    smartRefresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if (uploadPics.size() <= 0) {
                        smartRefresh.setEnableLoadmore(false);
                    }
                    page = page + 1;
                    picsAdapter.addDatas(uploadPics);

                });
            }
        });
    };

    private void setLikes(List<ExtendUploadPic> uploadPics, List<ThirdPicLikes> thirdPicLikes) {
        uploadPics = ExtendUploadPicUtil.setLikes(uploadPics, thirdPicLikes, picFilter.getGallery());
    }

    private void reqPicsFootStep() {
        GetPicsReq req = new GetPicsReq();
        if("风景".equals(filterStr) || "生活".equals(filterStr) || "建筑".equals(filterStr)){
            List<String> labels = new ArrayList<>();
            labels.add(filterStr);
            picFilter.setLabels(labels);
        }else if("附近".equals(filterStr)){
            if(bdLocation == null){
                return;
            }
            picFilter.setNearBy(true);
            picFilter.setCity(bdLocation.getCity());
            picFilter.setLon(bdLocation.getLongitude());
            picFilter.setLan(bdLocation.getLatitude());
        }
        req.setChoice(isChoice);
        req.setPicFilter(picFilter);
        req.setFromId(user.getId());
        req.setStartNum((page * num));
        req.setNum(num);
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    smartRefresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishLoadmore();
                    if(mResponse.getCode() == 200){
                        page = page + 1;
                        List<ExtendUploadPic> uploadPics = new UploadPicConverter().converterFootStrp(((GetPicsResp)mResponse).getUploadPics(), bdLocation);
                        if(uploadPics.size() < num){
                            smartRefresh.setEnableLoadmore(false);
                        }
                        picsAdapter.addDatas(uploadPics);
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    @OnClick({R.id.back, R.id.filter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.filter:
                if (DialogUtil.getInstance().ispicFilterShow()) {
                    return;
                }
                DialogUtil.getInstance().showPicFilterDialog(PicBroseActivity.this, filter, picFilter, hideLabel, enableThidrGally);
                DialogUtil.getInstance().setPicFilterClick(filter -> {
                    if (filter != null) {
                        PicBroseActivity.this.picFilter = filter;
                        newStart = true;
                        smartRefresh.setEnableLoadmore(true);
                        picsAdapter.clearData();
                        if("附近".equals(filterStr)){
                            LocationManager.getInstance().registerLocationListener(iLocationListener);
                        }else{
                            reqPics();
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
        super.onDestroy();
    }

    private MyHandler mHandler = new MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<PicBroseActivity> activity;

        MyHandler(PicBroseActivity activity) {
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

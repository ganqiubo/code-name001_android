package tl.pojul.com.fastim.View.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pojul.fastIM.entity.ExtendUploadPic;
import com.pojul.fastIM.entity.PicFilter;
import com.pojul.fastIM.entity.PixabayEntity;
import com.pojul.fastIM.entity.PixabayEntityResults;
import com.pojul.fastIM.entity.ThirdPicLikes;
import com.pojul.fastIM.entity.UnsplashEntity;
import com.pojul.fastIM.entity.UnsplashResult;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.GetPicsReq;
import com.pojul.fastIM.message.request.ThirdPicLikesCountReq;
import com.pojul.fastIM.message.response.GetPicsResp;
import com.pojul.fastIM.message.response.ThirdPicLikesCountResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.PicsAdapter;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.PicBroseActivity;
import tl.pojul.com.fastim.View.activity.SettingActivity;
import tl.pojul.com.fastim.http.request.HttpRequestManager;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.socket.Converter.UploadPicConverter;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.ExtendUploadPicUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class DailyPicFragment extends BaseFragment {

    @BindView(R.id.pic_list)
    RecyclerView picList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.filter)
    ImageView filter;
    @BindView(R.id.setting)
    ImageView setting;

    private int gridCount = 2;
    private PicFilter picFilter = new PicFilter();
    private User user;
    private List<ExtendUploadPic> uploadPics = new ArrayList<>();
    private PicsAdapter picsAdapter;
    private  boolean hideLabel;
    private boolean isChoice;
    private int page = 0;
    private int num = 10;
    private BDLocation bdLocation;
    private boolean newStart = true;
    private boolean enableThidrGally = true;
    private View view;
    private Unbinder unbinder;
    private String filterStr = "更多";

    public DailyPicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view!=null){
            ViewGroup parent =(ViewGroup)view.getParent();
            parent.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_daily_pic, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        user = SPUtil.getInstance().getUser();

        picFilter.setGallery("pixabay");
        hideLabel = false;

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(gridCount, StaggeredGridLayoutManager.VERTICAL);
        picList.setLayoutManager(staggeredGridLayoutManager);
        picsAdapter = new PicsAdapter(getActivity(), uploadPics, gridCount);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        picList.setAdapter(picsAdapter);

        smartRefresh.setPrimaryColors(Color.parseColor("#bbbbbb"));
        smartRefresh.setEnableRefresh(true);
        smartRefresh.setEnableLoadmore(true);
        smartRefresh.setOnLoadmoreListener(refreshlayout -> {
            if("附近".equals(filterStr)){
                DialogUtil.getInstance().showLoadingSimple(getActivity(), getActivity().getWindow().getDecorView());
                LocationManager.getInstance().registerLocationListener(iLocationListener);
            }else{
                reqPics();
            }
        });
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            newStart = true;
            picsAdapter.clearData();
            smartRefresh.setEnableLoadmore(true);
            reqPics();
        });
        if("附近".equals(filterStr)){
            DialogUtil.getInstance().showLoadingSimple(getActivity(), getActivity().getWindow().getDecorView());
            LocationManager.getInstance().registerLocationListener(iLocationListener);
        }else{
            reqPics();
        }
    }

    private LocationManager.ILocationListener iLocationListener = new LocationManager.ILocationListener() {
        @Override
        public void onReceive(BDLocation bdLocation) {
            smartRefresh.finishLoadmore();
            smartRefresh.finishRefresh();
            DialogUtil.getInstance().dimissLoadingDialog();
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            DailyPicFragment.this.bdLocation = bdLocation;
            reqPics();
        }

        @Override
        public void onFail(String msg) {
            smartRefresh.finishLoadmore();
            smartRefresh.finishRefresh();
            DialogUtil.getInstance().dimissLoadingDialog();
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            showShortToas(msg);
        }
    };

    private void reqPics() {
        if(newStart){
            page = 0;
            newStart = false;
            //DialogUtil.getInstance().showLoadingSimple(getActivity(), getActivity().getWindow().getDecorView());
        }
        if("脚步".equals(picFilter.getGallery())){
            reqPicsFootStep();
        } else if ("unsplash".equals(picFilter.getGallery())) {
            reqPicsUnsplash();
        }/*else if("pexels".equals(picFilter.getGallery())){
            reqPicsPexels();
        }*/else if("pixabay".equals(picFilter.getGallery())){
            reqPicsPixabay();
        }
    }

    private void reqPicsUnsplash() {
        HttpRequestManager.getInstance().getUnsplashSearchReq(picFilter, (page + 1), num, new HttpRequestManager.CallBack() {
            @Override
            public void fail(String message) {
                getActivity().runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishLoadmore();
                    smartRefresh.finishRefresh();
                    showShortToas(message);
                });
            }

            @Override
            public void success(String response) {
                getActivity().runOnUiThread(()->{
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
                            smartRefresh.finishRefresh();
                            return;
                        }
                        reqLikes(extendUploadPics);
                    }catch (Exception e){}
                });
            }
        });
    }

    private void reqPicsPixabay() {
        HttpRequestManager.getInstance().pixabayPicsReq(picFilter, (page + 1), new HttpRequestManager.CallBack(){
            @Override
            public void fail(String message) {
                getActivity().runOnUiThread(()->{
                    smartRefresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showLongToas(message);
                });
            }

            @Override
            public void success(String response) {
                getActivity().runOnUiThread(()->{
                    PixabayEntityResults results = new Gson().fromJson(response,
                            PixabayEntityResults.class);
                    List<PixabayEntity> pixabayEntities = results.getHits();
                    List<ExtendUploadPic> uploadPics = new UploadPicConverter().converterPixabayPics(pixabayEntities);
                    reqLikes(uploadPics);
                });
            }
        });
    }

    private void reqPicsPexels() {
        HttpRequestManager.getInstance().pexelsPicsReq(picFilter, (page + 1), new HttpRequestManager.CallBack(){
            @Override
            public void fail(String message) {
                getActivity().runOnUiThread(()->{
                    smartRefresh.finishLoadmore();
                    smartRefresh.finishRefresh();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showLongToas(message);
                });
            }

            @Override
            public void success(String response) {
                getActivity().runOnUiThread(()->{
                    List<ExtendUploadPic> uploadPics = new UploadPicConverter().converterPexelsPics(response);
                    reqLikes(uploadPics);
                });
            }
        });
    }

    private void reqLikes(List<ExtendUploadPic> uploadPics){
        ThirdPicLikesCountReq req = new ThirdPicLikesCountReq();
        req.setGallery(picFilter.getGallery());
        if("unsplash".equals(picFilter.getGallery()) || "pixabay".equals(picFilter.getGallery())){
            List<String> uids = ExtendUploadPicUtil.getUids(uploadPics);
            if(uids.size() <= 0){
                smartRefresh.finishLoadmore();
                smartRefresh.finishRefresh();
                DialogUtil.getInstance().dimissLoadingDialog();
                return;
            }
            req.setUids(uids);
        }else if("pexels".equals(picFilter.getGallery())){
            List<String> urls = ExtendUploadPicUtil.getUrls(uploadPics);
            if(urls.size() <= 0){
                smartRefresh.finishLoadmore();
                smartRefresh.finishRefresh();
                DialogUtil.getInstance().dimissLoadingDialog();
                return;
            }
            req.setUrls(urls);
        }
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                getActivity().runOnUiThread(()->{
                    smartRefresh.finishLoadmore();
                    smartRefresh.finishRefresh();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showLongToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                getActivity().runOnUiThread(()->{
                    List<ThirdPicLikes> thirdPicLikes = ((ThirdPicLikesCountResp)mResponse).getThirdPicLikes();
                    setLikes(uploadPics, thirdPicLikes);
                    smartRefresh.finishLoadmore();
                    smartRefresh.finishRefresh();
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
                getActivity().runOnUiThread(()->{
                    smartRefresh.finishLoadmore();
                    smartRefresh.finishRefresh();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    //showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                getActivity().runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishLoadmore();
                    smartRefresh.finishRefresh();
                    if(mResponse.getCode() == 200){
                        page = page + 1;
                        List<ExtendUploadPic> uploadPics = new UploadPicConverter().converterFootStrp(((GetPicsResp)mResponse).getUploadPics(), bdLocation);
                        if(uploadPics.size() < num){
                            smartRefresh.setEnableLoadmore(false);
                        }
                        picsAdapter.addDatas(uploadPics);
                    }else{
                        //showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    public void onFilterClick(ImageView filterView) {
        if (DialogUtil.getInstance().ispicFilterShow()) {
            return;
        }
        DialogUtil.getInstance().showPicFilterDialog(getActivity(), filterView, picFilter, hideLabel, enableThidrGally);
        DialogUtil.getInstance().setPicFilterClick(filter -> {
            if (filter != null) {
                picFilter = filter;
                newStart = true;
                smartRefresh.setEnableLoadmore(true);
                picsAdapter.clearData();
                DialogUtil.getInstance().showLoadingSimple(getActivity(), getActivity().getWindow().getDecorView());
                if("附近".equals(filterStr)){
                    LocationManager.getInstance().registerLocationListener(iLocationListener);
                }else{
                    reqPics();
                }
            }
        });
    }

    @OnClick({R.id.filter, R.id.setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.filter:
                onFilterClick(filter);
                break;
            case R.id.setting:
                ((BaseActivity) getContext()).startActivity(SettingActivity.class, null);
                break;
        }
    }

}

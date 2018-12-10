package tl.pojul.com.fastim.View.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiFilter;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.fastIM.entity.LatLonRange;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.fastIM.message.request.GetTopMessMultiReq;
import com.pojul.fastIM.message.response.GetTopMessMultiResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.CommunityAdapter;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.socket.Converter.HistoryChatConverter;
import tl.pojul.com.fastim.util.CustomTimeDown;
import tl.pojul.com.fastim.util.MyDistanceUtil;

public class NearByCommunityFragment extends BaseFragment implements CustomTimeDown.OnTimeDownListener{


    @BindView(R.id.community_list)
    RecyclerView communityList;
    @BindView(R.id.community_smart_layout)
    SmartRefreshLayout communitySmartLayout;
    Unbinder unbinder;

    private View view;
    private PoiSearch mPoiSearch;
    private List<CommunityRoom> communityRooms = new ArrayList<>();
    private CommunityAdapter communityAdapter;
    private int locReqTag; //1: 获取附近社区
    private boolean isFirstTick = true;

    //for test
    public static LatLng testLatLng;
    public static boolean isTestMode = false;

    private GeoCoder geoCoder;
    private BDLocation myBDLocation;
    private List<PoiDetailResult> tempPoiDetails = new ArrayList<>();
    private CustomTimeDown customTimeDown;

    private HashMap<String, List<String>> topmesses = new HashMap<>();
    private long lastTopMessMilli = System.currentTimeMillis();
    public static boolean paretnVisiable = false;
    public static boolean visiable = true;
    private static boolean isResume;
    private long topMessInterval = 20 * 60 * 1000;
    private int radius = 1000;

    public NearByCommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_nearby_community, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init() {
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
        communityList.setLayoutManager(layoutmanager);
        communityAdapter = new CommunityAdapter(getContext(), communityRooms);
        communityList.setAdapter(communityAdapter);

        communitySmartLayout.setEnableLoadmore(false);
        communitySmartLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                communityRooms.clear();
                tempPoiDetails.clear();
                if(isTestMode && testLatLng != null){
                    geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(testLatLng));
                    searchPoi(testLatLng);
                }else{
                    LocationManager.getInstance().registerLocationListener(iLocationListener);
                    locReqTag = 1;
                    new Handler(Looper.getMainLooper()).postDelayed(()->{
                        reqTopMesses();
                    }, 10 * 1000);
                }
            }
        });

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(geoListener);

        if(!isTestMode){
            LocationManager.getInstance().registerLocationListener(iLocationListener);
            locReqTag = 1;
        }

        customTimeDown = new CustomTimeDown(Long.MAX_VALUE, topMessInterval);
        customTimeDown.setOnTimeDownListener(this);
        customTimeDown.start();

        communityList.setNestedScrollingEnabled(false);

        new Handler(Looper.getMainLooper()).postDelayed(()->{
            reqTopMesses();
        }, 10 * 1000);
        
    }

    private OnGetGeoCoderResultListener geoListener = new OnGetGeoCoderResultListener() {

        //经纬度转换成地址
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        }

        //经纬度转换成地址
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if(isTestMode){
                ReverseGeoCodeResult.AddressComponent detail = reverseGeoCodeResult.getAddressDetail();
                CommunityRoom communityRoom = new CommunityRoom();
                communityRoom.setCommunityUid(detail.city + "_"  + detail.district);
                communityRoom.setName(detail.district);
                communityRoom.setCommunityType("行政区域");
                communityRoom.setCommunitySubtype("区/县");
                communityRoom.setCountry(detail.countryName);
                communityRoom.setProvince(detail.province);
                communityRoom.setCity(detail.city);
                communityRoom.setDistrict(detail.district);
                communityRoom.setAddr(communityRoom.getCountry() + communityRoom.getProvince() + communityRoom.getCity() + communityRoom.getDistrict());
                communityAdapter.addCommunity(communityRoom);

                CommunityRoom communityRoom1 = new CommunityRoom();
                communityRoom1.setCommunityUid(detail.province + "_" + detail.city);
                communityRoom1.setName(detail.city);
                communityRoom1.setCommunityType("行政区域");
                communityRoom1.setCommunitySubtype("市");
                communityRoom1.setCountry(detail.countryName);
                communityRoom1.setProvince(detail.province);
                communityRoom1.setCity(detail.city);
                communityRoom1.setDistrict("");
                communityRoom1.setAddr(communityRoom1.getCountry() + communityRoom1.getProvince() + communityRoom1.getCity());
                communityAdapter.addCommunity(communityRoom1);

                CommunityRoom communityRoom2 = new CommunityRoom();
                communityRoom2.setCommunityUid(detail.countryName + "_" + detail.province);
                communityRoom2.setName(detail.province);
                communityRoom2.setCommunityType("行政区域");
                communityRoom2.setCommunitySubtype("省");
                communityRoom2.setCountry(detail.countryName);
                communityRoom2.setProvince(detail.province);
                communityRoom2.setCity("");
                communityRoom2.setDistrict("");
                communityRoom2.setAddr(communityRoom2.getCountry() + communityRoom2.getProvince());
                communityAdapter.addCommunity(communityRoom2);
            }else{
                setCommunity(reverseGeoCodeResult);
            }

        }
    };

    private void setCommunity(ReverseGeoCodeResult reverseGeoCodeResult) {
        for (int i =0; i < tempPoiDetails.size(); i++){
            PoiDetailResult poiDetail = tempPoiDetails.get(i);
            double distance = Math.abs(DistanceUtil.getDistance(poiDetail.getLocation(), reverseGeoCodeResult.getLocation()));
            if(distance < 1){
                removeTempPoiDetail(i, reverseGeoCodeResult);
                break;
            }
        }
    }

    private void removeTempPoiDetail(int position, ReverseGeoCodeResult reverseGeoCodeResult) {
        synchronized (tempPoiDetails){
            if(position < tempPoiDetails.size()){
                CommunityRoom communityRoom = createCommunityByPoi(tempPoiDetails.get(position), reverseGeoCodeResult);
                communityAdapter.addCommunity(communityRoom);
                tempPoiDetails.remove(position);
            }
        }
    }

    private void searchPoi(LatLng latLng) {
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .sortType(PoiSortType.distance_from_near_to_far)
                //.keyword("教育培训;高等院校")
                .keyword("学校")
                .tag("教育培训,高等院校")
                .location(latLng)
                .radius(radius));

        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .sortType(PoiSortType.distance_from_near_to_far)
                //.keyword("教育培训;中学")
                .keyword("学校")
                .tag("教育培训,中学")
                .location(latLng)
                .radius(radius));

        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .sortType(PoiSortType.distance_from_near_to_far)
                //.keyword("教育培训;小学")
                .keyword("学校")
                .tag("教育培训,小学")
                .location(latLng)
                .radius(radius));

        /*mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .sortType(PoiSortType.distance_from_near_to_far)
                //.keyword("住宅区")
                //.tag("房地产,住宅区")
                .tag("住宅区")
                .location(latLng)
                .radius(radius));*/

        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .sortType(PoiSortType.distance_from_near_to_far)
                //.keyword("旅游景点;风景区")
                .keyword("风景区")
                .tag("旅游景点,风景区")
                .location(latLng)
                .radius(radius));

        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .sortType(PoiSortType.distance_from_near_to_far)
                //.keyword("旅游景点;公园")
                .keyword("公园")
                .tag("旅游景点,公园")
                .location(latLng)
                .radius(radius));

        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .sortType(PoiSortType.distance_from_near_to_far)
                //.keyword("旅游景点;动物园")
                .keyword("动物园")
                .tag("旅游景点,动物园")
                .location(latLng)
                .radius(radius));

        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                //.keyword("交通设施;火车站")
                .keyword("火车站")
                .tag("交通设施,火车站")
                .location(latLng)
                .radius(radius));
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .sortType(PoiSortType.distance_from_near_to_far)
                //.keyword("交通设施;飞机场")
                .keyword("飞机场")
                .tag("交通设施,飞机场")
                .location(latLng)
                .radius(radius));

        LatLonRange range = MyDistanceUtil.getLatLonRange(latLng.longitude, latLng.latitude, 0.7d);
        mPoiSearch.searchInBound(new PoiBoundSearchOption()
                .bound(new LatLngBounds.Builder().include(new LatLng(range.getMaxLat(), range.getMinLon()))
                        .include(new LatLng(range.getMinLat(), range.getMaxLon())).build())
                .keyword("小区"));
    }

    private LocationManager.ILocationListener iLocationListener = new LocationManager.ILocationListener() {

        @Override
        public void onReceive(BDLocation bdLocation) {
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            if(bdLocation == null){
                communitySmartLayout.finishRefresh();
                return;
            }
            /*bdLocation.setLongitude(116.309049);
            bdLocation.setLatitude(39.967564);*/
            myBDLocation = LocationManager.getInstance().getBDLocationInCoorType(bdLocation, BDLocation.BDLOCATION_GCJ02_TO_BD09LL);
            addAdministrativeArae(bdLocation);
            switch(locReqTag){
                case 1:
                    searchPoi(new LatLng(myBDLocation.getLatitude(), myBDLocation.getLongitude()));
                    break;
            }
        }

        @Override
        public void onFail(String msg) {
            communitySmartLayout.finishRefresh();
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            showShortToas(getString(R.string.get_location_fail));
        }
    };

    private void addAdministrativeArae(BDLocation bdLocation) {
        CommunityRoom communityRoom = new CommunityRoom();
        communityRoom.setCommunityUid(bdLocation.getCity() + "_"  + bdLocation.getDistrict());
        communityRoom.setName(bdLocation.getDistrict());
        communityRoom.setCommunityType("行政区域");
        communityRoom.setCommunitySubtype("区/县");
        communityRoom.setCountry(bdLocation.getCountry());
        communityRoom.setProvince(bdLocation.getProvince());
        communityRoom.setCity(bdLocation.getCity());
        communityRoom.setDistrict(bdLocation.getDistrict());
        communityRoom.setAddr(communityRoom.getCountry() + communityRoom.getProvince() + communityRoom.getCity() + communityRoom.getDistrict());
        communityAdapter.addCommunity(communityRoom);

        CommunityRoom communityRoom1 = new CommunityRoom();
        communityRoom1.setCommunityUid(bdLocation.getProvince() + "_" + bdLocation.getCity());
        communityRoom1.setName(bdLocation.getCity());
        communityRoom1.setCommunityType("行政区域");
        communityRoom1.setCommunitySubtype("市");
        communityRoom1.setCountry(bdLocation.getCountry());
        communityRoom1.setProvince(bdLocation.getProvince());
        communityRoom1.setCity(bdLocation.getCity());
        communityRoom1.setDistrict("");
        communityRoom1.setAddr(communityRoom1.getCountry() + communityRoom1.getProvince() + communityRoom1.getCity());
        communityAdapter.addCommunity(communityRoom1);

        CommunityRoom communityRoom2 = new CommunityRoom();
        communityRoom2.setCommunityUid(bdLocation.getCountry() + "_" + bdLocation.getProvince());
        communityRoom2.setName(bdLocation.getProvince());
        communityRoom2.setCommunityType("行政区域");
        communityRoom2.setCommunitySubtype("省");
        communityRoom2.setCountry(bdLocation.getCountry());
        communityRoom2.setProvince(bdLocation.getProvince());
        communityRoom2.setCity("");
        communityRoom2.setDistrict("");
        communityRoom2.setAddr(communityRoom2.getCountry() + communityRoom2.getProvince());
        communityAdapter.addCommunity(communityRoom2);
    }

    private CommunityRoom createCommunityByPoi(PoiDetailResult poiDetailResult, ReverseGeoCodeResult reverseGeoCodeResult){
        ReverseGeoCodeResult.AddressComponent detail = reverseGeoCodeResult.getAddressDetail();
        CommunityRoom communityRoom = new CommunityRoom();
        communityRoom.setCommunityUid(detail.district + "_"  + poiDetailResult.name);
        communityRoom.setName(poiDetailResult.name);
        communityRoom.setCommunityType(poiDetailResult.tag.split(";")[0]);
        communityRoom.setCommunitySubtype(poiDetailResult.tag.split(";")[1]);
        communityRoom.setCountry(detail.countryName);
        communityRoom.setProvince(detail.province);
        communityRoom.setCity(detail.city);
        communityRoom.setDistrict(detail.district);
        communityRoom.setAddr(communityRoom.getCountry() + communityRoom.getProvince() + communityRoom.getCity() + communityRoom.getDistrict());
        communityRoom.setLatitude(poiDetailResult.location.latitude);
        communityRoom.setLongitude(poiDetailResult.location.longitude);
        if(myBDLocation != null){
            double distance = DistanceUtil.getDistance(new LatLng(myBDLocation.getLatitude(), myBDLocation.getLongitude()), poiDetailResult.location);
            communityRoom.setDistance(Math.abs(distance));
        }
        communityAdapter.addCommunity(communityRoom);
        return communityRoom;
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){

        public void onGetPoiResult(PoiResult result){
            if(communitySmartLayout.isRefreshing()){
                communitySmartLayout.finishRefresh();
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                String str  = new Gson().toJson(result.getAllPoi());
                for(int i = 0; i < result.getAllPoi().size(); i++){
                    if(result.getAllPoi().get(i).uid != null){
                        mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(result.getAllPoi().get(i).uid));
                    }
                }
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result){
            //获取Place详情页检索结果
            if(communitySmartLayout.isRefreshing()){
                communitySmartLayout.finishRefresh();
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                filterPoi(result);
            }
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    private void filterPoi(PoiDetailResult result) {
        switch (result.getTag()){
            case "房地产;住宅区":
                Log.e("PoiDetailResult" , result.getName());
                String name = result.getName().split("-")[0];
                result.name = name;
                addTempPois(result);
                //communityAdapter.addCommunity(result);
                break;
            case "教育培训;高等院校":
                name = result.getName().split("-")[0];
                if(name.length() < 2){
                    return;
                }
                String endName = name.substring((name.length() -2), name.length());
                if("大学".equals(endName) || "学院".equals(endName)){
                    result.name = name;
                    //communityAdapter.addCommunity(result);
                    addTempPois(result);
                }
                break;
            case "教育培训;中学":
                name = result.getName().split("-")[0];
                if(name.length() > 2 && "中学".equals(name.substring((name.length() -2), name.length()))){
                    result.name = name;
                    //communityAdapter.addCommunity(result);
                    addTempPois(result);
                }
                break;
            case "教育培训;小学":
                name = result.getName().split("-")[0];
                if(name.length() > 2 && "小学".equals(name.substring((name.length() -2), name.length())) ){
                    result.name = name;
                    //communityAdapter.addCommunity(result);
                    addTempPois(result);
                }
                break;
            case "旅游景点;风景区":
                name = result.getName().split("-")[0];
                result.name = name;
                //communityAdapter.addCommunity(result);
                addTempPois(result);
                break;
            case "旅游景点;公园":
                name = result.getName().split("-")[0];
                result.name = name;
                //communityAdapter.addCommunity(result);
                addTempPois(result);
                break;
            case "旅游景点;动物园":
                name = result.getName().split("-")[0];
                result.name = name;
                //communityAdapter.addCommunity(result);
                addTempPois(result);
                break;
            case "交通设施;火车站":
                name = result.getName().split("-")[0];
                result.name = name;
                //communityAdapter.addCommunity(result);
                addTempPois(result);
                break;
            case "交通设施;飞机场":
                name = result.getName().split("-")[0];
                result.name = name;
                //communityAdapter.addCommunity(result);
                addTempPois(result);
                break;
        }
    }

    private void addTempPois(PoiDetailResult result){
        synchronized (tempPoiDetails){
            tempPoiDetails.add(result);
        }
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(result.getLocation()));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visiable = isVisibleToUser;
        if(paretnVisiable && visiable && isResume){
            if((System.currentTimeMillis() - lastTopMessMilli) > topMessInterval){
                reqTopMesses();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
        if(paretnVisiable && visiable && isResume) {
            if ((System.currentTimeMillis() - lastTopMessMilli) > topMessInterval) {
                reqTopMesses();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mPoiSearch.destroy();
        customTimeDown.setOnTimeDownListener(null);
        customTimeDown = null;
    }

    @Override
    public void onTick(long l) {
        if(isFirstTick){
            isFirstTick = false;
            return;
        }
        if(paretnVisiable && visiable && isResume){
            //showLongToas("ontick visiable: " + true);
            reqTopMesses();
        }
    }

    private void reqTopMesses() {
        List<String> communityUids = communityAdapter.getCommunityUids();
        if(communityUids.size() <= 0){
            return;
        }
        GetTopMessMultiReq req = new GetTopMessMultiReq();
        req.setCommunityUids(communityUids);
        req.setNum(6);
        lastTopMessMilli = System.currentTimeMillis();
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {}

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(()->{
                    if(mResponse.getCode() == 200){
                        topmesses = new HistoryChatConverter().getTopMessMulyi(((GetTopMessMultiResp)mResponse).getCommunityMessEntities());
                        communityAdapter.refreshTops(topmesses);
                    }
                });
            }
        });
    }

    @Override
    public void OnFinish() {

    }

}

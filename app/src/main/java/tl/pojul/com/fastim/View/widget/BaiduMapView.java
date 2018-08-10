package tl.pojul.com.fastim.View.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.google.gson.Gson;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.fragment.CommunityFragment;
import tl.pojul.com.fastim.View.fragment.NearByCommunityFragment;
import tl.pojul.com.fastim.map.baidu.LocationManager;

public class BaiduMapView extends RelativeLayout {

    MapView communityMap;
    ImageView myLocation;
    ImageView mapSatelliteType;
    Button mapLevelAdd;
    Button mapLevelReduce;
    ImageView mapCompass;
    RelativeLayout mapCompassRl;
    ImageView mapOverlook;
    RelativeLayout mapControlRl;

    private BaiduMap mBaiduMap;
    private boolean isMapStatusChanged;
    private PoiSearch mPoiSearch;

    public BaiduMapView(Context context) {
        super(context);
        init();
    }

    public BaiduMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaiduMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.include_map_view, this);

        communityMap = findViewById(R.id.community_map);
        myLocation = findViewById(R.id.my_location);
        mapSatelliteType = findViewById(R.id.map_satellite_type);
        mapLevelAdd = findViewById(R.id.map_level_add);
        mapLevelReduce = findViewById(R.id.map_level_reduce);
        mapCompass = findViewById(R.id.map_compass);
        mapCompassRl = findViewById(R.id.map_compass_rl);
        mapOverlook = findViewById(R.id.map_overlook);
        mapControlRl = findViewById(R.id.map_control_rl);

        myLocation.setOnClickListener(mapControlClick);
        mapSatelliteType.setOnClickListener(mapControlClick);
        mapLevelAdd.setOnClickListener(mapControlClick);
        mapLevelReduce.setOnClickListener(mapControlClick);
        mapCompass.setOnClickListener(mapControlClick);
        mapOverlook.setOnClickListener(mapControlClick);

        mapCompass.setRotation(0);
        mapSatelliteType.setSelected(false);
        mapOverlook.setSelected(true);

        for (int i = 0; i < communityMap.getChildCount(); i++) {
            if (i == 0) {
                continue;
            }
            communityMap.getChildAt(i).setVisibility(View.GONE);
        }
        mBaiduMap = communityMap.getMap();
        mBaiduMap.setIndoorEnable(true);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                if(NearByCommunityFragment.isTestMode){
                    NearByCommunityFragment.testLatLng = new LatLng(mapPoi.getPosition().latitude, mapPoi.getPosition().longitude);
                }
                //mapPoi.getPosition().
                showLongToas("onMapPoiClick: " + new Gson().toJson(mapPoi));
                getPoiDetail(mapPoi.getUid());
                return false;
            }
        });

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                mapCompass.setRotation(mapStatus.rotate);
                if (mapStatus.overlook < 0) {
                    mapOverlook.setSelected(false);
                }
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
            }
        });

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

    }

    private OnClickListener mapControlClick = v -> {
        switch (v.getId()){
            case R.id.my_location:
                requestMyLocation();
                break;
            case R.id.map_satellite_type:
                if (mBaiduMap == null || isMapStatusChanged) {
                    return;
                }
                isMapStatusChanged = true;
                mapSatelliteType.setSelected(!mapSatelliteType.isSelected());
                if (mapSatelliteType.isSelected()) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                } else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
                mapControlFinish(500);
                break;
            case R.id.map_level_add:
                if (mBaiduMap == null || isMapStatusChanged) {
                    return;
                }
                if (mBaiduMap.getMapStatus().zoom >= mBaiduMap.getMaxZoomLevel()) {
                    return;
                }
                isMapStatusChanged = true;
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomIn());
                mapControlFinish(500);
                break;
            case R.id.map_level_reduce:
                if (mBaiduMap == null || isMapStatusChanged) {
                    return;
                }
                if (mBaiduMap.getMapStatus().zoom <= mBaiduMap.getMinZoomLevel()) {
                    return;
                }
                isMapStatusChanged = true;
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomOut());
                mapControlFinish(500);
                break;
            case R.id.map_compass_rl:
                if (mBaiduMap == null || isMapStatusChanged) {
                    return;
                }
                isMapStatusChanged = true;
                MapStatus mapStatus = new MapStatus.Builder().rotate(0).build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
                mapControlFinish(500);
                break;
            case R.id.map_overlook:
                if (mBaiduMap == null || isMapStatusChanged) {
                    return;
                }
                isMapStatusChanged = true;
                mapOverlook.setSelected(!mapOverlook.isSelected());
                if (mapOverlook.isSelected()) {
                    mapStatus = new MapStatus.Builder().overlook(0).build();
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
                } else {
                    mapStatus = new MapStatus.Builder().overlook(-85).build();
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
                }
                mapControlFinish(500);
                break;
        }
    };

    private void mapControlFinish(long mill){
        new Handler(Looper.getMainLooper()).postDelayed(()->{
            isMapStatusChanged = false;
        }, mill);
    }

    public void getPoiDetail(String uid){
        mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(uid));
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.e("poi", "error: " + poiDetailResult.error);
                showLongToas("error: " + poiDetailResult.error);
                //详情检索失败
                // result.error请参考SearchResult.ERRORNO
            } else {
                String json = new Gson().toJson(poiDetailResult);
                Log.e("poi", "poiDetailResult: " + json);
                showLongToas("poiDetailResult: " + json);
                //检索成功
            }
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        }
    };

    private void requestMyLocation() {
        myLocation.setSelected(true);
        LocationManager.getInstance().registerLocationListener(iLocationListener);
    }

    private LocationManager.ILocationListener iLocationListener = new LocationManager.ILocationListener() {
        @Override
        public void onReceive(BDLocation bdLocation) {
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            myLocation.setSelected(false);
            if (bdLocation == null || mBaiduMap == null || isMapStatusChanged) {
                return;
            }
            isMapStatusChanged = true;
            /*LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            bdLocation = LocationManager.getInstance().getBDLocationInCoorType(bdLocation, BDLocation.BDLOCATION_GCJ02_TO_BD09LL);
            float zoom = mBaiduMap.getMaxZoomLevel();
            long delay = 0;
           if(mBaiduMap.getMapStatus().zoom > (mBaiduMap.getMaxZoomLevel() - 5)){
                zoom = mBaiduMap.getMaxZoomLevel() - 5;
                delay = 500;
            }
            if(delay > 0){
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(
                        new MapStatus.Builder().zoom(zoom).build()));
            }*/
           /*BDLocation finalBdLocation = bdLocation;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(finalBdLocation.getLatitude(), finalBdLocation.getLongitude())), 1000);
                mapControlFinish(1100);
            }, delay);*/
            float zoom = mBaiduMap.getMapStatus().zoom ;
            if(mBaiduMap.getMapStatus().zoom > (mBaiduMap.getMaxZoomLevel() - 5)){
                zoom = mBaiduMap.getMaxZoomLevel() - 5;
            }
            MapStatus newMapStatus = new MapStatus.Builder().target(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())).zoom(zoom).build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(newMapStatus);
            mBaiduMap.animateMapStatus(mMapStatusUpdate, 1000);
            mapControlFinish(1100);
        }

        @Override
        public void onFail(String msg) {
            myLocation.setSelected(false);
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            showShortToas("定位失败");
        }
    };


    public void showShortToas(String msg) {
        try {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    public void showLongToas(String msg) {
        try {
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        }
    }

    public void onDestory(){
        if(mPoiSearch != null){
            mPoiSearch.destroy();
        }
        if(communityMap != null){
            communityMap.onDestroy();
        }
    }
}

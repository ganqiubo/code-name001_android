package tl.pojul.com.fastim.map.baidu;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;

import java.util.ArrayList;
import java.util.List;

public class LocationManager {

    private static LocationManager mLocationManager;

    public static LocationClient mLocationClient;

    protected List<ILocationListener> iLocationListeners = new ArrayList<>();

    private MyLocationListener myLocationListener = new MyLocationListener();

    private LocationClientOption option;

    private LocationManager(Context context) {
        mLocationClient= new LocationClient(context);
        initLocation();
    }

    public static LocationManager Instance(Context context) {
        if (mLocationManager == null) {
            synchronized (LocationManager.class) {
                if (mLocationManager == null) {
                    mLocationManager = new LocationManager(context);
                }
            }
        }
        return mLocationManager;
    }

    public static LocationManager getInstance() {
        return mLocationManager;
    }

    private void initLocation() {
        mLocationClient.registerLocationListener(myLocationListener);
        option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("gcj02");
        //option.setCoorType("bd09ll");
        option.setEnableSimulateGps(false);
        option.setIsNeedAltitude(true);
        option.setIsNeedAddress(true);
        //option.setTimeOut(6 * 1000);
        option.setOpenGps(true);
        option.setScanSpan(4000);
        mLocationClient.setLocOption(option);
        mLocationClient.stop();
    }

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            for (int i = 0; i < iLocationListeners.size(); i ++){
                ILocationListener iLocationListener = iLocationListeners.get(i);
                if(iLocationListener == null){
                    continue;
                }
                if(bdLocation.getLocType() == 61 || bdLocation.getLocType() == 66 || bdLocation.getLocType() == 161){
                    iLocationListener.onReceive(bdLocation);
                }else {
                    iLocationListener.onFail("定位失败");
                }
            }
        }
    };

    public void registerLocationListener(ILocationListener iLocationListener){
        synchronized (iLocationListeners){
            if(iLocationListener != null){
                if(!mLocationClient.isStarted()){
                    mLocationClient.start();
                }
                iLocationListeners.add(iLocationListener);
            }
        }
    }

    public BDLocation getBDLocationInCoorType(BDLocation bdLocation, String toCoordType){
        //such as BDLocation.BDLOCATION_GCJ02_TO_BD09LL
        if(bdLocation == null || toCoordType == null){
            return null;
        }
        return LocationClient.getBDLocationInCoorType(bdLocation, toCoordType);
    }

    public void unRegisterLocationListener(ILocationListener iLocationListener){
        synchronized (iLocationListeners){
            if(iLocationListener != null){
                iLocationListeners.remove(iLocationListener);
                if(iLocationListeners.size() <= 0 && mLocationClient.isStarted()){
                    mLocationClient.stop();
                }
            }
        }
    }

    public interface ILocationListener{
        void onReceive(BDLocation bdLocation);
        void onFail(String msg);
    }

    public void onDestory(){
        mLocationClient.unRegisterLocationListener(myLocationListener);
    }

}

package tl.pojul.com.fastim.util;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.fastIM.entity.LatLonRange;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MyDistanceUtil {

    public static String getDisttanceStr(double distance){
        if(distance < 1000){
            String distanceStr = new BigDecimal(("" + distance)).setScale(1, RoundingMode.HALF_UP).toString();
            return distanceStr + "米";
        }else{
            String distanceKmStr = new BigDecimal(("" + distance * 0.001)).setScale(1, RoundingMode.HALF_UP).toString();
            return distanceKmStr + "千米";
        }
    }

    public static int isSpaceTravel(CommunityRoom communityRoom, BDLocation bdLocation, int showCommunityLoc){
        if(showCommunityLoc == 1){
            return 0;
        }
        if(communityRoom.getCommunitySubtype().equals("省")){
            if(communityRoom.getCountry().equals(bdLocation.getCountry())
                    && communityRoom.getProvince().equals(bdLocation.getProvince())){
                return 1;
            }
        }else if(communityRoom.getCommunitySubtype().equals("市")){
            if(communityRoom.getCountry().equals(bdLocation.getCountry())
                    && communityRoom.getProvince().equals(bdLocation.getProvince())
                    && communityRoom.getCity().equals(bdLocation.getCity())){
                return 1;
            }
        }else if(communityRoom.getCommunitySubtype().equals("区/县")){
            if(communityRoom.getCountry().equals(bdLocation.getCountry())
                    && communityRoom.getProvince().equals(bdLocation.getProvince())
                    && communityRoom.getCity().equals(bdLocation.getCity())
                    && communityRoom.getDistrict().equals(bdLocation.getDistrict())){
                return 1;
            }
        }else{
            double distance = DistanceUtil.getDistance(new LatLng(communityRoom.getLatitude(), communityRoom.getLongitude()),
                    new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
            if(Math.abs(distance) < 3000){
                return 1;
            }
        }
        return 0;
    }

    /**
     * @param longitude
     * @param latitude
     * @param distance 距离(Km)
     * */
    public static LatLonRange getLatLonRange(double longitude, double latitude, double distance){
        LatLonRange latLonRange = new LatLonRange();
        double r = 6371;//地球半径千米
        double dlng =  2*Math.asin(Math.sin(distance/(2*r))/Math.cos(latitude*Math.PI/180));
        dlng = dlng*180/Math.PI;//角度转为弧度
        double dlat = distance/r;
        dlat = dlat*180/Math.PI;
        double minlat =latitude-dlat;
        double maxlat = latitude+dlat;
        double minlng = longitude -dlng;
        double maxlng = longitude + dlng;
        latLonRange.setMinLat(minlat);
        latLonRange.setMinLon(minlng);
        latLonRange.setMaxLat(maxlat);
        latLonRange.setMaxLon(maxlng);
        latLonRange.setRawLat(latitude);
        latLonRange.setRawLon(longitude);
        return latLonRange;
    }

}

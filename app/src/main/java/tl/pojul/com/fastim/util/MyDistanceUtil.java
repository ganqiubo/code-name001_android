package tl.pojul.com.fastim.util;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.pojul.fastIM.entity.CommunityRoom;

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

}

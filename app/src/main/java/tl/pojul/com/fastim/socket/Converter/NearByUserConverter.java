package tl.pojul.com.fastim.socket.Converter;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.GsonBuilder;
import com.pojul.fastIM.entity.LocUser;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.UserFilter;
import com.pojul.fastIM.message.chat.TagCommuMessage;

import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class NearByUserConverter {

    public List<LocUser> locUserConverter(List<LocUser> rawUsers, BDLocation bdLocation){
        List<LocUser> locUsers = new ArrayList<>();
        if(rawUsers == null || rawUsers.size() <= 0){
            return locUsers;
        }
        for (int i = 0; i < rawUsers.size(); i++) {
            LocUser locUser = rawUsers.get(i);
            if(locUser == null){
                continue;
            }
            if(bdLocation != null){
                locUser.setDistance(DistanceUtil.getDistance(new LatLng(locUser.getLatitude(), locUser.getLongitude()),
                        new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())));
            }
            try{
                UserFilter userFilter = new GsonBuilder().disableHtmlEscaping().create()
                        .fromJson(locUser.getFilter(), UserFilter.class);
                locUser.setUserFilter(userFilter);
            }catch (Exception e){}
            if(userFilter(locUser)){
                locUsers.add(locUser);
            }
        }
        return locUsers;
    }

    public List<LocUser> locUserConverter(List<LocUser> rawUsers, BDLocation bdLocation, List<LocUser> recomdUser){
        List<LocUser> locUsers = new ArrayList<>();
        if(rawUsers == null || rawUsers.size() <= 0){
            return locUsers;
        }
        for (int i = 0; i < rawUsers.size(); i++) {
            LocUser locUser = rawUsers.get(i);
            if(locUser == null){
                continue;
            }
            if(bdLocation != null){
                locUser.setDistance(DistanceUtil.getDistance(new LatLng(locUser.getLatitude(), locUser.getLongitude()),
                        new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())));
            }
            try{
                UserFilter userFilter = new GsonBuilder().disableHtmlEscaping().create()
                        .fromJson(locUser.getFilter(), UserFilter.class);
                locUser.setUserFilter(userFilter);
            }catch (Exception e){}
            if(userFilter(locUser) && !containRecomdUser(locUser, recomdUser)){
                locUsers.add(locUser);
            }
        }
        return locUsers;
    }

    private boolean containRecomdUser(LocUser locUser, List<LocUser> recomdUser){
        if(locUser == null || recomdUser == null || recomdUser.size() <= 0){
            return false;
        }
        for (int i = 0; i < recomdUser.size(); i++) {
            if(locUser.getUserName().equals(recomdUser.get(i).getUserName())){
                return  true;
            }
        }
        return false;
    }

    private boolean userFilter(LocUser locUser) {
        User user = SPUtil.getInstance().getUser();
        if(user == null){
            return false;
        }
        if(locUser.getUserName().equals(user.getUserName())){
            return false;
        }
        UserFilter userFilter = locUser.getUserFilter();
        if(userFilter == null || !userFilter.isFilterEnabled()){
            return true;
        }

        if(userFilter.isWhiteListEnabled() && !ArrayUtil
                .containsStringVal(userFilter.getWhiteListNames(), user.getUserName())){
            return false;
        }
        if(userFilter.isBlackListEnabled() && ArrayUtil.containsStringVal(
                userFilter.getBlackListNames(), user.getUserName())){
            return false;
        }
        if(userFilter.isAgeEnabled() && (user.getAge() < userFilter.getMinAge() || user.getAge() > userFilter.getMaxAge())){
            return false;
        }
        if(userFilter.isSexEnabled() && user.getSex() != userFilter.getSex()){
            return false;
        }
        if(userFilter.isCreditEnabled() && user.getCredit() < userFilter.getCredit()){
            return false;
        }
        if(userFilter.isCertificatEnabled() && user.getCertificate() == 0){
            return false;
        }
        return true;
    }

}

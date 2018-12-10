package tl.pojul.com.fastim.converter;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.NearbyUserFilter;
import com.pojul.fastIM.entity.UserFilter;

public class NUFConverter {

    public NearbyUserFilter converter(NearbyUserFilter nearbyUserFilter){
        if(nearbyUserFilter == null || nearbyUserFilter.getFilter() == null){
            return nearbyUserFilter;
        }
        UserFilter userFilter = new Gson().fromJson(nearbyUserFilter.getFilter(), UserFilter.class);
        nearbyUserFilter.setUserFilter(userFilter);
        return nearbyUserFilter;
    }

}

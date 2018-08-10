package tl.pojul.com.fastim.map.baidu;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pojul.fastIM.entity.BaiduMapConfig;
import com.pojul.fastIM.entity.BaiduMapStyle;

import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.util.Constant;
import tl.pojul.com.fastim.util.FileUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class MapConfigManager {

    private static MapConfigManager mMapConfigManager;

    private List<BaiduMapConfig> baiduMapConfigs;

    private MapConfigManager() {
        setConfigs();
    }

    public static MapConfigManager getInstance() {
        if (mMapConfigManager == null) {
            synchronized (MapConfigManager.class) {
                if (mMapConfigManager == null) {
                    mMapConfigManager = new MapConfigManager();
                }
            }
        }
        return mMapConfigManager;
    }


    public void setConfigs() {
        baiduMapConfigs = new ArrayList<>();
        setDefaultConfig();
        SPUtil.getInstance().putString("BaiduMapConfig", new Gson().toJson(baiduMapConfigs));
        /*if("".equals(SPUtil.getInstance().getString("BaiduMapConfig"))){

        }else{
            String json = SPUtil.getInstance().getString("BaiduMapConfig");
            baiduMapConfigs = new Gson().fromJson(json, new TypeToken<List<BaiduMapConfig>>(){}.getType());
            FileUtil.writeTextToFile(json, (SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) + "/BaiduMap/custom_config"));
        }*/
    }

    public void setDefaultConfig() {
        for (int i = 0; i < 4; i++){
            BaiduMapConfig baiduMapConfig = new BaiduMapConfig();
            BaiduMapStyle baiduMapStyle = new BaiduMapStyle();
            baiduMapConfig.setStylers(baiduMapStyle);
            baiduMapConfigs.add(baiduMapConfig);
        }
        setHighwayConfig(false);
        String json = new Gson().toJson(baiduMapConfigs);
        FileUtil.writeTextToFile(json, (SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) + "/BaiduMap/custom_config"));
    }

    /**
     * 高速公路是否可见, 第0至第3项配置
     * @param visible
     */
    public MapConfigManager setHighwayConfig(boolean visible) {
        BaiduMapConfig highwayConfig0 = baiduMapConfigs.get(0);
        highwayConfig0.setFeatureType("highway");
        highwayConfig0.setElementType("geometry");
        setStyleVisible(highwayConfig0.getStylers(), visible);

        BaiduMapConfig highwayConfig1 = baiduMapConfigs.get(1);
        highwayConfig1.setFeatureType("highway");
        highwayConfig1.setElementType("labels.text.fill");
        setStyleVisible(highwayConfig1.getStylers(), visible);

        BaiduMapConfig highwayConfig2 = baiduMapConfigs.get(2);
        highwayConfig2.setFeatureType("highway");
        highwayConfig2.setElementType("labels.text.stroke");
        setStyleVisible(highwayConfig2.getStylers(), visible);

        BaiduMapConfig highwayConfig3 = baiduMapConfigs.get(3);
        highwayConfig3.setFeatureType("highway");
        highwayConfig3.setElementType("labels.icon");
        setStyleIconVisible(highwayConfig3.getStylers(), visible);
        return this;
    }

    public void saveConfig(){
        if(baiduMapConfigs == null){
            return;
        }
        SPUtil.getInstance().putString("BaiduMapConfig", new Gson().toJson(baiduMapConfigs));
        String json = new Gson().toJson(baiduMapConfigs);
        FileUtil.writeTextToFile(json, (SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) + "/BaiduMap/custom_config"));
    }

    private void setStyleVisible(BaiduMapStyle baiduMapStyle, boolean visible){
        if(visible){
            baiduMapStyle.setColor(null);
        }else{
            baiduMapStyle.setColor("#00000000");
        }
    }

    private void setStyleIconVisible(BaiduMapStyle baiduMapStyle, boolean visible){
        if(visible){
            baiduMapStyle.setVisibility(null);
        }else{
            baiduMapStyle.setVisibility("off");
        }
    }

    public List<BaiduMapConfig> getBaiduMapConfigs() {
        return baiduMapConfigs;
    }
}

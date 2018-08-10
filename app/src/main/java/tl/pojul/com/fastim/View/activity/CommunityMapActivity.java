package tl.pojul.com.fastim.View.activity;

import android.os.Bundle;
import com.baidu.mapapi.map.MapView;
import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.BaiduMapView;
import tl.pojul.com.fastim.util.Constant;
import tl.pojul.com.fastim.util.SPUtil;

public class CommunityMapActivity extends BaseActivity {

    @BindView(R.id.BaiduMapView)
    BaiduMapView mBaiduMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File file = new File((SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) + "/BaiduMap/custom_config"));
        if (file.exists()) {
            MapView.setCustomMapStylePath((SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) + "/BaiduMap/custom_config"));
        }
        setContentView(R.layout.activity_community_map);
        ButterKnife.bind(this);

        MapView.setMapCustomEnable(true);
    }
}

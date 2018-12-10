package tl.pojul.com.fastim.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

public class PackageUtil {

    public boolean applicationExit(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ ){
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

}

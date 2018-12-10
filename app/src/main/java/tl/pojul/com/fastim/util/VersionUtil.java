package tl.pojul.com.fastim.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class VersionUtil {

    public int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = -1;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
        return code;
    }

    public String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        return name;
    }

}

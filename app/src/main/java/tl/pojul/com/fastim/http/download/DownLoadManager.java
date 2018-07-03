package tl.pojul.com.fastim.http.download;

import android.util.Log;

import java.util.Hashtable;

public class DownLoadManager {

    private static DownLoadManager downLoadManager;
    private static Hashtable<String, DownloadTask> downloads = new Hashtable<>();
    private static final String TAG = "DownLoadManager";

    public static DownLoadManager getInstance() {
        if (downLoadManager == null) {
            synchronized (DownLoadManager.class) {
                if (downLoadManager == null) {
                    downLoadManager = new DownLoadManager();
                }
            }
        }
        return downLoadManager;
    }

    /**
     * @param url     下载路径
     * @param downloadLocalPath 下载存放目录
     */
    public void downloadFile(String url, String downloadLocalPath, DownLoadCallBack callback) {
        if(downloads.contains(url)){
            if(callback != null){
                DownloadTask downloadFailTask = new DownloadTask(url, downloadLocalPath, callback);
                downloadFailTask.setE(new Exception("该文件已处于下载列表"));
                callback.downloadFail(downloadFailTask);
            }
            Log.e(TAG, "该文件已处于下载列表");
            return;
        }
        DownloadTask downloadTask = new DownloadTask(url, downloadLocalPath, callback);
        downloads.put(url, downloadTask);
        downloadTask.execute();
    }

    public void cancel(String url){
        if(downloads.contains(url) && downloads.get(url) != null){
            downloads.get(url).cancel();
        }
    }

    public void removeDownLoad(String url){
        if(downloads.contains(url) && downloads.get(url) != null){
            downloads.remove(url);
        }
    }

}

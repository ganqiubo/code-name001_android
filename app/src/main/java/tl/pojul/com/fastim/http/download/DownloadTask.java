package tl.pojul.com.fastim.http.download;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import tl.pojul.com.fastim.util.CharToolsUtil;

public class DownloadTask{

    private String urlPath;
    private String downloadLocalPath;
    private DownLoadCallBack callback;
    private Exception e;
    private int progress;

    private static final String TAG = "DownloadTask";
    private boolean cancel;

    public DownloadTask(String urlPath, String downloadLocalPath, DownLoadCallBack callback) {
        this.urlPath = urlPath;
        this.downloadLocalPath = downloadLocalPath;
        this.callback = callback;
    }

    private Thread downloadThread = new Thread(new Runnable(){
        @Override
        public void run() {
            File file;
            HttpURLConnection httpURLConnection = null;
            try {
                file = new File(downloadLocalPath);
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    setE(new Exception("创建文件失败"));
                    callback.downloadFail(DownloadTask.this);
                    return;
                }
                if(file.exists()){
                    file.delete();
                }
                file.createNewFile();

                String urlFileName = urlPath.substring(urlPath.lastIndexOf(File.separatorChar) + 1);
                String urlBase = urlPath.substring(0, (urlPath.lastIndexOf(File.separatorChar) + 1));
                // 统一资源
                //URL url = new URL(urlBase + URLEncoder.encode(urlFileName, "utf-8"));
                URL url = new URL(CharToolsUtil.Utf8URLencode(urlPath));
                // http的连接类
                httpURLConnection = (HttpURLConnection) url.openConnection();
                // 设定请求的方法，默认是GET
                httpURLConnection.setRequestMethod("GET");
                // 设置连接超时
                httpURLConnection.setConnectTimeout(1000 * 30);
                // 设置字符编码
                //httpURLConnection.setRequestProperty("Charset", "UTF-8");

                // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
                httpURLConnection.connect();

                //httpURLConnection.get

                // 文件大小
                int fileLength = httpURLConnection.getContentLength();

                System.out.println("file length---->" + fileLength);

                BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());

                OutputStream out = new FileOutputStream(file);
                int size = 0;
                int len = 0;
                byte[] buf = new byte[512];
                while ((size = bis.read(buf)) != -1) {
                    if(cancel){
                        callback.downloadCancel(DownloadTask.this);
                        break;
                    }
                    len += size;
                    out.write(buf, 0, size);
                    // 打印下载百分比
                    if(callback != null){
                        setProgress((len * 100 / fileLength));
                        callback.downloadProgress(DownloadTask.this);
                    }
                }
                bis.close();
                out.close();
                if(callback != null){
                    callback.downloadCompete(DownloadTask.this);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                if(callback != null){
                    setE(e);
                    callback.downloadFail(DownloadTask.this);
                }
            }finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                DownLoadManager.getInstance().removeDownLoad(urlPath);
            }
        }
    });

    public void execute(){
        if(downloadThread == null){
            Log.e(TAG, "下载线程已被销毁");
            return;
        }
        downloadThread.start();
    }

    public void cancel(){
        cancel = true;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public String getDownloadLocalPath() {
        return downloadLocalPath;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

}

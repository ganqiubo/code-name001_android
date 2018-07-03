package tl.pojul.com.fastim.http.request;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpRequestManager {

    private static String baseBaiduPicSearchUrl = "https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1530342306102_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8";

    private static String baseSogouPicSearchUrl = "https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1530342306102_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&pn=";

    private static HttpRequestManager httpRequestManager;

    public static HttpRequestManager getInstance() {
        if (httpRequestManager == null) {
            synchronized (HttpRequestManager.class) {
                if (httpRequestManager == null) {
                    httpRequestManager = new HttpRequestManager();
                }
            }
        }
        return httpRequestManager;
    }

    public Call getNetPicSearchReq(String keyWord, String searchEngine, int currentSize){
        OkHttpClient client = new OkHttpClient();
        String searchUrl = "";
        switch (searchEngine){
            case "baidu":
                searchUrl = baseBaiduPicSearchUrl + "&pn=" + currentSize +"&word=" + keyWord;
                break;
            case "sogou":
                break;
        }
        Request request = new Request.Builder().get()
                .url(searchUrl)
                .build();
        Call call = client.newCall(request);
        return call;
    }

}

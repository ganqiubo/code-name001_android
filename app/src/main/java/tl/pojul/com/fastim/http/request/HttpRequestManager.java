package tl.pojul.com.fastim.http.request;

import android.util.Log;

import com.pojul.fastIM.entity.PicFilter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tl.pojul.com.fastim.http.converter.BaiduTranslateConverter;
import tl.pojul.com.fastim.socket.Converter.UploadPicConverter;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.CharToolsUtil;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DialogUtil;

public class HttpRequestManager {

    private static String baseBaiduPicSearchUrl = "https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1530342306102_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8";

    private static String baseSogouPicSearchUrl = "https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1530342306102_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&pn=";

    private static String baseUnsplash = "https://unsplash.com/napi/";

    private HashMap<String, String> englishMap = new HashMap<String, String>(){{
        put("壁纸", "wallpaper");
        put("风景", "Scenery");
        put("自然", "nature");
    }};

    private HashMap<String, String> allEnglishMap = new HashMap<String, String>(){{
        put("壁纸", "wallpaper");
        put("风景", "Scenery");
        put("自然", "nature");
        put("生活", "Life");
        put("建筑", "Architecture");
        put("自拍", "Selfie");
        put("摄影", "Photography");
        put("文艺", "literature");
        put("清新", "fresh");
        put("美女", "Beauty");
        put("萝莉", "Lolita");
        put("迷人", "charming");
        put("宠物", "Pet");
        put("写真", "Portrait");
        put("沙滩", "Sandy beach");
        put("大海", "Sea");
        put("古典", "classical");
        put("唯美", "Aesthetical");
        put("内涵", "connotation");
        put("可爱", "Lovely");
        put("校花", "Campus Belle");
        put("家居", "Home Furnishing");
        put("旅游", "Travel");
    }};

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
        searchUrl = CharToolsUtil.Utf8URLencode(searchUrl);
        Request request = new Request.Builder().get()
                .url(searchUrl)
                .build();
        Call call = client.newCall(request);
        return call;
    }

    public void getUnsplashSearchReq(PicFilter picFilter, int page, int num, CallBack callBack){
        String url;
        if(picFilter.getLabels() != null && picFilter.getLabels().size() > 0){
            StringBuffer sb = new StringBuffer();
            sb.append("https://unsplash.com/napi/search/photos?query=");
            List<String> labels = picFilter.getLabels();
            for (int i = 0; i < labels.size(); i++) {
                String label = labels.get(i);
                if(label == null || label.isEmpty()){
                    continue;
                }
                if(englishMap.get(label) != null){
                    label = englishMap.get(label);
                }
                if(i > 0){
                    sb.append(" ");
                }
                sb.append(label);
            }
            sb.append("&page=");
            sb.append(("" + page));
            sb.append("&per_page=");
            sb.append(("" + num));
            url = sb.toString();
        }else{
            url = "https://unsplash.com/napi/photos?page=" + page + "&per_page=" + num +"";
        }
        url = CharToolsUtil.Utf8URLencode(url);

        String finalUrl = url;
        new Thread(() -> {
            try {
                URL url1 = new URL(finalUrl);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10 * 1000);
                conn.setReadTimeout(10 * 1000);
                conn.setRequestProperty("authority", "unsplash.com");
                conn.setRequestProperty("path", finalUrl.replace("https://unsplash.com", ""));
                conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                conn.setRequestProperty("accept-encoding", "gzip, deflate, br");
                conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
                conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
                conn.setRequestProperty("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
                int code = conn.getResponseCode();
                if (code == 200) {
                    InputStream urlStream = new GZIPInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream,"gb2312"));
                    String line;
                    String result = "";
                    while((line = reader.readLine()) != null) {
                        System.out.println(line);
                        result = result + line;
                    }
                    reader.close();
                    urlStream.close();
                    Log.e("result", "result: " + result);
                    callBack.success(result);
                }else{
                    if(callBack != null){
                        callBack.fail("fail");
                    }
                }
            } catch (Exception e) {
                callBack.fail(e.getMessage());
            }
        }).start();
    }

    public void pexelsPicsReq(PicFilter picFilter, int page, CallBack callBack) {
        String url = "";
        if(picFilter.getLabels() != null && picFilter.getLabels().size() > 0){
            List<String> labels = picFilter.getLabels();
            StringBuffer sb = new StringBuffer();
            sb.append("https://www.pexels.com/search/");
            for (int i = 0; i < labels.size(); i++) {
                String label = labels.get(i);
                if(label == null || label.isEmpty()){
                    continue;
                }
                if(allEnglishMap.get(label) != null){
                    label = allEnglishMap.get(label);
                }
                if(i > 0){
                    sb.append(" ");
                }
                sb.append(label);
            }
            sb.append(("/?page=" + page + "&format=js"));
            url = sb.toString();
            url = CharToolsUtil.Utf8URLencode(url);
            pexelsPicsUrlReq(url, callBack);
        }else{
            url = "https://www.pexels.com/popular-photos/all-time.js?page=" + page;
            url = CharToolsUtil.Utf8URLencode(url);
            pexelsPicsUrlReq(url, callBack);
        }

    }

    public void pexelsPicsUrlReq(String url, CallBack callBack){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(callBack != null){
                    callBack.fail(e.getMessage());
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //if(response.code() == 200){
                if(response == null || response.body() == null){
                    callBack.fail("fail");
                    return;
                }
                String str = response.body().string();
                if(callBack == null){
                    return;
                }
                if(str != null || !str.isEmpty()){
                    callBack.success(str);
                }else{
                    callBack.fail("fail");
                }
                //new UploadPicConverter().converterPexelsPics(str);
                //Log.e("pexelsPicsReq", "pexelsPicsReq: " + str);
                /*}else{
                    callBack.fail("fail");
                }*/
            }
        });
    }

    public void translateZhToEn(String translated, TranslateCallBcck callBack){

    }

    public interface CallBack{
        void fail(String message);

        void success(String response);
    }

    public interface TranslateCallBcck{
        void fail(String message);

        void success(String translate);
    }

}

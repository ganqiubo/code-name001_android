package tl.pojul.com.fastim.http.utils;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.pojul.objectsocket.utils.LogUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttpUitls {

    private static final String TAG = OKHttpUitls.class.getSimpleName();
    private OKHttpGetListener onOKHttpGetListener;
    private MyHandler myHandler = new MyHandler();

    public void post(String url, RequestBody requestBody) {
        try {
            LogUtil.e(TAG, "post url:" + url + ";\n params: " + new Gson().toJson(requestBody));
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);//设置超时时间
            builder.readTimeout(30, TimeUnit.SECONDS);
        /*builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());*/
            OkHttpClient client = builder.build();
            //创建请求对象
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)//传递请求体
                    .build();
            //创建Call请求队列，请求都是放到一个队列里面的
            Call call = client.newCall(request);
            //开始异步请求   即为开启一个子线程
            call.enqueue(new Callback() {
                //失败，成功的方法都是在子线程里面，不能直接更新UI，需要使用Handler
                public void onFailure(Call call, IOException e) { //网络断开的情况
                    Message message = myHandler.obtainMessage();
                    message.obj = "{code:200,message:'" + e.getMessage() + "'}";
                    message.what = 0;
                    myHandler.sendMessage(message);
                }

                public void onResponse(Call call, Response response) throws IOException {
                    Message message = myHandler.obtainMessage();
                    String json = response.body().string();
                    message.obj = json;
                    if (response.code() == 200) {//后台正常处理，没有发生异常的情况
                        message.what = 1;
                    } else {                  //后台发生了异常
                        message.what = 0;
                    }
                    myHandler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            Message message = myHandler.obtainMessage();
            message.obj = "{code:200,message:'" + e.getMessage() + "'}";
            message.what = 0;
            myHandler.sendMessage(message);
        }
    }

    public void get(String url) {
        try {
            LogUtil.e(TAG, "get url:" + url);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);//设置超时时间
            builder.readTimeout(30, TimeUnit.SECONDS);
        /*builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());*/
            OkHttpClient client = builder.build();

            //创建请求对象
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            //创建Call请求队列，请求都是放到一个队列里面的
            Call call = client.newCall(request);
            //开始异步请求   即为开启一个子线程
            call.enqueue(new Callback() {
                //失败，成功的方法都是在子线程里面，不能直接更新UI，需要使用Handler
                public void onFailure(Call call, IOException e) { //网络断开的情况
                    Message message = myHandler.obtainMessage();
                    message.obj = "{code:200,message:'" + e.getMessage() + "'}";
                    message.what = 0;
                    myHandler.sendMessage(message);
                }

                public void onResponse(Call call, Response response) throws IOException {
                    Message message = myHandler.obtainMessage();
                    String json = response.body().string();
                    message.obj = json;
                    if (response.code() == 200) {//后台正常处理，没有发生异常的情况
                        message.what = 1;
                    } else {                  //后台发生了异常
                        message.what = 0;
                    }
                    myHandler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            Message message = myHandler.obtainMessage();
            message.obj = "{code:200,message:'" + e.getMessage() + "'}";
            message.what = 0;
            myHandler.sendMessage(message);
        }
    }

    //在使用工具类的地方实现这个接口，将数据返回，来处理请求结果
    public interface OKHttpGetListener {
        void error(String error);

        void success(String json);
    }

    //将外部实现的接口注入进来
    public void setOnOKHttpGetListener(OKHttpGetListener onOKHttpGetListener) {
        this.onOKHttpGetListener = onOKHttpGetListener;
    }

    //使用Handler，将数据在主线程返回
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {//在这里进行UI界面的更新，这个方法是运行在UI主线程中的
            int w = msg.what;
            if (w == 0) {
                String error = (String) msg.obj;
                onOKHttpGetListener.error(error);
            }
            if (w == 1) {
                String json = (String) msg.obj;
                onOKHttpGetListener.success(json);
            }
        }
    }

}

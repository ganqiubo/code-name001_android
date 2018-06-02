package tl.pojul.com.fastim.net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import tl.pojul.com.fastim.net.Entity.TimeOutTask;

/**
 * Created by gqb on 2018/5/31.
 */

public class RequestTimeOut{
    public static LinkedHashMap<String, TimeOutTask> requestQuenes = new LinkedHashMap<>();

    public static RequestTimeOut requestTimeOut;
    private boolean isWait = false;
    private long monitorInterval = 1000;

    private RequestTimeOut(){
        timeOutMonitor.start();
    }

    public static RequestTimeOut getInstance() {
        if (requestTimeOut == null) {
            synchronized (RequestTimeOut.class) {
                if (requestTimeOut == null) {
                    requestTimeOut = new RequestTimeOut();
                }
            }
        }
        return requestTimeOut;
    }

    public long getMonitorInterval() {
        return monitorInterval;
    }

    public void setMonitorInterval(long monitorInterval) {
        this.monitorInterval = monitorInterval;
    }

    public void addMonitorTask(String requestUid, TimeOutTask timeOutTask){
        synchronized (requestQuenes){
            if(timeOutTask != null && timeOutTask.iRequest !=null){
                requestQuenes.put(requestUid, timeOutTask);
            }
            if(isWait && requestQuenes.size() > 0){
                requestQuenes.notifyAll();
            }
        }

    }

    private Thread timeOutMonitor = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                synchronized (requestQuenes){
                    try {
                        if(requestQuenes.size()<= 0){
                            requestQuenes.wait();
                        }
                        Iterator<Map.Entry<String, TimeOutTask>> iterator= requestQuenes.entrySet().iterator();
                        ArrayList<String> removeKeys = new ArrayList<String>();
                        while(iterator.hasNext()){
                            Map.Entry entry = iterator.next();
                            if(System.currentTimeMillis() > ((TimeOutTask)entry.getValue()).timeout ){
                                removeKeys.add((String) entry.getKey());
                            }
                        }
                        for (int i = 0; i < removeKeys.size(); i++){
                            requestQuenes.remove(removeKeys.get(i));
                        }
                        Thread.sleep(monitorInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });

}

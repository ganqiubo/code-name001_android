package com.pojul.objectsocket.socket;

import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.utils.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by gqb on 2018/5/31.
 */

public class RequestTimeOut{
    public static LinkedHashMap<String, TimeOutTask> requestQuenes = new LinkedHashMap<>();

    public static RequestTimeOut requestTimeOut;
    private boolean isWait = false;
    private long monitorInterval = 1000;
    private boolean stop = false;

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
    
    public void stopMonitor() {
    	stop = true;
    }
    
    public void startMonitor() {
    	stop = false;
    }

    public boolean isMonitor() {
    	return !stop;
    }
    
	public void setMonitorInterval(long monitorInterval) {
        this.monitorInterval = monitorInterval;
    }

    public void addMonitorTask(String requestUid, TimeOutTask timeOutTask){
        synchronized (requestQuenes){
            if(timeOutTask != null && timeOutTask.iRequest !=null){
                LogUtil.d(getClass().getName(), "add request timeout task to RequestQuenes");
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
            while (!stop){
            	try {
                    Thread.sleep(monitorInterval);
                } catch (InterruptedException e) {
                    LogUtil.i(getClass().getName(), e.toString());
                    LogUtil.dStackTrace(e);
                }
                synchronized (requestQuenes){
                    try {
                    	if(stop) {
                    		return;
                    	}
                        if(requestQuenes.size()<= 0){
                            LogUtil.d(getClass().getName(), "RequestQuenes wait");
                            isWait = true;
                            requestQuenes.wait();
                        }
                        LogUtil.d(getClass().getName(), "RequestQuenes Monitor");
                        isWait = false;
                        Iterator<Entry<String, TimeOutTask>> iterator= requestQuenes.entrySet().iterator();
                        ArrayList<String> removeKeys = new ArrayList<String>();
                        while(iterator.hasNext()){
                            Entry<String, TimeOutTask> entry = iterator.next();
                            if(System.currentTimeMillis() > ((TimeOutTask)entry.getValue()).timeout ){
                                removeKeys.add((String) entry.getKey());
                            }
                        }
                        for (int i = 0; i < removeKeys.size(); i++){
                            TimeOutTask mTimeOutTask = requestQuenes.get(removeKeys.get(i));
                            if(mTimeOutTask != null){
                                mTimeOutTask.iRequest.onError("连接超时");
                            }
                            requestQuenes.remove(removeKeys.get(i));
                        }
                    } catch (InterruptedException e) {
                        LogUtil.i(getClass().getName(), e.toString());
                        LogUtil.dStackTrace(e);
                    }
                }
            }
        }
    });

    public void removeMonitorTask(String requestUid){
        synchronized (requestQuenes){
            if(requestQuenes.containsKey(requestUid)){
                requestQuenes.remove(requestUid);
            }
        }
    }

    public boolean isRequestMessage(String messageUid){
        synchronized (requestQuenes){
            if(requestQuenes.containsKey(messageUid)){
                return true;
            }
        }
        return false;
    }

    public void onRequestFinish(ResponseMessage mMessage){
        TimeOutTask mTimeOutTask = requestQuenes.get(mMessage.getMessageUid());
        if(mTimeOutTask != null){
            mTimeOutTask.iRequest.onFinished(mMessage);
        }
        RequestTimeOut.getInstance().removeMonitorTask(mMessage.getMessageUid());
    }

    public void onRequestError(ResponseMessage mMessage, Exception e){
        TimeOutTask mTimeOutTask = RequestTimeOut.requestQuenes.get(mMessage.getMessageUid());
        if(mTimeOutTask != null){
            mTimeOutTask.iRequest.onError(e.getMessage());
        }
        RequestTimeOut.getInstance().removeMonitorTask(mMessage.getMessageUid());
    }
    
    public void clearRequestQuenes() {
    	synchronized (requestQuenes){
    		requestQuenes.clear();
    	}
    }
    
    public void clearRequestQuenesNotify() {
    	synchronized (requestQuenes){
    		requestQuenes.clear();
    		requestQuenes.notifyAll();
    	}
    }

}

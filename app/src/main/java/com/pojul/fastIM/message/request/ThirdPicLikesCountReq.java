package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

import java.util.List;

public class ThirdPicLikesCountReq extends RequestMessage{

    private String gallery;
    private List<String> uids;
    private List<String> urls;

    public ThirdPicLikesCountReq() {
        super();
        setRequestUrl("ThirdPicLikesCountReq");
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public List<String> getUids() {
        return uids;
    }

    public void setUids(List<String> uids) {
        this.uids = uids;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}

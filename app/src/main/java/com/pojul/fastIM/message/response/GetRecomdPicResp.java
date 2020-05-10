package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.RecomdPic;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetRecomdPicResp extends ResponseMessage{

    private List<RecomdPic> recomdPics;

    public List<RecomdPic> getRecomdPics() {
        return recomdPics;
    }

    public void setRecomdPics(List<RecomdPic> recomdPics) {
        this.recomdPics = recomdPics;
    }
}

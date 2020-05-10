package com.pojul.fastIM.message.response;

import com.pojul.objectsocket.message.ResponseMessage;

public class PicCommentResp extends ResponseMessage{
    private long insertId;

    public long getInsertId() {
        return insertId;
    }

    public void setInsertId(long insertId) {
        this.insertId = insertId;
    }
}

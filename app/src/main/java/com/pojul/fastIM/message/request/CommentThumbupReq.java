package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class CommentThumbupReq extends RequestMessage{

    private String commentId;
    private String userName;

    public CommentThumbupReq() {
        super();
        setRequestUrl("CommentThumbupReq");
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

package tl.pojul.com.fastim.dao;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pojul.fastIM.entity.ExtendUploadPic;
import com.pojul.fastIM.entity.Pic;
import com.pojul.fastIM.entity.PicFilter;
import com.pojul.fastIM.entity.SimpleUploadPic;
import com.pojul.fastIM.entity.ThirdPicLikes;
import com.pojul.fastIM.entity.UnsplashEntity;
import com.pojul.fastIM.entity.UnsplashResult;
import com.pojul.fastIM.entity.UploadPic;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.CollectUploadPicReq;
import com.pojul.fastIM.message.request.GetCollectedPicsReq;
import com.pojul.fastIM.message.request.GetLikedPicsReq;
import com.pojul.fastIM.message.request.GetPicsReq;
import com.pojul.fastIM.message.request.LikeUploadPicReq;
import com.pojul.fastIM.message.request.ThirdPicLikesCountReq;
import com.pojul.fastIM.message.request.ThumbupUploadPicReq;
import com.pojul.fastIM.message.request.UserUploadPicReq;
import com.pojul.fastIM.message.response.GetCollectedPicsResp;
import com.pojul.fastIM.message.response.GetLikedPicsResp;
import com.pojul.fastIM.message.response.GetPicsResp;
import com.pojul.fastIM.message.response.ThirdPicLikesCountResp;
import com.pojul.fastIM.message.response.UserUploadPicResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.util.List;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.http.request.HttpRequestManager;
import tl.pojul.com.fastim.socket.Converter.UploadPicConverter;
import tl.pojul.com.fastim.util.ExtendUploadPicUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class LockScreenDao {

    private int num = 5;
    private long page;

    public void reqPics(PicFilter picFilter, CallBack callBack, BDLocation bdLocation, long page){
        if(picFilter == null){
            if(callBack != null){
                callBack.onFail("fail");
            }
            return;
        }
        this.page = page;

        if(picFilter.getOther() != null && !picFilter.getOther().isEmpty()){
            if("我喜欢的".equals(picFilter.getOther())){
                getPicsLiked(picFilter, callBack);
                return;
            }else if("我的收藏".equals(picFilter.getOther())){
                getPicsCollected(picFilter, callBack);
                return;
            }
        }

        if("脚步".equals(picFilter.getGallery())){
            reqPicsFootStep(picFilter, callBack, bdLocation);
        } else if ("unsplash".equals(picFilter.getGallery())) {
            reqPicsUnsplash(picFilter, callBack);
        }else if("pexels".equals(picFilter.getGallery())){
            reqPicsPexels(picFilter, callBack);
        }else{
            if(callBack != null){
                callBack.onFail("fail");
            }
        }
    }

    private void reqPicsPexels(PicFilter picFilter, CallBack callBack) {
        HttpRequestManager.getInstance().pexelsPicsReq(picFilter, (int) (page + 1), new HttpRequestManager.CallBack(){
            @Override
            public void fail(String message) {
                if(callBack != null){
                    callBack.onFail("fail");
                }
            }

            @Override
            public void success(String response) {
                List<ExtendUploadPic> uploadPics = new UploadPicConverter().converterPexelsPics(response);
                reqLikes(uploadPics, picFilter, callBack);
                /*if (callBack != null) {
                    callBack.onSuccess(uploadPics);
                }*/
            }
        });
    }

    private void reqPicsUnsplash(PicFilter picFilter, CallBack callBack) {
        HttpRequestManager.getInstance().getUnsplashSearchReq(picFilter, (int) (page + 1), num, new HttpRequestManager.CallBack() {
            @Override
            public void fail(String message) {
                if(callBack != null){
                    callBack.onFail("fail");
                }
            }

            @Override
            public void success(String response) {
                    List<UnsplashEntity> unsplashEntities = null;
                    try{
                        if(picFilter.getLabels() != null && picFilter.getLabels().size() > 0){
                            UnsplashResult unsplashResult = new Gson().fromJson(response, UnsplashResult.class);
                            if(unsplashResult != null){
                                unsplashEntities = unsplashResult.getResults();
                            }
                        }else{
                            unsplashEntities = new Gson().fromJson(response,
                                    new TypeToken<List<UnsplashEntity>>(){}.getType());
                        }
                        List<ExtendUploadPic> extendUploadPics = new UploadPicConverter().converterUnsplash(unsplashEntities);
                        if(extendUploadPics == null){
                            if(callBack != null){
                                callBack.onFail("fail");
                            }
                            return;
                        }
                        reqLikes(extendUploadPics, picFilter, callBack);
                    }catch (Exception e){}
            }
        });
    }

    private void reqLikes(List<ExtendUploadPic> uploadPics, PicFilter picFilter, CallBack callBack){
        ThirdPicLikesCountReq req = new ThirdPicLikesCountReq();
        req.setGallery(picFilter.getGallery());
        if("unsplash".equals(picFilter.getGallery())){
            List<String> uids = ExtendUploadPicUtil.getUids(uploadPics);
            if(uids.size() <= 0){
                if(callBack != null){
                    callBack.onSuccess(uploadPics);
                }
                return;
            }
            req.setUids(uids);
        }else if("pexels".equals(picFilter.getGallery())){
            List<String> urls = ExtendUploadPicUtil.getUrls(uploadPics);
            if(urls.size() <= 0){
                if(callBack != null){
                    callBack.onSuccess(uploadPics);
                }
                return;
            }
            req.setUrls(urls);
        }
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                if(callBack != null){
                    callBack.onFail("fail");
                }
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                    List<ThirdPicLikes> thirdPicLikes = ((ThirdPicLikesCountResp)mResponse).getThirdPicLikes();
                    setLikes(uploadPics, thirdPicLikes, picFilter);
                if(callBack != null){
                    if(mResponse.getCode() == 200){
                        callBack.onSuccess(uploadPics);
                    }else{
                        callBack.onFail(mResponse.getMessage());
                    }
                }
            }
        });
    }

    private void setLikes(List<ExtendUploadPic> uploadPics, List<ThirdPicLikes> thirdPicLikes, PicFilter picFilter) {
        ExtendUploadPicUtil.setLikes(uploadPics, thirdPicLikes, picFilter.getGallery());
    }


    private void reqPicsFootStep(PicFilter picFilter, CallBack callBack, BDLocation bdLocation) {
        if(picFilter.getOther() != null && !picFilter.getOther().isEmpty()){
            if("我的图集".equals(picFilter.getOther())){
                reqPicsFootStepMine(picFilter, callBack);
            }else if("附近".equals(picFilter.getOther())){
                reqPicsFootStepNormal(picFilter, callBack, true, bdLocation);
            }else{
                if(callBack != null){
                    callBack.onFail("fail");
                }
            }
        }else{
            reqPicsFootStepNormal(picFilter, callBack, false, bdLocation);
        }
    }

    private void reqPicsFootStepNormal(PicFilter picFilter, CallBack callBack, boolean isNearBy, BDLocation bdLocation) {
        User user = SPUtil.getInstance().getUser();
        if(user == null){
            if(callBack != null){
                callBack.onFail("fail");
            }
        }
        GetPicsReq req = new GetPicsReq();
        if(isNearBy){
            if(bdLocation == null){
                if(callBack != null){
                    callBack.onFail("fail");
                }
                return;
            }
            picFilter.setNearBy(true);
            picFilter.setCity(bdLocation.getCity());
            picFilter.setLon(bdLocation.getLongitude());
            picFilter.setLan(bdLocation.getLatitude());
        }
        req.setChoice(false);
        req.setPicFilter(picFilter);
        req.setFromId(user.getId());
        req.setStartNum((page * num));
        req.setNum(num);
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {

            @Override
            public void onError(String msg) {
                if(callBack != null){
                    callBack.onFail(msg);
                }
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                if(mResponse.getCode() == 200){
                    List<ExtendUploadPic> uploadPics = new UploadPicConverter().converterFootStrp(((GetPicsResp)mResponse).getUploadPics(), bdLocation);
                    if(callBack != null){
                        callBack.onSuccess(uploadPics);
                    }
                }else{
                    if(callBack != null){
                        callBack.onFail(mResponse.getMessage());
                    }
                }
            }
        });
    }

    private void reqPicsFootStepMine(PicFilter picFilter, CallBack callBack) {
        User user = SPUtil.getInstance().getUser();
        if(user == null){
            if(callBack != null){
                callBack.onFail("fail");
            }
        }
        UserUploadPicReq req = new UserUploadPicReq();
        req.setNum(num);
        req.setUserId(user.getId());
        req.setStartNum(page * num);
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                if(callBack != null){
                    callBack.onFail(msg);
                }
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                if (mResponse.getCode() == 200) {
                    List<UploadPic> tempUploadPics = new UploadPicConverter().converter(((UserUploadPicResp) mResponse).getUploadPics());
                    List<ExtendUploadPic> extendUploadPics = new UploadPicConverter().converNormal(tempUploadPics);
                    if(callBack != null){
                        callBack.onSuccess(extendUploadPics);
                    }
                } else {
                    if(callBack != null){
                        callBack.onFail(mResponse.getMessage());
                    }
                }
            }
        });
    }

    private void getPicsLiked(PicFilter picFilter, CallBack callBack) {
        User user = SPUtil.getInstance().getUser();
        if(user == null){
            if(callBack != null){
                callBack.onFail("fail");
            }
        }
        GetLikedPicsReq req = new GetLikedPicsReq();
        req.setUserId(user.getId());
        req.setGallery(picFilter.getGallery());
        req.setNum(num);
        req.setStartNum((num * page));
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                if(callBack != null){
                    callBack.onFail(msg);
                }
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                List<ExtendUploadPic> uploadPics = new UploadPicConverter().converterSimple(((GetLikedPicsResp)mResponse).getSimpleUploadPics());
                if(callBack != null){
                    if(mResponse.getCode() == 200){
                        callBack.onSuccess(uploadPics);
                    }else{
                        callBack.onFail(mResponse.getMessage());
                    }
                }
            }
        });
    }

    private void getPicsCollected(PicFilter picFilter, CallBack callBack) {
        User user = SPUtil.getInstance().getUser();
        if(user == null){
            if(callBack != null){
                callBack.onFail("fail");
            }
        }
        GetCollectedPicsReq req = new GetCollectedPicsReq();
        req.setUserId(user.getId());
        req.setGallery(picFilter.getGallery());
        req.setNum(num);
        req.setStartNum((num * page));
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                if(callBack != null){
                    callBack.onFail(msg);
                }
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                List<ExtendUploadPic> uploadPics = new UploadPicConverter().converterSimple(((GetCollectedPicsResp)mResponse).getSimpleUploadPics());
                if(callBack != null){
                    if(mResponse.getCode() == 200){
                        callBack.onSuccess(uploadPics);
                    }else{
                        callBack.onFail(mResponse.getMessage());
                    }
                }
            }
        });
    }

    public void reqLikePic(ExtendUploadPic uploadPic, int type, CallBackEmpty callBack) {
        LikeUploadPicReq req = new LikeUploadPicReq();
        req.setGallery(uploadPic.getGalleryType());
        req.setType(type);
        if("脚步".equals(uploadPic.getGalleryType())){
            req.setLikeUserId(SPUtil.getInstance().getUser().getId());
            req.setUploadPicId(uploadPic.getId());
        }else if("unsplash".equals(uploadPic.getGalleryType())){
            req.setUid(uploadPic.getThirdUid());
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }else if("pexels".equals(uploadPic.getGalleryType())){
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                if(callBack != null){
                    callBack.onFail(msg);
                }
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                if(callBack != null){
                    if(mResponse.getCode() == 200){
                        callBack.onSuccess();
                    }else{
                        callBack.onFail(mResponse.getMessage());
                    }
                }
            }
        });
    }

    public void reqCollectPic(ExtendUploadPic uploadPic, int type, CallBackEmpty callBack) {
        CollectUploadPicReq req = new CollectUploadPicReq();
        req.setType(type);
        req.setGallery(uploadPic.getGalleryType());
        if("脚步".equals(uploadPic.getGalleryType())){
            req.setCollectUserId(SPUtil.getInstance().getUser().getId());
            req.setUploadPicId(uploadPic.getId());
        }else if("unsplash".equals(uploadPic.getGalleryType())){
            req.setUid(uploadPic.getThirdUid());
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }else if("pexels".equals(uploadPic.getGalleryType())){
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                if(callBack != null){
                    callBack.onFail(msg);
                }
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                if(callBack != null){
                    if(mResponse.getCode() == 200){
                        callBack.onSuccess();
                    }else{
                        callBack.onFail(mResponse.getMessage());
                    }
                }
            }
        });
    }

    public void reqThumbUpPic(ExtendUploadPic uploadPic, CallBackEmpty callBack) {
        ThumbupUploadPicReq req = new ThumbupUploadPicReq();
        req.setGallery(uploadPic.getGalleryType());
        if("脚步".equals(uploadPic.getGalleryType())){
            req.setThumbupUpUserId(SPUtil.getInstance().getUser().getId());
            req.setUploadPicId(uploadPic.getId());
        }else if("unsplash".equals(uploadPic.getGalleryType())){
            req.setUid(uploadPic.getThirdUid());
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }else if("pexels".equals(uploadPic.getGalleryType())){
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                if(callBack != null){
                    callBack.onFail(msg);
                }
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                if(callBack != null){
                    if(mResponse.getCode() == 200){
                        callBack.onSuccess();
                    }else{
                        callBack.onFail(mResponse.getMessage());
                    }
                }
            }
        });
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public interface CallBack{
        void onFail(String msg);
        void onSuccess(List<ExtendUploadPic> uploadPics);
    }

    public interface CallBackEmpty{
        void onFail(String msg);
        void onSuccess();
    }


}

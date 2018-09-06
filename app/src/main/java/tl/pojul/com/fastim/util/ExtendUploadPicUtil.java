package tl.pojul.com.fastim.util;

import com.pojul.fastIM.entity.ExtendUploadPic;
import com.pojul.fastIM.entity.ThirdPicLikes;

import java.util.ArrayList;
import java.util.List;

public class ExtendUploadPicUtil {

    public static List<String> getUids(List<ExtendUploadPic> extendUploadPics){
        List<String> uids = new ArrayList<>();
        if(extendUploadPics == null || extendUploadPics.size() <= 0){
            return uids;
        }
        for (int i = 0; i < extendUploadPics.size(); i++) {
            String uid = extendUploadPics.get(i).getThirdUid();
            if(uid == null || uid.isEmpty()){
                continue;
            }
            uids.add(uid);
        }
        return uids;
    }

    public static List<String> getUrls(List<ExtendUploadPic> extendUploadPics){
        List<String> urls = new ArrayList<>();
        if(extendUploadPics == null || extendUploadPics.size() <= 0){
            return urls;
        }
        for (int i = 0; i < extendUploadPics.size(); i++) {
            ExtendUploadPic extendUploadPic = extendUploadPics.get(i);
            if(extendUploadPic.getPics() == null || extendUploadPic.getPics().size() <= 0){
                continue;
            }
            String url = extendUploadPic.getPics().get(0).getUploadPicUrl().getFilePath();
            if(url == null || url.isEmpty()){
                continue;
            }
            urls.add(url);
        }
        return urls;
    }

    public static List<ExtendUploadPic> setLikes(List<ExtendUploadPic> extendUploadPics, List<ThirdPicLikes> thirdPicLikes, String gallery){
        if(extendUploadPics == null || extendUploadPics.size() <= 0
                || thirdPicLikes == null || thirdPicLikes.size() <= 0){
            return extendUploadPics;
        }
        for (int i = 0; i < extendUploadPics.size(); i++) {
            ExtendUploadPic extendUploadPic = extendUploadPics.get(i);
            setLikes(extendUploadPic, thirdPicLikes, gallery);
        }
        return extendUploadPics;
    }

    public static void setLikes(ExtendUploadPic extendUploadPic, List<ThirdPicLikes> thirdPicLikes, String gallery){
        if(extendUploadPic == null){
            return;
        }
        for (int i = 0; i < thirdPicLikes.size(); i++) {
            ThirdPicLikes thirdPicLike = thirdPicLikes.get(i);
            if("unsplash".equals(gallery)){
                if(extendUploadPic.getThirdUid() == null){
                    break;
                }
                if(extendUploadPic.getThirdUid().equals(thirdPicLike.getUid())){
                    extendUploadPic.setHasLiked(thirdPicLike.getHasLiked());
                    extendUploadPic.setHasCollected(thirdPicLike.getHasCollected());
                    extendUploadPic.setHasThubmUp(thirdPicLike.getHasThumbuped());
                    extendUploadPic.setThumbUpNum(thirdPicLike.getThumbupNum());
                    break;
                }
            }else if("pexels".equals(gallery)){
                if(extendUploadPic.getPics() == null || extendUploadPic.getPics().size() <= 0 ||
                        extendUploadPic.getPics().get(0) == null){
                    break;
                }
                String url = extendUploadPic.getPics().get(0).getUploadPicUrl().getFilePath();
                if(url == null ){
                    break;
                }
                if(url.equals(thirdPicLike.getUrl())){
                    extendUploadPic.setHasLiked(thirdPicLike.getHasLiked());
                    extendUploadPic.setHasCollected(thirdPicLike.getHasCollected());
                    extendUploadPic.setHasThubmUp(thirdPicLike.getHasThumbuped());
                    extendUploadPic.setThumbUpNum(thirdPicLike.getThumbupNum());
                    break;
                }
            }
        }
    }

}

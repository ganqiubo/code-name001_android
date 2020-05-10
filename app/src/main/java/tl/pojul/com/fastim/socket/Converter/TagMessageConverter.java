package tl.pojul.com.fastim.socket.Converter;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.fastIM.entity.Pic;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.UserFilter;
import com.pojul.fastIM.message.chat.ReplyMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.objectsocket.utils.Constant;
import com.pojul.objectsocket.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class TagMessageConverter {

    private static final String TAG = "TagMessageConverter";

    public List<TagCommuMessage> converterUserTagMess(List<CommunityMessEntity> communityMessEntities, User user){
        List<TagCommuMessage> tagMessages = new ArrayList<>();
        if(communityMessEntities == null){
            return tagMessages;
        }
        for(int i =0; i < communityMessEntities.size(); i++){
            CommunityMessEntity communityMessEntity = communityMessEntities.get(i);
            if(communityMessEntity == null){
                continue;
            }
            TagCommuMessage tagCommuMessage = converUserTagMess(communityMessEntity, user);
            if(tagCommuMessage != null && userFilter(tagCommuMessage)){
                tagMessages.add(tagCommuMessage);
            }
        }
        return tagMessages;
    }

    private boolean userFilter(TagCommuMessage tagCommuMessage) {
        UserFilter userFilter = tagCommuMessage.getUserFilter();
        if(userFilter == null || !userFilter.isFilterEnabled()){
            return true;
        }
        User user = SPUtil.getInstance().getUser();
        if(user == null){
            return false;
        }
        if(tagCommuMessage.getFrom().equals(user.getUserName())){
            return true;
        }
        if(userFilter.isWhiteListEnabled() && !ArrayUtil
                .containsStringVal(userFilter.getWhiteListNames(), user.getUserName())){
            return false;
        }
        if(userFilter.isBlackListEnabled() && ArrayUtil.containsStringVal(
                userFilter.getBlackListNames(), user.getUserName())){
            return false;
        }
        if(userFilter.isAgeEnabled() && (user.getAge() < userFilter.getMinAge() || user.getAge() > userFilter.getMaxAge())){
            return false;
        }
        if(userFilter.isSexEnabled() && user.getSex() != userFilter.getSex()){
            return false;
        }
        if(userFilter.isCreditEnabled() && user.getCredit() < userFilter.getCredit()){
            return false;
        }
        if(userFilter.isCertificatEnabled() && user.getCertificate() == 0){
            return false;
        }
        return true;
    }

    public List<TagCommuMessage> converterTagMess(List<CommunityMessEntity> communityMessEntities, BDLocation bdLocation){
        List<TagCommuMessage> tagMessages = new ArrayList<>();
        if(communityMessEntities == null){
            return tagMessages;
        }
        for(int i =0; i < communityMessEntities.size(); i++){
            CommunityMessEntity communityMessEntity = communityMessEntities.get(i);
            if(communityMessEntity == null){
                continue;
            }
            TagCommuMessage tagCommuMessage = converTagMess(communityMessEntity, bdLocation);
            if(tagCommuMessage != null && userFilter(tagCommuMessage)){
                tagMessages.add(tagCommuMessage);
            }
        }
        return tagMessages;
    }

    public List<TagCommuMessage> converterTagMess(List<CommunityMessEntity> communityMessEntities, BDLocation bdLocation, List<TagCommuMessage> recomdMesses){
        List<TagCommuMessage> tagMessages = new ArrayList<>();
        if(communityMessEntities == null){
            return tagMessages;
        }
        for(int i =0; i < communityMessEntities.size(); i++){
            CommunityMessEntity communityMessEntity = communityMessEntities.get(i);
            if(communityMessEntity == null){
                continue;
            }
            TagCommuMessage tagCommuMessage = converTagMess(communityMessEntity, bdLocation);
            if(tagCommuMessage != null && userFilter(tagCommuMessage) && !containTagMess(recomdMesses, tagCommuMessage)){
                tagMessages.add(tagCommuMessage);
            }
        }
        return tagMessages;
    }

    private TagCommuMessage converTagMess(CommunityMessEntity communityMessEntity, BDLocation bdLocation) {
        if(communityMessEntity == null){
            return null;
        }
        TagCommuMessage tagCommuMessage = null;
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            tagCommuMessage = (TagCommuMessage)gson
                    .fromJson(communityMessEntity.getMessageContent(), Class.forName(communityMessEntity.getMessageClass()));
            tagCommuMessage.setUserSex(communityMessEntity.getSex());
            tagCommuMessage.setCertificate(communityMessEntity.getCertificate());
            tagCommuMessage.setNickName(communityMessEntity.getNickName());
            tagCommuMessage.setPhoto(communityMessEntity.getPhoto());
            tagCommuMessage.setAge(communityMessEntity.getAge());
            tagCommuMessage.setIsSend(1);
            if(bdLocation != null){
                tagCommuMessage.setDistance(DistanceUtil.getDistance(
                        new LatLng(tagCommuMessage.getLatitude(), tagCommuMessage.getLongitude()),
                        new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())));
            }

            tagCommuMessage.setIsEffective(communityMessEntity.getIsEffective());
            tagCommuMessage.setThumbsUps(communityMessEntity.getThumbUps());
            tagCommuMessage.setHsaThumbsUp(communityMessEntity.getHasThumbUp());
            tagCommuMessage.setHasReport(communityMessEntity.getHasReport());
            tagCommuMessage.setTimeMill(communityMessEntity.getTimeMill());
            tagCommuMessage.setReplysNum(communityMessEntity.getReplyNum());
            if(tagCommuMessage.getPics() != null){
                for (int i = 0; i < tagCommuMessage.getPics().size(); i++) {
                    Pic pic = tagCommuMessage.getPics().get(i);
                    String url = pic.getUploadPicUrl().getFilePath();
                    Pattern pattern = Pattern.compile("http.*?resources/");
                    Matcher matcher = pattern.matcher(url);
                    if(matcher.find()){
                        String str = url.substring(matcher.end(), url.length());
                        pic.getUploadPicUrl().setFilePath(Constant.BASE_URL + str);
                    }
                }
            }
            if(communityMessEntity.getLastReply() != null){
                String[] strs = communityMessEntity.getLastReply().split(",");
                if(strs.length == 3){
                    ReplyMessage replyMessage = new ReplyMessage();
                    replyMessage.setText(strs[0]);
                    replyMessage.setUserName(strs[1]);
                    replyMessage.setNickName(strs[2]);
                    List<ReplyMessage> replyMessages = new ArrayList<>();
                    replyMessages.add(replyMessage);
                    tagCommuMessage.setReplys(replyMessages);
                }
            }
        } catch (Exception e) {
            LogUtil.i(TAG,"解析数据失败");
        }
        return tagCommuMessage;
    }

    public boolean containTagMess(List<TagCommuMessage> recomdMesses, TagCommuMessage tagCommuMessage){
        if(recomdMesses == null || recomdMesses.size() <= 0){
            return false;
        }
        for (int i = 0; i < recomdMesses.size(); i++) {
            TagCommuMessage recomdMess = recomdMesses.get(i);
            if(recomdMess.getMessageUid().equals(tagCommuMessage.getMessageUid())){
                return true;
            }
        }
        return false;
    }

    private TagCommuMessage converUserTagMess(CommunityMessEntity communityMessEntity, User user) {
        if(communityMessEntity == null){
            return null;
        }
        TagCommuMessage tagCommuMessage = null;
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            tagCommuMessage = (TagCommuMessage)gson
                    .fromJson(communityMessEntity.getMessageContent(), Class.forName(communityMessEntity.getMessageClass()));
            tagCommuMessage.setUserSex(user.getSex());
            tagCommuMessage.setCertificate(user.getCertificate());
            tagCommuMessage.setNickName(user.getNickName());
            tagCommuMessage.setPhoto(user.getPhoto());
            tagCommuMessage.setAge(user.getAge());
            tagCommuMessage.setIsSend(1);

            tagCommuMessage.setIsEffective(communityMessEntity.getIsEffective());
            tagCommuMessage.setThumbsUps(communityMessEntity.getThumbUps());
            tagCommuMessage.setHsaThumbsUp(communityMessEntity.getHasThumbUp());
            tagCommuMessage.setHasReport(communityMessEntity.getHasReport());
            tagCommuMessage.setTimeMill(communityMessEntity.getTimeMill());
            tagCommuMessage.setReplysNum(communityMessEntity.getReplyNum());
            if(tagCommuMessage.getPics() != null){
                for (int i = 0; i < tagCommuMessage.getPics().size(); i++) {
                    Pic pic = tagCommuMessage.getPics().get(i);
                    String url = pic.getUploadPicUrl().getFilePath();
                    Pattern pattern = Pattern.compile("http.*?resources/");
                    Matcher matcher = pattern.matcher(url);
                    if(matcher.find()){
                        String str = url.substring(matcher.end(), url.length());
                        pic.getUploadPicUrl().setFilePath(Constant.BASE_URL + str);
                    }
                }
            }
            if(communityMessEntity.getLastReply() != null){
                String[] strs = communityMessEntity.getLastReply().split(",");
                if(strs.length == 3){
                    ReplyMessage replyMessage = new ReplyMessage();
                    replyMessage.setText(strs[0]);
                    replyMessage.setUserName(strs[1]);
                    replyMessage.setNickName(strs[2]);
                    List<ReplyMessage> replyMessages = new ArrayList<>();
                    replyMessages.add(replyMessage);
                    tagCommuMessage.setReplys(replyMessages);
                }
            }
        } catch (Exception e) {
            LogUtil.i(TAG,"解析数据失败");
        }
        return tagCommuMessage;
    }

}

package tl.pojul.com.fastim.converter;

import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.User;
import com.pojul.objectsocket.message.StringFile;

import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.util.ArrayUtil;

public class UserConverter {

    public Friend converterToFriend(User user){
        Friend friend = new Friend();
        if(user == null){
            return null;
        }
        friend.setId(user.getId());
        friend.setUserName(user.getUserName());
        friend.setNickName(user.getNickName());
        friend.setRegistDate(user.getRegistDate());
        friend.setPhoto(user.getPhoto());
        friend.setAutograph(user.getAutograph());
        friend.setSex(user.getSex());
        friend.setCertificate(user.getCertificate());
        friend.setCredit(user.getCredit());
        friend.setAge(user.getAge());
        friend.setBan(user.getBan());
        friend.setShowCommunityLoc(user.getShowCommunityLoc());
        friend.setBirthday(user.getBirthday());
        friend.setBirthdayType(user.getBirthdayType());
        friend.setHobby(user.getHobby());
        friend.setHeight(user.getHeight());
        friend.setWeight(user.getWeight());
        friend.setOccupation(user.getOccupation());
        friend.setEducationalLevel(user.getEducationalLevel());
        friend.setGraduateSchool(user.getGraduateSchool());
        friend.setMypagePhoto(user.getMypagePhoto());
        friend.setImei(user.getImei());
        friend.setNumberValidTime(user.getNumberValidTime());
        return friend;
    }

    public List<User> converByOrder(List<String> userNames, List<User> rawUsers){
        List<User> users = new ArrayList<>();
        for (int i = 0; i < userNames.size(); i++) {
            User user = getUser(userNames.get(i), rawUsers);
            if(user != null){
                users.add(user);
            }
        }
        return users;
    }

    private User getUser(String userName, List<User> rawUsers){
        for (int i = 0; i < rawUsers.size(); i++) {
            User user = rawUsers.get(i);
            if(user == null){
                continue;
            }
            if(userName.equals(user.getUserName())){
                return user;
            }
        }
        return null;
    }

}

package tl.pojul.com.fastim.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pojul.fastIM.entity.ExtendUploadPic;
import com.pojul.fastIM.entity.PicFilter;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.WhiteBlack;
import com.pojul.objectsocket.utils.UidUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPUtil {

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static SPUtil mSPUtil;

    public static String NOTIFY_TAGMESS_REPLY = "notify_tagmess_reply";
    public static String VIBRATE_TAGMESS_REPLY = "vibrate_tagmess_reply";
    public static String NOTIFY_ADDFRIEND_REQ = "notify_addfriedd_req";
    public static String SHOW_KEYGUARD_GALLERY = "show_keyguard_gallery";
    public static String SHOW_SCREEN_GUIDE = "show_screen_guide";
    public static String SHOW_PRIVACY_AGREEMENT = "show_privacy_agreement";

    public SPUtil(Context context) {
        mPreferences =  context.getSharedPreferences("SPUtil" ,Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static void Instance(Context context) {
        if (mSPUtil == null) {
            synchronized (SPUtil.class) {
                if (mSPUtil == null) {
                    mSPUtil = new SPUtil(context);
                }
            }
        }
    }

    public static SPUtil getInstance() {
        return mSPUtil;
    }

    public void putString(String key,String value) {
        mEditor.putString(key,value);
        mEditor.commit();
    }

    public String getString(String key) {
        return mPreferences.getString(key,"");
    }

    public void remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    public User getUser(){
        String userJson =  mPreferences.getString("user","");
        if("".equals(userJson)){
            return null;
        }
        User user = new Gson().fromJson(userJson, User.class);
        return user;
    }

    public void putUser(User user){
        String userJson =  new Gson().toJson(user);
        mEditor.putString("user",userJson);
        mEditor.commit();
    }

    public void addWhiteBlack(WhiteBlack whiteBlack){
        HashMap<String, WhiteBlack> whiteBlacks = getWhiteBlacks();
        if(whiteBlacks == null){
            whiteBlacks = new HashMap<>();
        }
        whiteBlacks.put(whiteBlack.getName(), whiteBlack);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String json = gson.toJson(whiteBlacks);
        mEditor.putString("WhiteBlacks",json);
        mEditor.commit();
    }

    private HashMap<String, WhiteBlack> getWhiteBlacks(){
        try{
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            String whiteBlacksStr = mPreferences.getString("WhiteBlacks","");
            Type type = new TypeToken<HashMap<String, WhiteBlack>>() {}.getType();
            HashMap<String, WhiteBlack> whiteBlacks = gson.fromJson(whiteBlacksStr, type);
            return whiteBlacks;
        }catch(Exception e){
            return null;
        }
    }

    public List<WhiteBlack> getWhiteBlacks(String type){
        List<WhiteBlack> whiteBlackLists = new ArrayList<>();
        if(type == null || (!"white".equals(type) && !"black".equals(type)) ){
            return whiteBlackLists;
        }
        HashMap<String, WhiteBlack> whiteblacks = getWhiteBlacks();
        if(whiteblacks == null){
            return whiteBlackLists;
        }
        for(Map.Entry<String, WhiteBlack> entry: whiteblacks.entrySet()){
            if(type.equals(entry.getValue().getType())){
                whiteBlackLists.add(entry.getValue());
            }
        }
        return whiteBlackLists;
    }

    public void removeWhiteBlack(String name) {
        HashMap<String, WhiteBlack> whiteBlacks = getWhiteBlacks();
        if(whiteBlacks == null){
            return;
        }
        whiteBlacks.remove(name);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String json = gson.toJson(whiteBlacks);
        mEditor.putString("WhiteBlacks",json);
        mEditor.commit();
    }

    public WhiteBlack getWhiteBlack(String name) {
        HashMap<String, WhiteBlack> whiteBlacks = getWhiteBlacks();
        if(whiteBlacks == null){
            return null;
        }
        return whiteBlacks.get(name);
    }

    public boolean containWhiteBlack(String name) {
        HashMap<String, WhiteBlack> whiteblacks = getWhiteBlacks();
        if(whiteblacks == null){
            return false;
        }
        if(whiteblacks.containsKey(name)){
            return true;
        }
        return false;
    }

    public void putArrays(String tokenId, String userName) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 25; i++){
            if( i == ((int)(userName.length() * 0.35f))){
                sb.append(tokenId.substring(16, 32));
            }else if(i == (userName.length())){
                sb.append(tokenId.substring(0, 16));
            }else{
                sb.append(UidUtil.getRandomMd5().substring(0, 16));
            }
        }
        mEditor.putString("arrays", sb.toString());
        mEditor.commit();
    }

    public String getArrays(){
        String arrays = mPreferences.getString("arrays","");
        User user = getUser();
        if(arrays.length() <= 0 || user ==null || user.getUserName() == null){
            return "";
        }
        int start1 = (int)(user.getUserName().length() * 0.35f) * 16;
        int start2 = user.getUserName().length() * 16;
        return ( arrays.substring(start2, (start2 + 16)) + arrays.substring(start1, (start1 + 16)) );
    }

    public void clearArrays(){
        mEditor.putString("arrays", "");
        mEditor.commit();
    }

    public PicFilter getLockSctreenFiler(){
        String json = mPreferences.getString("lock_screen_filter","");
        try {
            PicFilter picFilter = new Gson().fromJson(json, PicFilter.class);
            return picFilter;
        }catch(Exception e){return null;}
    }

    public void saveLockSctreenFiler(PicFilter picFilter){
        try{
            String json = new Gson().toJson(picFilter);
            mEditor.putString("lock_screen_filter", json);
            mEditor.commit();
        }catch(Exception e){}
    }

    public List<ExtendUploadPic> getLastLockPics(){
        String json = mPreferences.getString("last_lock_pics","");
        try {
            List<ExtendUploadPic> pics = new Gson().fromJson(json, new TypeToken<ArrayList<ExtendUploadPic>>(){}.getType());
            return pics;
        }catch(Exception e){return null;}
    }

    public void saveLastLockPics(List<ExtendUploadPic> pics){
        try{
            String json = new Gson().toJson(pics);
            mEditor.putString("last_lock_pics", json);
            mEditor.commit();
        }catch(Exception e){}
    }

    public int getInt(String key, int defaultVal){
        return mPreferences.getInt(key, defaultVal);
    }

    public void putInt(String key, int value){
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public String getExperienceValidTime(){
        String validDate = mPreferences.getString("ExperienceValidTime",null);
        return validDate;
    }

    public void putExperienceValidTime(String validDate){
        mEditor.putString("ExperienceValidTime", validDate);
        mEditor.commit();
    }

}

package tl.pojul.com.fastim.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.WhiteBlack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPUtil {

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static SPUtil mSPUtil;

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
}

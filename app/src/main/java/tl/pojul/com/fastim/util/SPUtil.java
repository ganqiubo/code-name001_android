package tl.pojul.com.fastim.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pojul.fastIM.entity.User;

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

}

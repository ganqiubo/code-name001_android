package tl.pojul.com.fastim.util;

import android.content.Context;

import tl.pojul.com.fastim.View.widget.LoadingDialog;

/**
 * Created by gqb on 2018/5/30.
 */

public class DialogUtil {

    private static DialogUtil mDialogUtil;
    private LoadingDialog mLoadingDialog;

    public static DialogUtil getInstance() {
        if(mDialogUtil == null) {
            synchronized (DialogUtil.class) {
                if(mDialogUtil == null) {
                    mDialogUtil = new DialogUtil();
                }
            }
        }
        return mDialogUtil;
    }

    public void showLoadingDialog(Context ct, String msg){
        if(mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
        LoadingDialog.Builder builder1=new LoadingDialog.Builder(ct)
                .setMessage(msg)
                .setCancelable(false);
        mLoadingDialog=builder1.create();
        mLoadingDialog.show();
    }

    public void dimissLoadingDialog(){
        if(mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
    }

}

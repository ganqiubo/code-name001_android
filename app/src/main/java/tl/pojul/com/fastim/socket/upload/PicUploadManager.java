package tl.pojul.com.fastim.socket.upload;

import android.util.Log;

import com.pojul.fastIM.entity.UploadPic;
import com.pojul.fastIM.message.request.UploadPicReq;
import com.pojul.fastIM.message.response.UploadPicRecordResp;
import com.pojul.fastIM.message.response.UploadPicResp;
import com.pojul.objectsocket.constant.StorageType;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.dao.UploadPicDao;
import tl.pojul.com.fastim.util.SPUtil;

public class PicUploadManager {

    private static PicUploadManager mPicUploadManager;
    private HashMap<String, UploadPicTask> uploadPicTasks = new HashMap<>();
    private List<IUploadPicProgress> iUploadPicProgressList = new ArrayList<>();

    private PicUploadManager(){
        MyApplication.getApplication().registerSendProgress(iSendProgress);
        refreshLocalData();
    }

    public static PicUploadManager getInstance() {
        if (mPicUploadManager == null) {
            synchronized (PicUploadManager.class) {
                if (mPicUploadManager == null) {
                    mPicUploadManager = new PicUploadManager();
                }
            }
        }
        return mPicUploadManager;
    }

    private MyApplication.ISendProgress iSendProgress = new MyApplication.ISendProgress() {
        @Override
        public void progress(BaseMessage message, int progress) {
            if (!(message instanceof  UploadPicReq))return;
            synchronized (uploadPicTasks){
                if(uploadPicTasks.get(message.getMessageUid()) != null){
                    uploadPicTasks.get(message.getMessageUid()).uploadProgress = progress;
                }
            }
            for(int i = 0; i < iUploadPicProgressList.size(); i++){
                IUploadPicProgress iUploadPicProgress = iUploadPicProgressList.get(i);
                if(iUploadPicProgress == null){
                    continue;
                }
                iUploadPicProgress.progress(uploadPicTasks.get(message.getMessageUid()));
            }
        }

        @Override
        public void finish(BaseMessage message) {
        }
    };

    public class UploadPicTask{
        public UploadPicReq uploadPicReq;
        public String uid;
        public int uploadProgress;
        public long fileSize;
        public int type; //0 上传中; 1: 上传失败本地读取
        public boolean isUploading;
    }

    public void addTask(UploadPic uploadPic, long localDataId, int type){

        if(uploadPic == null) return;
        UploadPicReq uploadPicReq = new UploadPicReq();
        uploadPicReq.setUserName(SPUtil.getInstance().getUser().getUserName());
        uploadPicReq.setUploadPic(uploadPic);

        UploadPicTask uploadPicTag= new UploadPicTask();
        uploadPicTag.uploadPicReq = uploadPicReq;
        uploadPicTag.uid = uploadPicReq.getMessageUid();
        uploadPicTag.type = type;
        if(uploadPicReq.getUploadPic().getPics() != null){
            for(int i =0; i < uploadPicReq.getUploadPic().getPics().size(); i++){
                if(uploadPicReq.getUploadPic().getPics().get(i) == null){
                    continue;
                }
                String path = uploadPicReq.getUploadPic().getPics().get(i).getUploadPicUrl().getFilePath();
                File file = new File(path);
                if(file.exists()){
                    uploadPicTag.fileSize = uploadPicTag.fileSize + file.length();
                }
            }
        }
        synchronized (uploadPicTasks){
            uploadPicTasks.put(uploadPicTag.uid, uploadPicTag);
        }
        if(type ==2){
            return;
        }
        uploadPic(uploadPicTag, localDataId);
    }

    public void uploadPic(UploadPicTask uploadPicTag, long localDataId) {
        if(uploadPicTag.isUploading){
            return;
        }
        uploadPicTag.isUploading = true;
        UploadPicReq uploadPicReq = uploadPicTag.uploadPicReq;
        new SocketRequest().request(MyApplication.ClientSocket, uploadPicReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                Log.e("uploadPicManager", "onFinished");
                uploadPicTag.isUploading = false;
                uploadError(uploadPicTag, localDataId);
                for(int i = 0; i < iUploadPicProgressList.size(); i++){
                    IUploadPicProgress iUploadPicProgress = iUploadPicProgressList.get(i);
                    if(iUploadPicProgress == null){
                        continue;
                    }
                    iUploadPicProgress.onError(uploadPicReq, msg);
                }
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                uploadPicTag.isUploading = false;
                if (mResponse.getCode() == 200) {
                    synchronized (uploadPicTasks){
                        uploadPicTasks.remove(uploadPicReq.getMessageUid());
                    }
                    if(localDataId != -1){
                        new UploadPicDao().deleteUploadPic(localDataId);
                    }
                    for(int i = 0; i < iUploadPicProgressList.size(); i++){
                        IUploadPicProgress iUploadPicProgress = iUploadPicProgressList.get(i);
                        if(iUploadPicProgress == null){
                            continue;
                        }
                        uploadPicTag.uploadPicReq.getUploadPic().setUploadPicTime(((UploadPicResp)mResponse).getUploadTime());
                        iUploadPicProgress.onFinished(uploadPicTag);
                    }
                    Log.e("uploadPicManager", "onFinished");
                } else {
                    uploadError(uploadPicTag, localDataId);
                    for(int i = 0; i < iUploadPicProgressList.size(); i++){
                        IUploadPicProgress iUploadPicProgress = iUploadPicProgressList.get(i);
                        if(iUploadPicProgress == null){
                            continue;
                        }
                        iUploadPicProgress.onError(uploadPicReq, mResponse.getMessage());
                    }
                }
            }
        }, 3 * 60 * 1000);
    }

    private void uploadError(UploadPicTask uploadPicTag, long localDataId) {
        if(localDataId == -1){
            new UploadPicDao().saveUploadPic(uploadPicTag.uploadPicReq.getUploadPic(), SPUtil.getInstance().getUser().getUserName(), 2);
        }
        uploadPicTag.type = 2;
    }

    public boolean isTaskValid(UploadPicTask uploadPicTask){
        if(uploadPicTask == null || uploadPicTask.uploadPicReq.getUploadPic() == null
                || uploadPicTask.uploadPicReq.getUploadPic().getPics() == null){
            return false;
        }
        UploadPic uploadPic = uploadPicTask.uploadPicReq.getUploadPic();
        if(uploadPic.getPics().size() <= 0){
            return false;
        }
        for (int i = 0; i < uploadPic.getPics().size(); i++){
            String path = uploadPic.getPics().get(i).getUploadPicUrl().getFilePath();
            uploadPic.getPics().get(i).getUploadPicUrl().setStorageType(StorageType.LOCAL);
            if(!new File(path).exists()){
                return false;
            }
        }
        return true;
    }

    public void registUploadPicProgress(IUploadPicProgress iUploadPicProgress){
        if(iUploadPicProgress == null){
            return;
        }
        synchronized (iUploadPicProgressList){
            iUploadPicProgressList.add(iUploadPicProgress);
        }
    }

    public void unRegistUploadPicProgress(IUploadPicProgress iUploadPicProgress){
        synchronized (iUploadPicProgressList){
            iUploadPicProgressList.remove(iUploadPicProgress);
        }
    }

    public interface IUploadPicProgress{
        void progress(UploadPicTask uploadPicTask);
        void onError(UploadPicReq message, String msg);
        void onFinished(UploadPicTask uploadPicTask);
    }

    public void refreshLocalData(){
        List<UploadPic> uploadPics = new UploadPicDao().queryMyUploadPic(SPUtil.getInstance().getUser().getUserName(), 2);
        if(uploadPics != null && uploadPics.size() >0){
            synchronized (uploadPicTasks){
                for (int i = 0; i < uploadPics.size(); i++){
                    UploadPic uploadPic = uploadPics.get(i);
                    addTask(uploadPic, uploadPic.getId(), 2);
                }
            }
        }
    }

    public List<UploadPicTask> getAllTasks(){
        List<UploadPicTask> uploadPics = new ArrayList<>();
        synchronized (uploadPicTasks){
            for (Map.Entry<String, UploadPicTask> entry: uploadPicTasks.entrySet()) {
                if(entry.getValue() == null){
                    continue;
                }
                uploadPics.add(entry.getValue());
            }
        }
        return uploadPics;
    }

    public void onDestory(){
        MyApplication.getApplication().unRegisterSendProgress(iSendProgress);
    }

    public void deleteItem(UploadPicTask uploadPicTask){
        new UploadPicDao().deleteUploadPic(uploadPicTask.uploadPicReq.getUploadPic().getId());
        synchronized (uploadPicTasks){
            uploadPicTasks.remove(uploadPicTask.uid);
        }
    }

}

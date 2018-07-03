package tl.pojul.com.fastim.View.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pojul.fastIM.message.chat.AudioMessage;
import com.pojul.fastIM.message.chat.FileMessage;
import com.pojul.fastIM.message.chat.NetPicMessage;
import com.pojul.fastIM.message.chat.PicMessage;
import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.constant.StorageType;
import com.pojul.objectsocket.utils.FileClassUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tl.pojul.com.fastim.Audio.AudioManager;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.ChatRoomActivity;
import tl.pojul.com.fastim.View.activity.SingleChatRoomActivity;
import tl.pojul.com.fastim.http.converter.BaiduPicConverter;
import tl.pojul.com.fastim.http.request.HttpRequestManager;
import tl.pojul.com.fastim.util.SPUtil;

public class MoreMessageAdapter extends RecyclerView.Adapter<MoreMessageAdapter.ViewHolder> {

    private static final int REQUEST_CODE_IMAGE_CAPTURE = 2;
    private int chatRoomType;
    private int REQUEST_CODE_IMAGE = 1;
    private int REQUEST_CODE_FILE = 3;
    private Context mContext;
    private List<Integer> typeIconList = new ArrayList() {{
        add(R.drawable.pic);
        add(R.drawable.sound);
        add(R.drawable.take_pic);
        add(R.drawable.record_video);
        add(R.drawable.pic_net);
        add(R.drawable.file);
    }};

    private List<String> typeNoteList = new ArrayList() {{
        add("图片");
        add("语音");
        add("拍照");
        add("视频");
        add("搜图");
        add("文件");
    }};

    private boolean isRecordering;
    private String recorderPath;
    private String imageCapturePath;
    private final String TAG = "MoreMessageAdapter";

    public MoreMessageAdapter(Context mContext, int chatRoomType) {
        this.mContext = mContext;
        this.chatRoomType = chatRoomType;
        checkRecordRermission(false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_more_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.icon.setImageResource(typeIconList.get(position));
        holder.note.setText(typeNoteList.get(position));
        holder.rl.setOnClickListener(v->{
            onItemClick(position);
        });
        holder.rl.setOnLongClickListener(v -> {
            onItemLongClick(position);
            return true;
        });
        holder.rl.setOnTouchListener((View v, MotionEvent event) ->{
            switch(event.getAction()){
                case MotionEvent.ACTION_UP:
                    onItemTouchUp(position);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onItemTouchCancel(position);
                    break;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return typeIconList != null ? typeIconList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.note)
        TextView note;
        @BindView(R.id.rl)
        RelativeLayout rl;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null,null);
            if (cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                createPicMessage(path);
            }
        }else if(requestCode == REQUEST_CODE_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            createPicMessage(imageCapturePath);
            imageCapturePath = "";
        }else if(requestCode == REQUEST_CODE_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = uri.getPath();
            createTypeFileMessage(path);
            Log.e("", "REQUEST_CODE_FILE" + path);
        }
    }

    private void startImageCapture() {
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/footstep/pic";
        File file = new File(filePath);
        if(!file.exists() && !file.mkdirs()){
            showShortToas("获取不到存储空间");
            return;
        }
        String fileName = SPUtil.getInstance().getUser().getNickName() + "_" + System.currentTimeMillis() + ".jpg";
        imageCapturePath = filePath + "/" + fileName;
        file  = new File(imageCapturePath);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        ((ChatRoomActivity) mContext).startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
    }

    public void onItemClick(int position){
        if(mContext == null){
            return;
        }
        switch (position) {
            case 0:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ((ChatRoomActivity) mContext).startActivityForResult(intent, REQUEST_CODE_IMAGE);
                break;
            case 1:
                showShortToas("长按发送语音消息");
                break;
            case 2:
                startImageCapture();
                break;
            case 3:
                //录制视频
                //startImageCapture();
                break;
            case 4:
                ((ChatRoomActivity)mContext).showSearchPic();
                break;
            case 5:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                ((Activity)mContext).startActivityForResult(intent,REQUEST_CODE_FILE);
                break;
        }
    }

    private void onItemLongClick(int position) {
        if(mContext == null){
            return;
        }
        switch (position) {
            case 1:
                checkRecordRermission(true);
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void checkRecordRermission(boolean hasAndRecord){
        new RxPermissions((Activity) mContext).requestEach(Manifest.permission.RECORD_AUDIO).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    if(hasAndRecord){
                        isRecordering = true;
                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/footstep/audio";
                        String fileName = SPUtil.getInstance().getUser().getNickName() + "_" + System.currentTimeMillis() + ".amr";
                        AudioManager.getInstance().startRecording(filePath, fileName);
                        recorderPath = filePath + "/" + fileName;
                    }
                }else {
                    Toast.makeText(mContext, "录音权限被拒绝，请在设置里面开启相应权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onItemTouchUp(int position) {
        if(mContext == null){
            return;
        }
        switch (position){
            case 1:
                if(isRecordering){
                    isRecordering = false;
                    AudioManager.getInstance().stopRecording();
                    createAudioMessage(recorderPath);
                    recorderPath = "";
                }
                break;
        }
    }

    private void onItemTouchCancel(int position) {
        if(mContext == null){
            return;
        }
        switch (position){
            case 1:
                if(isRecordering){
                    isRecordering = false;
                    AudioManager.getInstance().stopRecording();
                    recorderPath = "";
                }
                break;
        }
    }

    public void createPicMessage(String path) {
        if(mContext == null){
            return;
        }
        File file = new File(path);
        if(!file.exists()){
            showShortToas("图片不存在");
            return;
        }
        PicMessage picMessage = new PicMessage();
        picMessage.setChatType(chatRoomType);
        picMessage.setPic(FileClassUtil.createStringFile(path));
        ((ChatRoomActivity) mContext).sendPicMessage(picMessage);
    }

    private void createAudioMessage(String recorderPath) {
        if(mContext == null){
            return;
        }
        File file = new File(recorderPath);
        if(!file.exists()){
            showShortToas("音频文件不存在");
            return;
        }
        AudioMessage audioMessage = new AudioMessage();
        audioMessage.setChatType(chatRoomType);
        audioMessage.setAudio(FileClassUtil.createStringFile(recorderPath));
        ((ChatRoomActivity) mContext).sendAudioMessage(audioMessage);
    }

    private void createTypeFileMessage(String filePath) {
        if(filePath == null || !new File(filePath).exists()){
            showShortToas("文件不存在或路径不正确");
            return;
        }
        if(FileClassUtil.isPathPicFile(filePath)){
            createPicMessage(filePath);
        } else if(FileClassUtil.isPathAudioFile(filePath)){
            createAudioMessage(filePath);
        } else if(FileClassUtil.isPathVideoFile(filePath)){

        } else {
            createFileMessage(filePath);
        }
    }

    private void createFileMessage(String filePath) {
        if(mContext == null){
            return;
        }
        if(filePath == null || !new File(filePath).exists()){
            showShortToas("文件不存在");
            return;
        }
        FileMessage fileMessage = new FileMessage();
        File f = new File(filePath);
        fileMessage.setFile(FileClassUtil.createStringFile(filePath));
        if(f.length() > 10 * 1024 * 1024){
            showShortToas("文件大小超过10M");
        } else {
            ((ChatRoomActivity)mContext).sendSmallFileMessage(fileMessage);
        }
    }

    public void showShortToas(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLongToas(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }
}

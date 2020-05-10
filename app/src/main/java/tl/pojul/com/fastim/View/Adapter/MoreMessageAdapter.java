package tl.pojul.com.fastim.View.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.CommunityMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.Factory.ChatMessageFcctory;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.ChatRoomActivity;
import tl.pojul.com.fastim.View.activity.CommunityChatActivity;
import tl.pojul.com.fastim.View.activity.TagMessageActivity;
import tl.pojul.com.fastim.util.Constant;
import tl.pojul.com.fastim.util.FileUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class MoreMessageAdapter extends RecyclerView.Adapter<MoreMessageAdapter.ViewHolder> {

    private static final int REQUEST_CODE_IMAGE_CAPTURE = 2;
    private int chatRoomType;
    private int REQUEST_CODE_IMAGE = 1;
    private int REQUEST_CODE_FILE = 3;
    private int REQUEST_CODE_RECORD_VIDEO = 4;
    private static final int REQUEST_CODE_TAG = 5;
    private Context mContext;
    private List<Integer> typeIconList = new ArrayList<Integer>() {{
        add(R.drawable.pic);
        add(R.drawable.sound);
        add(R.drawable.take_pic);
        add(R.drawable.record_video);
        add(R.drawable.pic_net);
        //add(R.drawable.file);
    }};

    private List<String> typeNoteList = new ArrayList<String>() {{
        add("图片");
        add("语音");
        add("拍照");
        add("视频");
        add("搜图");
        //add("文件");
    }};

    private boolean isRecordering;
    private String recorderPath;
    private String imageCapturePath;
    private String recorderVideoPath = "";
    private final String TAG = "MoreMessageAdapter";

    public MoreMessageAdapter(Context mContext, int chatRoomType) {
        this.mContext = mContext;
        this.chatRoomType = chatRoomType;
        if(chatRoomType == 3){
            typeIconList.add(R.drawable.label);
            typeNoteList.add("标签");
        }else{
            typeIconList.add(R.drawable.file);
            typeNoteList.add("文件");
        }
        checkRecordRermission(false);
        checkRecordVideoRermission(false);
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
                createMessage(ChatMessageFcctory.TYPE_PIC, path);
            }
        }else if(requestCode == REQUEST_CODE_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            createMessage(ChatMessageFcctory.TYPE_PIC, imageCapturePath);
            imageCapturePath = "";
        }else if(requestCode == REQUEST_CODE_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = uri.getPath();
            createMessage(ChatMessageFcctory.TYPE_FILE, path);
            Log.e("", "REQUEST_CODE_FILE" + path);
        }else if(requestCode == REQUEST_CODE_RECORD_VIDEO && resultCode == Activity.RESULT_OK) {
            createMessage(ChatMessageFcctory.TYPE_VIDEO, recorderVideoPath);
            recorderVideoPath = "";
        }else if(requestCode == REQUEST_CODE_TAG && resultCode == Activity.RESULT_OK){
            String result = data.getExtras().getString("TagCommuMessage");
            TagCommuMessage communityMessage = new Gson().fromJson(result, TagCommuMessage.class);
            ((ChatRoomActivity) mContext).sendChatMessage(communityMessage);
        }
    }

    private void startImageCapture() {
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        String filePath = SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) + "/footstep/pic";
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
                recorderVideoPath = "";
                checkRecordVideoRermission(true);
                break;
            case 4:
                ((ChatRoomActivity)mContext).showSearchPic();
                break;
            case 5:
                if(chatRoomType == 3){
                    Bundle bundle = new Bundle();
                    if(mContext instanceof CommunityChatActivity
                            && ((CommunityChatActivity)mContext).communityRoom.getManager()!=null){
                        bundle.putString("manager", ((CommunityChatActivity)mContext).communityRoom.getManager());
                    }
                    ((BaseActivity)mContext).startActivityForResult(TagMessageActivity.class, bundle, REQUEST_CODE_TAG);
                }else{
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                    ((Activity)mContext).startActivityForResult(intent,REQUEST_CODE_FILE);
                }
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

    private void startRecordVideo(){
        String filePath = SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) + "/footstep/video";
        String fileName = SPUtil.getInstance().getUser().getNickName() + "_" + System.currentTimeMillis() + ".mp4";
        File dir = new File(filePath);
        if(!dir.exists() && !dir.mkdirs()){
            showShortToas("创建文件失败");
            return;
        }
        recorderVideoPath = filePath + "/" + fileName;
        File file = new File(recorderVideoPath);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri fileUri = Uri.fromFile(file);//设置视频录制保存地址的uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);     //限制持续时长
        ((Activity)mContext).startActivityForResult(intent, REQUEST_CODE_RECORD_VIDEO);
    }

    /**
     * @param hasAndRecord
     * */
    @SuppressLint("CheckResult")
    private void checkRecordVideoRermission(boolean hasAndRecord){
        new RxPermissions((Activity) mContext).requestEach(Manifest.permission.RECORD_AUDIO).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    if(hasAndRecord){
                        startRecordVideo();
                    }
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void checkRecordRermission(boolean hasAndRecord){
        new RxPermissions((Activity) mContext).requestEach(Manifest.permission.RECORD_AUDIO).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    if(hasAndRecord){
                        isRecordering = true;
                        String filePath = SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) + "/footstep/audio";
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
                    createMessage(ChatMessageFcctory.TYPE_AUDIO, recorderPath);
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

    public void createMessage(int chatMessageType, String path){
        if(mContext == null || path == null){
            return;
        }
        File file = new File(path);
        if(!file.exists()){
            showShortToas("文件不存在");
            return;
        }
        if(file.length() > Constant.MAX_UPLOAD_SIZE){
            showShortToas("上传文件不能超过20M" + FileUtil.getDataSize(Constant.MAX_UPLOAD_SIZE));
            return;
        }
        if(file.length() > Constant.NEW_TASK_UPLOAD_SIZE){
            showShortToas("文件大小超过5M");
        }else{
            ChatMessage chatMessage = new ChatMessageFcctory().create(chatMessageType, path, chatRoomType);
            ((ChatRoomActivity) mContext).sendChatMessage(chatMessage);
        }
    }

    public void showShortToas(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLongToas(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }
}

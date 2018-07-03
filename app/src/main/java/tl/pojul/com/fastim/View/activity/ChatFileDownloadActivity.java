package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.pojul.fastIM.message.chat.FileMessage;
import com.pojul.objectsocket.constant.StorageType;

import java.io.File;
import java.nio.file.FileSystem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.http.download.DownLoadCallBack;
import tl.pojul.com.fastim.http.download.DownLoadManager;
import tl.pojul.com.fastim.http.download.DownloadTask;
import tl.pojul.com.fastim.util.FileUtil;

public class ChatFileDownloadActivity extends BaseActivity {

    @BindView(R.id.file_icon)
    ImageView fileIcon;
    @BindView(R.id.file_name)
    TextView fileName;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.file_operation)
    Button fileOperation;
    @BindView(R.id.file_size)
    TextView fileSize;

    private FileMessage fileMessage;
    private String fileLocalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_file_download);
        ButterKnife.bind(this);

        try {
            fileMessage = new Gson().fromJson(getIntent().getStringExtra("fileMessage"), FileMessage.class);
        } catch (Exception e) {
            showShortToas("数据异常");
            finish();
        }
        init();

    }

    private void init() {
        fileName.setText(" " + fileMessage.getFile().getFileName() + " ");
        if(fileMessage.getFile().getFileSize() > 0){
            fileSize.setText("文件大小：" + FileUtil.getDataSize(fileMessage.getFile().getFileSize()));
        }else if(fileMessage.getFile().getFileSize() == 0){
            fileSize.setText("文件大小：0bytes");
        }

        File file;
        if (fileMessage.getFile().getStorageType() == StorageType.LOCAL) {
            fileLocalPath = fileMessage.getFile().getFilePath();
            file = new File(fileMessage.getFile().getFilePath());
            if (file.exists()) {
                progress.setProgress(100);
                fileOperation.setText("查看文件");
            } else {
                progress.setProgress(0);
                fileOperation.setText("文件不存在");
            }
        } else {
            fileLocalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/footstep/others/" + fileMessage.getFile().getFileName();
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/footstep/others/" + fileMessage.getFile().getFileName());
            if (file.exists()) {
                progress.setProgress(100);
                fileOperation.setText("查看文件");
            } else {
                progress.setProgress(0);
                fileOperation.setText("下载文件");
            }
        }
    }

    @OnClick({R.id.file_operation})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.file_operation:
                switch (fileOperation.getText().toString()) {
                    case "下载文件":
                        startDownload();
                        break;
                    case "取消下载":
                        DownLoadManager.getInstance().cancel(fileMessage.getFile().getFilePath());
                        break;
                    case "查看文件":
                        File file = new File(fileLocalPath);
                        if(!file.exists()){
                            showShortToas("文件已被删除");
                            if(fileMessage.getFile().getStorageType() == StorageType.LOCAL){
                                fileOperation.setText("文件不存在");
                            }else{
                                fileOperation.setText("下载文件");
                                progress.setProgress(0);
                            }
                        } else {
                            openFile(file);
                        }
                        break;
                    case "文件不存在":
                        break;
                    case "重新下载":
                        startDownload();
                        break;
                }
                break;
        }
    }

    public void startDownload() {
        File file = new File((Environment.getExternalStorageDirectory().getAbsolutePath() + "/footstep/others/"));
        if (!file.exists() && !file.mkdirs()) {
            showShortToas("创建下载文件失败");
            return;
        }
        DownLoadManager.getInstance().downloadFile(fileMessage.getFile().getFilePath(),
                (file.getAbsolutePath() + "/" + fileMessage.getFile().getFileName()),
                new DownloadCallBack());
        fileOperation.setText("取消下载");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class DownloadCallBack extends DownLoadCallBack{
        @Override
        public void downloadFail(DownloadTask task) {
            super.downloadFail(task);
            runOnUiThread(()->{
                progress.setProgress(0);
                showShortToas("下载失败");
                fileOperation.setText("重新下载");
                FileUtil.deleteFile(task.getDownloadLocalPath());
            });
        }

        @Override
        public void downloadProgress(DownloadTask task) {
            super.downloadProgress(task);
            runOnUiThread(()->{
                progress.setProgress(task.getProgress());
            });
        }

        @Override
        public void downloadCompete(DownloadTask task) {
            super.downloadCompete(task);
            runOnUiThread(()->{
                progress.setProgress(100);
                fileOperation.setText("查看文件");
            });
        }

        @Override
        public void downloadCancel(DownloadTask task) {
            super.downloadCancel(task);
            runOnUiThread(()->{
                progress.setProgress(0);
                fileOperation.setText("下载文件");
                FileUtil.deleteFile(task.getDownloadLocalPath());
            });
        }
    }

    private void openFile(File file){
        try{
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);  //设置intent的Action属性
            String type = FileUtil.getMIMEType(file);  //获取文件file的MIME类型
            intent.setDataAndType(/*uri*/Uri.fromFile(file), type);   //设置intent的data和Type属性。
            startActivity(intent);     //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
        }catch (Exception e){
            showShortToas("找不到对应的文件查看器");
        }
    }
}

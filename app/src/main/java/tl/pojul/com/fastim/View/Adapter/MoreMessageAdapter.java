package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;

public class MoreMessageAdapter extends RecyclerView.Adapter<MoreMessageAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> typeIconList = new ArrayList() {{
        add(R.drawable.pic);
        add(R.drawable.sound);
        add(R.drawable.take_pic);
        add(R.drawable.expression);
        add(R.drawable.gif);
        add(R.drawable.file);
    }};

    private List<String> typeNoteList = new ArrayList() {{
        add("图片");
        add("语音");
        add("照相");
        add("表情");
        add("gif");
        add("文件");
    }};
    private OnItemClickListener mOnItemClickListener;

    public MoreMessageAdapter(Context mContext) {
        this.mContext = mContext;
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
        int i = Build.VERSION.SDK_INT;
        holder.rl.setOnClickListener(v->{
            if(mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(position);
            }
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


}

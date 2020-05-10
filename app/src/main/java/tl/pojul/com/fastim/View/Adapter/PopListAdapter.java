package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.DialogUtil;

public class PopListAdapter extends RecyclerView.Adapter<PopListAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> mList;
    private OnItemClick onclick;

    public PopListAdapter(Context mContext, List<String> mList, OnItemClick onclick) {
        this.mContext = mContext;
        this.mList = mList;
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_text, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.text.setText(mList.get(position));
        holder.text.setOnClickListener(v->{
            if(onclick != null){
                onclick.click(position);
            }
        });
        if(position >= (mList.size() -1)){
            holder.line.setVisibility(View.GONE);
        }else{
            holder.line.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.line)
        View line;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClick{
        void click(int position);
    }

}

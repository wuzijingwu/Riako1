package text.bwei.com.wuzijinglianxi2.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import text.bwei.com.wuzijinglianxi2.R;
import text.bwei.com.wuzijinglianxi2.bean.News;

/**
 * Created by dell on 2017/11/11.
 */

public class Myadapter extends RecyclerView.Adapter {

    List<News.DataBean> list;
    private MyViewHolder myViewHolder;

    public Myadapter(List<News.DataBean> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        myViewHolder = (MyViewHolder) holder;
        myViewHolder.textView.setText(list.get(position).getTitle());
        Uri uri = Uri.parse(list.get(position).getImg());
        myViewHolder.simpleDraweeView.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        private final SimpleDraweeView simpleDraweeView;
        private final TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = itemView.findViewById(R.id.sdv_image);
            textView = itemView.findViewById(R.id.text);
        }
    }

}

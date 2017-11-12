package text.bwei.com.riako;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Bean.DataBean> data;
    private String url = "http://api.expoon.com/AppNews/getNewsList/type/1/p/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getDates();


    }

    public void getDates() {
        OkHttp.getAsync(url, new OkHttp.DataCallBack() {


            @Override
            public void requestFailure(Request request, IOException e) {


            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Gson gson = new Gson();
                Bean bean = gson.fromJson(result, Bean.class);
                data = bean.getData();
                recyclerView.setAdapter(new MyAdapter());


            }
        });


    }

    class MyAdapter extends RecyclerView.Adapter {


        private MyViewHolder myViewHolder;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new MyViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            myViewHolder = (MyViewHolder) holder;
            myViewHolder.text.setText(data.get(position).getNews_title());
            Uri imgUrl = Uri.parse(data.get(position).getPic_url());
//            SimpleDraweeView urlImg = (SimpleDraweeView) findViewById(R.id.sdv_image_url);
            myViewHolder.simpleDraweeView.setImageURI(imgUrl);

        }

        @Override
        public int getItemCount() {

            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {


            private final SimpleDraweeView simpleDraweeView;
            private final TextView text;

            public MyViewHolder(View itemView) {
                super(itemView);
                simpleDraweeView = itemView.findViewById(R.id.sdv_image);
                text = itemView.findViewById(R.id.text);


            }
        }
    }
}

package text.bwei.com.wuzijinglianxi2.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import text.bwei.com.wuzijinglianxi2.Api.Api;
import text.bwei.com.wuzijinglianxi2.R;
import text.bwei.com.wuzijinglianxi2.adapter.Myadapter;
import text.bwei.com.wuzijinglianxi2.bean.News;
import text.bwei.com.wuzijinglianxi2.presenter.presenter;

public class MainActivity extends AppCompatActivity implements Iview {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private int p = 1;
    //    private String  page_1.json
    private LinearLayoutManager linearLayoutManager;
    private Myadapter myadapter;
    private int pages = 1;
    private text.bwei.com.wuzijinglianxi2.presenter.presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new presenter(this);
        presenter.getOk(Api.BUTH, pages);
        initview();
    }

    public void initview() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipere);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
    }


    @Override
    public void showSuccess(final List<News.DataBean> list) {
        linearLayoutManager = new LinearLayoutManager(this);
        myadapter = new Myadapter(list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myadapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition == list.size() - 1) {
                    pages++;
                    presenter.getOk(Api.BUTH, pages);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pages++;
                presenter.getOk(Api.BUTH, pages);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    @Override
    public void showError(String s) {

    }
}

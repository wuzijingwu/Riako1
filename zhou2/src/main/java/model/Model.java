package model;

import java.util.List;

import bean.News;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tools.API;
import tools.APIService;

/**
 * Created by T_baby on 17/11/11.
 */

public class Model implements Imodel{
    SetDate setDate;
    String url;
    public Model(SetDate setDate,String url) {
        this.setDate = setDate;
        this.url=url;
    }

    @Override
    public void RequestDate() {
         //数据请求
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(API.BASEURL)
                .build();
        final APIService service = retrofit.create(APIService.class);
        service.GetDate(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<News>>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(List<News> news) {
                        setDate.Setviewdate(news.get(0));
                    }
                });
    }
    public interface SetDate{
        void Setviewdate(News news);
    }
}

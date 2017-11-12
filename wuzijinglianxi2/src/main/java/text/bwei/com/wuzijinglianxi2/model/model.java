package text.bwei.com.wuzijinglianxi2.model;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import text.bwei.com.wuzijinglianxi2.Api.Api;
import text.bwei.com.wuzijinglianxi2.Api.Apiservers;
import text.bwei.com.wuzijinglianxi2.bean.News;

/**
 * Created by dell on 2017/11/11.
 */

public class model implements Imodel {
    @Override
    public void RequestSuccess(String url, int page,final OnselectLinsenter onselectLinsenter) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BUTH).
                addConverterFactory(GsonConverterFactory.create()).
                addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
                build();
        //通过动态代理得到网络接口对象
        Apiservers apiservers = retrofit.create(Apiservers.class);

        //得到Observable
        Observable<List<News>> getdate = apiservers.getdatas(page);
        getdate.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<News>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<News> newses) {
                        onselectLinsenter.datasSuccsee(newses.get(0).getData());
                    }
                });


    }
}

package text.bwei.com.wuzijinglianxi2.Api;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import text.bwei.com.wuzijinglianxi2.bean.News;

/**
 * Created by dell on 2017/11/11.
 */

public interface Apiservers {
//    page_1.json
//    @GET("wap/data/news/txs/page_.json")
//    Observable<List<News>> getdate();
@GET("wap/data/news/txs/page_{pages}.json")
Observable<List<News>> getdatas(@Path("pages") int pages);
}

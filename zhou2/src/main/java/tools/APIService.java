package tools;

import java.util.List;

import bean.News;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by T_baby on 17/11/11.
 */

public interface APIService {
    @POST
    Observable<List<News>> GetDate(@Url String url);
}

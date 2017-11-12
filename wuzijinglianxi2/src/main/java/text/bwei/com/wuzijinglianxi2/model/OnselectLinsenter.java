package text.bwei.com.wuzijinglianxi2.model;

import java.util.List;

import text.bwei.com.wuzijinglianxi2.bean.News;

/**
 * Created by dell on 2017/11/11.
 */

public interface OnselectLinsenter {
    void datasSuccsee(List<News.DataBean> list);

    void daatsError(String s);


}

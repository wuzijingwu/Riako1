package text.bwei.com.wuzijinglianxi2.view;

import java.util.List;

import text.bwei.com.wuzijinglianxi2.bean.News;

/**
 * Created by dell on 2017/11/11.
 */

public interface Iview {
    void showSuccess(List<News.DataBean> list);
    void showError(String s);


}

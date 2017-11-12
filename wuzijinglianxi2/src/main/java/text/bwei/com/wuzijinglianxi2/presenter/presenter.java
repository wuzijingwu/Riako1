package text.bwei.com.wuzijinglianxi2.presenter;

import java.util.List;

import text.bwei.com.wuzijinglianxi2.bean.News;
import text.bwei.com.wuzijinglianxi2.model.Imodel;
import text.bwei.com.wuzijinglianxi2.model.OnselectLinsenter;
import text.bwei.com.wuzijinglianxi2.model.model;
import text.bwei.com.wuzijinglianxi2.view.Iview;
import text.bwei.com.wuzijinglianxi2.view.MainActivity;

/**
 * Created by dell on 2017/11/11.
 */

public class presenter {
    Imodel imodel;
    Iview iview;

    public presenter(MainActivity iview) {
        this.iview = iview;
        imodel = new model();

    }

    public void getOk(String url,int page) {
        imodel.RequestSuccess(url,page, new OnselectLinsenter() {
            @Override
            public void datasSuccsee(List<News.DataBean> list) {
                iview.showSuccess(list);
            }

            @Override
            public void daatsError(String s) {
                iview.showError(s);
            }
        });


    }


}

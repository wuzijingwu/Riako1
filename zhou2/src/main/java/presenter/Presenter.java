package presenter;

import model.Model;

import bean.News;
import view.Iview;

/**
 * Created by T_baby on 17/11/11.
 */

public class Presenter implements Model.SetDate {
    Model model;
    Iview iview;
    public Presenter( Iview iview,String url) {
        this.iview = iview;
        this.model = new Model(this,url);
    }
    public void Getdate(){
        model.RequestDate();
    }
    @Override
    public void Setviewdate(News news) {
        iview.GetViewdate(news);
    }
}

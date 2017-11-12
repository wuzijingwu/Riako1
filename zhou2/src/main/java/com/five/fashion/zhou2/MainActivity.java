package com.five.fashion.zhou2;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.loopj.android.http.HttpGet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import bean.Myadapter;
import bean.News;
import bean.SPUtils;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpHead;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import presenter.Presenter;
import view.Iview;

public class MainActivity extends AppCompatActivity implements Iview {
    // http://mnews.gw.com.cn/wap/data/news/txs/page_1.json
    XRecyclerView recycleview;
    List<News.DataBean> list;
    LinearLayoutManager linearLayoutManager;
    int lastVisibleItem;
    int pager = 1;
    Presenter presenter;

    //进度条
    protected static final String TAG = "OtherActivity";
    //下载线程的数量
    private final static int threadsize = 3;
    protected static final int SET_MAX = 0;
    public static final int UPDATE_VIEW = 1;
    private SPUtils share;
    private LinearLayout linearLayout;
    private ProgressBar pb;
    private String url;
    private TextView tv_info;
    private int loadingid = 0;
    //显示进度和更新进度
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SET_MAX://设置进度条的最大值
                    int filelength = msg.arg1;
                    pb.setMax(filelength);
                    break;
                case UPDATE_VIEW://更新进度条  和 下载的比率
                    int len = msg.arg1;//新下载的长度
                    pb.setProgress(pb.getProgress() + len);//设置进度条的刻度

                    int max = pb.getMax();//获取进度的最大值
                    int progress = pb.getProgress();//获取已经下载的数据量
                    //  下载：30    总：100
                    int result = (progress * 100) / max;
                    tv_info.setText("下载:" + result + "%");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //数据库
        share = new SPUtils();
        //初始化控件
        tv_info = findViewById(R.id.progress);
        linearLayout = findViewById(R.id.barlayout);
        pb = findViewById(R.id.bar);
        recycleview = findViewById(R.id.recycle);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recycleview.setLayoutManager(linearLayoutManager);
        list = new ArrayList<News.DataBean>();
        presenter = new Presenter(this, "page_" + pager + ".json");
        presenter.Getdate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void GetViewdate(News news) {
        List<News.DataBean> data = news.getData();
        if (pager == 1)
            for (int i = 0; i < data.size(); i++) {
                share.put(MainActivity.this, "myimg" + i, data.get(i).getImg());
            }
        list.clear();
        list.addAll(data);
        final Myadapter adapter = new Myadapter(list, MainActivity.this);
        recycleview.setAdapter(adapter);
        //点击事件
        adapter.SetItemListion(new Myadapter.ItemListion() {
            @Override
            public void OnitemListion(View view, int Postion) {
                linearLayout.setVisibility(View.VISIBLE);
                url = (String) share.get(MainActivity.this, "myimg" + Postion, "http://c.hiphotos.baidu.com/image/pic/item/b90e7bec54e736d1e51217c292504fc2d46269f3.jp");
                createfile(url);
                if (loadingid != Postion) {
                    createfile(url);
                    pb.setProgress(0);
                    loadingid = Postion;
                    download(view);
                } else {
                    Toast.makeText(MainActivity.this,"相同",Toast.LENGTH_SHORT).show();
                    pause(view);
                }
            }
        });
        //上拉刷新，下拉加载
        recycleview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                try {
                    pager = 1;
                    Thread.sleep(2000);
                    presenter = new Presenter(MainActivity.this, "page_" + pager + ".json");
                    presenter.Getdate();
                    adapter.notifyDataSetChanged();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "下拉", Toast.LENGTH_SHORT).show();
                // 当您停止刷新或加载更多的工作
                recycleview.refreshComplete();
            }
            @Override
            public void onLoadMore() {
                try {
                    pager++;
                    Thread.sleep(2000);
                    presenter = new Presenter(MainActivity.this, "page_"+pager+".json");
                    presenter.Getdate();
                    adapter.notifyDataSetChanged();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 通知加载更多的工作完成
                recycleview.loadMoreComplete();
                Toast.makeText(MainActivity.this, "上拉", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //创建文件
    public void createfile(String url) {
        //确定下载的文件
        String name = getFileName(url);
        File file = new File(Environment.getExternalStorageDirectory(), name);
        if (file.exists()) {//文件存在回显
            //获取文件的大小
            int filelength = (int) file.length();
            pb.setMax(filelength);
            try {
                //统计原来所有的下载量
                int count = 0;
                //读取下载记录文件
                for (int threadid = 0; threadid < threadsize; threadid++) {
                    //获取原来指定线程的下载记录
                    int existDownloadLength = readDownloadInfo(threadid);
                    count = count + existDownloadLength;
                }
                //设置进度条的刻度
                pb.setProgress(count);
                //计算比率
                int result = (count * 100) / filelength;
                tv_info.setText("下载:" + result + "%");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean flag = false;//是否在下载
    //暂停
    public void pause(View v) {
        flag = false;
    }

    //下载
    public void download(View v) {
        flag = true; //是在下载
        new Thread() {//子线程
            public void run() {
                try {
                    //获取服务器上文件的大小
                    HttpClient client = new DefaultHttpClient();
                    HttpHead request = new HttpHead(url);
                    HttpResponse response = client.execute(request);
                    //response  只有响应头  没有响应体
                    if (response.getStatusLine().getStatusCode() == 200) {
                        Header[] headers = response.getHeaders("Content-Length");
                        String value = headers[0].getValue();
                        //文件大小
                        int filelength = Integer.parseInt(value);
                        Log.i(TAG, "filelength:" + filelength);

                        //设置进度条的最大值
                        Message msg_setmax = Message.obtain(mHandler, SET_MAX, filelength, 0);
                        msg_setmax.sendToTarget();


                        //处理下载记录文件
                        for (int threadid = 0; threadid < threadsize; threadid++) {
                            //对应的下载记录文件
                            File file = new File(Environment.getExternalStorageDirectory(), threadid + ".txt");
                            //判断文件是否存在
                            if (!file.exists()) {
                                //创建文件
                                file.createNewFile();
                            }
                        }

                        //在sdcard创建和服务器大小一样的文件
                        String name = getFileName(url);
                        File file = new File(Environment.getExternalStorageDirectory(), name);
                        //随机访问文件
                        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                        //设置文件的大小
                        raf.setLength(filelength);
                        //关闭
                        raf.close();
                        //计算每条线程的下载量
                        int block = (filelength % threadsize == 0) ? (filelength / threadsize) : (filelength / threadsize + 1);
                        //开启三条线程执行下载
                        for (int threadid = 0; threadid < threadsize; threadid++) {
                            new DownloadThread(threadid, url, file, block).start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    //线程下载类
    private class DownloadThread extends Thread {
        private int threadid;//线程的id
        private String uri;//下载的地址
        private File file;//下载文件
        private int block;//下载的块
        private int start;
        private int end;

        public DownloadThread(int threadid, String uri, File file, int block) {
            super();
            this.threadid = threadid;
            this.uri = uri;
            this.file = file;
            this.block = block;
            //计算下载的开始位置和结束位置
            start = threadid * block;
            end = (threadid + 1) * block - 1;
            try {
                //读取该条线程原来的下载记录
                int existDownloadLength = readDownloadInfo(threadid);

                //修改下载的开始位置   从新下载
                start = start + existDownloadLength;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //下载   状态码：200是普通的下载      206是分段下载        Range:范围
        @Override
        public void run() {
            super.run();
            try {
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                //跳转到起始位置
                raf.seek(start);

                //分段下载
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(uri);
                request.addHeader("Range", "bytes:" + start + "-" + end);//添加请求头
                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStream inputStream = response.getEntity().getContent();
                    //把流写入到文件
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        //如果暂停下载  点击暂停 false 就直接return 点击下载true接着下载
                        if (!flag) {
                            return;//标准线程结束
                        }
                        //写数据
                        raf.write(buffer, 0, len);

                        //读取原来下载的数据量 这里读取是为了更新下载记录
                        int existDownloadLength = readDownloadInfo(threadid);//原来下载的数据量

                        //计算最新的下载
                        int newDownloadLength = existDownloadLength + len;

                        //更新下载记录 从新记录最新下载位置
                        updateDownloadInfo(threadid, newDownloadLength);
                        //更新进度条的显示   下载的百分比
                        Message update_msg = Message.obtain(mHandler, UPDATE_VIEW, len, 0);
                        update_msg.sendToTarget();
                        //模拟  看到进度条动的效果
                        SystemClock.sleep(50);
                    }
                    inputStream.close();
                    raf.close();
                    Log.i(TAG, "第" + threadid + "条线程下载完成");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 读取指定线程的下载数据量
     *
     * @param threadid 线程的id
     * @return
     * @throws Exception
     */
    public int readDownloadInfo(int threadid) throws Exception {
        //下载记录文件
        File file = new File(Environment.getExternalStorageDirectory(), threadid + ".txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        //读取一行数据
        String content = br.readLine();

        int downlength = 0;
        //如果该文件第一次创建去执行读取操作  文件里面的内容是 null
        if (!TextUtils.isEmpty(content)) {
            downlength = Integer.parseInt(content);
        }
        //关闭流
        br.close();
        return downlength;
    }


    /**
     * 更新下载记录
     *
     * @param threadid
     * @param newDownloadLength
     */
    public void updateDownloadInfo(int threadid, int newDownloadLength) throws Exception {
        //下载记录文件
        File file = new File(Environment.getExternalStorageDirectory(), threadid + ".txt");
        FileWriter fw = new FileWriter(file);
        fw.write(newDownloadLength + "");
        fw.close();
    }

    /**
     * 获取文件的名称
     *
     * @param uri
     * @return
     */
    private String getFileName(String uri) {
        return uri.substring(uri.lastIndexOf("/") + 1);
    }


}

package com.cpd.yuqing.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.cpd.yuqing.CpdnewsApplication;
import com.cpd.yuqing.R;
import com.cpd.yuqing.databinding.FragmentNewsContentBinding;
import com.cpd.yuqing.db.dao.NewsDao;
import com.cpd.yuqing.db.vo.News;
import com.cpd.yuqing.view.FontSizeView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by s21v on 2017/12/15.
 */

public class NewsContentFragment extends Fragment implements FontSizeView.SliderPositionChangeListener{
    private News news;
    private PopupWindow mPopupWindow;
    private WebView contentWebView;
    private NewsDao dao;
    private boolean isFavorite;
    private boolean isThumbUp;

//    private static final String TAG = NewsContentFragment.class.getSimpleName();

    public static NewsContentFragment getInstance(News news) {
        NewsContentFragment newsContentFragment = new NewsContentFragment();
        Bundle args = new Bundle();
        args.putParcelable("news", news);
        newsContentFragment.setArguments(args);
        return newsContentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            news = savedInstanceState.getParcelable("news");
        else
            news = getArguments().getParcelable("news");
        //设置选项菜单
        setHasOptionsMenu(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
            DataBinding在Activity中使用：DataBindingUtil.setContentView(...)
            DataBinding在Fragment中使用：DataBindingUtil.inflate(...)
        */
        FragmentNewsContentBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_news_content, container, false);
        dataBinding.setNews(news);
        View rootView = dataBinding.getRoot();
        contentWebView = rootView.findViewById(R.id.contentWebView);
        //不显示滚动条
        contentWebView.setVerticalScrollBarEnabled(false);
        //设置透明背景
        contentWebView.setBackgroundColor(0);
        //设置webView,支持JavaScript
        WebSettings webSettings = contentWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        //有图片时,对文本进行缩放 效果不如设置css好 放弃
//        if (!news.getPicUrls().isEmpty()) {
//            //设置可以支持缩放
//            webSettings.setSupportZoom(true);
//            //扩大比例的缩放
//            webSettings.setUseWideViewPort(true);
//            //自适应屏幕
//            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
//            webSettings.setLoadWithOverviewMode(true);
//        }
        //获得baseUrl
        String baseUrl = "http://www.cpd.com.cn";
        Pattern baseUrlPattern = Pattern.compile("(http://.*?)/.*");
        Matcher matcher = baseUrlPattern.matcher(news.getUrl());
        if (matcher.find())
            baseUrl = matcher.group(1);
        //添加本地css文件
        String css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/content.css\">";
        //去除多余的换行<br>或<br />
        String removeBr = news.getContent().replaceFirst("<td>(?:<br>)+\\s?(?:<br />)*", "<td>");
        String removeBrAgain = removeBr.replaceFirst("(?:<br />)*\\s*(?:<br>)+</td>", "</td>");
        news.setContent(css+removeBrAgain);
        //加载文章内容
        contentWebView.loadDataWithBaseURL(baseUrl, news.getContent(), "text/html", "utf-8", null);
        //加载本地js文件
        contentWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                try (
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                getContext().getAssets().open("content.js"),
                                                Charset.forName("utf-8")))
                ){
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null)
                        sb.append(line);
                    view.loadUrl("javascript:"+sb.toString());
                    //加载用户指定的字体大小
                    int[] fontSizeValues = getResources().getIntArray(R.array.FontSizeValue);
                    int defaultFontSizeValue = fontSizeValues[fontSizeValues.length/2];
                    SharedPreferences contentSettings = getContext().getSharedPreferences("contentSetting", MODE_PRIVATE);
                    int fontSizeValue = contentSettings.getInt("currentFontSizeValue", defaultFontSizeValue);
                    boolean isNightMode = contentSettings.getBoolean("isNightMode", false);
                    if (fontSizeValue != defaultFontSizeValue)
                        view.loadUrl(String.format("javascript:textSizeChange(%d)", fontSizeValue));
                    if (isNightMode)
                        view.loadUrl(String.format("javascript:switchNightMode(%b)", isNightMode));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dao = new NewsDao(getContext());
        //更新新闻数据
        int userId = CpdnewsApplication.getCurrentUser().getId();
        Cursor cursor = dao.selectOne(userId, news.getNews_id());
        if (cursor.moveToFirst()){
            isFavorite = cursor.getInt(cursor.getColumnIndex("favorite")) == 1;
            isThumbUp = cursor.getInt(cursor.getColumnIndex("thumbUp")) == 1;
        } else {
            isFavorite = false;
            isThumbUp = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        dao.openDB(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dao.isDBOpen())
            dao.closeDB();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (news != null) {
            outState.putParcelable("news", news);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.content_opt_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.openMenu) {
            showPopupWindow();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPopupWindow() {
        @SuppressLint("InflateParams")
        View root = LayoutInflater.from(getContext()).inflate(R.layout.news_content_popupmenu, null);
        root.setOnClickListener(it -> mPopupWindow.dismiss());
        FontSizeView fontSizeView = root.findViewById(R.id.fontSizeView);
        fontSizeView.setCallBack(this);
        //取消键
        Button dismissBtn = root.findViewById(R.id.dismiss);
        dismissBtn.setOnClickListener(it -> mPopupWindow.dismiss());
        //收藏按钮
        ImageButton favoriteBtn = root.findViewById(R.id.favoriteBtn);
        if (isFavorite)
            favoriteBtn.setImageResource(R.drawable.favorite_selected);
        favoriteBtn.setOnClickListener(it -> {
            //更新数据库
            int userId = CpdnewsApplication.getCurrentUser().getId();
            int result = dao.operation(userId, news, NewsDao.Companion.getTYPE_FAVORITE(), isFavorite?0:1);
            if (result == 1){   //数据库写入成功
                //更新数据
                isFavorite = !isFavorite;
                //更新图片
                if (isFavorite)
                    ((ImageView)it).setImageResource(R.drawable.favorite_selected);
                else
                    ((ImageView)it).setImageResource(R.drawable.favorite_unselected);
            }
        });
        //点赞按钮
        ImageButton thumbUpBtn = root.findViewById(R.id.thumbUpBtn);
        if (isThumbUp)
            thumbUpBtn.setImageResource(R.drawable.thumbup_selected);
        thumbUpBtn.setOnClickListener(it -> {
            //更新数据库
            int userId = CpdnewsApplication.getCurrentUser().getId();
            int result = dao.operation(userId, news, NewsDao.Companion.getTYPE_THUMBUP(), isThumbUp?0:1);
            if (result == 1){   //数据库写入成功
                //更新数据
                isThumbUp = !isThumbUp;
                //更新图片
                if (isThumbUp)
                    ((ImageView)it).setImageResource(R.drawable.thumbup_selected);
                else
                    ((ImageView)it).setImageResource(R.drawable.thumbup_unselected);
            }
        });
        mPopupWindow = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        //设置背景图后setOutsideTouchable()才有效
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击popupwindow以外的区域是否可以点击
        mPopupWindow.setOutsideTouchable(true);
//        //加载动画
//        mPopupWindow.setAnimationStyle(R.style.newsContentMenuAnim);
    }

    private void showPopupWindow() {
        if (mPopupWindow == null)
            initPopupWindow();
        mPopupWindow.showAtLocation(getView(), Gravity.BOTTOM, 0 , 0);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onSliderPositionChange(int currentSliderValue) {
        contentWebView.loadUrl(String.format("javascript:textSizeChange(%d)", currentSliderValue));
    }
}

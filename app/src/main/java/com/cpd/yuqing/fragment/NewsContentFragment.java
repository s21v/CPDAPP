package com.cpd.yuqing.fragment;

import android.annotation.SuppressLint;
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
import android.widget.PopupWindow;
import com.cpd.yuqing.R;
import com.cpd.yuqing.databinding.FragmentNewsContentBinding;
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
        //设置webView,支持JavaScript
        WebSettings webSettings = contentWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //有图片时,对文本进行缩放
        if (!news.getPicUrls().isEmpty()) {
            //设置可以支持缩放
            webSettings.setSupportZoom(true);
            //扩大比例的缩放
            webSettings.setUseWideViewPort(true);
            //自适应屏幕
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            webSettings.setLoadWithOverviewMode(true);
        }
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //加载用户指定的字体大小
        int[] fontSizeValues = getResources().getIntArray(R.array.FontSizeValue);
        int defaultFontSizeValue = fontSizeValues[fontSizeValues.length/2];
        int fontSizeValue = getContext().getSharedPreferences("contentSetting", MODE_PRIVATE)
                .getInt("currentFontSizeValue", defaultFontSizeValue);
        Log.i("NewsContentFragment", "fontSizeValue:"+fontSizeValue+", middle:"+fontSizeValues[fontSizeValues.length/2]);
        if (fontSizeValue != defaultFontSizeValue)
            contentWebView.loadUrl(String.format("javascript:textSizeChange(%d)", fontSizeValue));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (news != null)
        outState.putParcelable("news", news);
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
        FontSizeView fontSizeView = root.findViewById(R.id.fontSizeView);
        fontSizeView.setCallBack(this);
        Button dismissBtn = root.findViewById(R.id.dismiss);
        dismissBtn.setOnClickListener(it -> mPopupWindow.dismiss());
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

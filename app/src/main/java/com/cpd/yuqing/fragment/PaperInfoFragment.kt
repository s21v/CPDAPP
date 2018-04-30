package com.cpd.yuqing.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.cpd.yuqing.R
import kotlinx.android.synthetic.main.fragment_paper_info.*

/**
 * 数字报版面信息
 * Created by s21v on 2018/4/30.
 */
class PaperInfoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_paper_info, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val paperInfo = arrayListOf<String>()
        paperInfo.add("1版：要闻")
        paperInfo.add("2版：时政")
        paperInfo.add("3版：视点")
        paperInfo.add("4版：“五一”国际劳动节特别报道")
        paperInfo.add("5版：山东周刊")
        paperInfo.add("6版：剑兰周刊")
        paperInfo.add("7版：文化园地")
        paperInfo.add("8版：副刊")
        paperInfoList.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, paperInfo)
        val articleList = arrayListOf<String>()
        articleList.add("第1条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第2条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第3条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第4条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第5条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第6条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第7条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第8条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第9条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第10条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第11条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第12条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第13条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第14条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第15条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第16条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第17条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第18条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第19条新闻：XXXXXXXXXXXXXXXX")
        articleList.add("第20条新闻：XXXXXXXXXXXXXXXX")
        paperArticleList.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, articleList)
    }
}
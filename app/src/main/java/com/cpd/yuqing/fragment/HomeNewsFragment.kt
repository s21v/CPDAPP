package com.cpd.yuqing.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import com.cpd.yuqing.R
import com.cpd.yuqing.activity.CustomChannelActivity
import com.cpd.yuqing.activity.SearchActivity
import com.cpd.yuqing.adapter.NewsViewPagerAdapter
import com.cpd.yuqing.db.dao.ChannelDao
import com.cpd.yuqing.db.vo.Channel
import com.cpd.yuqing.util.NetUtils
import com.cpd.yuqing.util.OkHttpUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home_news.*
import okhttp3.*
import java.io.IOException

/**
 * 新闻栏目导航fragment
 * Created by s21v on 2018/1/26.
 */
class HomeNewsFragment : Fragment() {
    private val requestChangeChannel = 10

    //栏目列表
    private var channelList: ArrayList<Channel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_home_news, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        tabLayout.setupWithViewPager(viewPage, true)
        //检查本地的栏目是否存在（第一次使用app（本地栏目版本为0）需从服务器上下载栏目列表）
        val channelSharedPreferences = context.getSharedPreferences("channelInfo", Context.MODE_PRIVATE)
        val localChannelListVersion = channelSharedPreferences.getInt("localChannelListVersion", 0)
        //本地没有栏目信息，从网络上加载（第一次使用时）
        if (localChannelListVersion == 0) {
            //查询线上栏目版本
            val formBody = FormBody.Builder().add("m", "channelInfo").build()
            val request = Request.Builder().url(NetUtils.ChannelCommonUrl).post(formBody).build()
            OkHttpUtils.getOkHttpUtilInstance(activity)!!.httpConnection(request, object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    TODO("not implemented 栏目版本获取失败" + e?.message)
                }

                override fun onResponse(call: Call?, response: Response?) {
                    val status = response?.header("status")
                    if ("success" == status) {
                        val channelVersion = response.header("channelVersion")
                        channelList = Gson().fromJson(response.body()!!.string(), object : TypeToken<ArrayList<Channel>>() {}.type)
                        var size = channelList!!.size
                        channelList!!.forEach { it.sortNum = size-- }
                        //将信息写入本地系统
                        channelSharedPreferences.edit()
                                .putInt("localChannelListVersion", Integer.valueOf(channelVersion))
                                .apply()
                        ChannelDao.getInstance(activity).insertChannelList(channelList!!)
                        ChannelDao.getInstance(activity).closeDB()
                        activity.runOnUiThread {
                            viewPage.adapter = NewsViewPagerAdapter(childFragmentManager, channelList!!)
                        }
                    } else {
                        TODO("栏目版本获取失败 version fail")
                    }
                }
            })
        } else {
            val needUpdate = channelSharedPreferences.getBoolean("needUpdate", false)
            if (needUpdate) {
                //从服务器端更新栏目信息
                val fromBody = FormBody.Builder().add("m", "queryAllChannel").build()
                val request = Request.Builder().url(NetUtils.ChannelCommonUrl).post(fromBody).build()
                OkHttpUtils.getOkHttpUtilInstance(activity)!!.httpConnection(request, object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        val status = response!!.header("status")
                        if ("success" == status) {
                            val newChannelList: ArrayList<Channel> = Gson().fromJson(response.body()!!.string(), object : TypeToken<ArrayList<Channel>>() {}.type)
                            val oldChannelList = ChannelDao.getInstance(activity).queryAll()
                            //遍历新旧两个列表中较小的
                            if (newChannelList.size < oldChannelList!!.size) {    //新列表小
                                newChannelList.forEach {
                                    //新列表在旧列表中存在的对数据库不做操作，不在旧列表中的新栏目添加到数据库中
                                    if (oldChannelList.contains(it))
                                        oldChannelList.remove(it)
                                    else    //将新栏目添加到数据库中
                                        ChannelDao.getInstance(activity).insert(it)
                                }
                                if (oldChannelList.isNotEmpty())   //将旧列表中原来多余的栏目从数据库中删除
                                    oldChannelList.forEach { ChannelDao.getInstance(activity).delete(it) }
                            } else {    //旧列表小
                                oldChannelList.forEach {
                                    //旧列表在新列表中存在的不对数据进行操作，不在新列表中的栏目从数据中删除
                                    if (newChannelList.contains(it))
                                        newChannelList.remove(it)
                                    else
                                        ChannelDao.getInstance(activity).delete(it)
                                }
                                if (newChannelList.isNotEmpty())
                                    newChannelList.forEach { ChannelDao.getInstance(activity).insert(it) }
                            }
                            channelList = ChannelDao.getInstance(activity).queryByIsShow(true)
                            var size = channelList!!.size
                            channelList!!.forEach { it.sortNum = size-- }
                            ChannelDao.getInstance(activity).updateChannel(channelList!!)
                            ChannelDao.getInstance(activity).closeDB()
                        }
                    }
                })
            } else {
                //从本地数据库中记载栏目信息
                channelList = ChannelDao.getInstance(activity).queryByIsShow(true)
                viewPage.adapter = NewsViewPagerAdapter(childFragmentManager, channelList!!)
                ChannelDao.getInstance(activity).closeDB()
            }
        }

        //设置自定义栏目按钮监听器
        customChannel.setOnClickListener {
            val intent = Intent(context, CustomChannelActivity::class.java)
            intent.putExtra("channelList", channelList)
            startActivityForResult(intent, requestChangeChannel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.news_opt_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search -> {
                startActivity(Intent(activity, SearchActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestChangeChannel) {
            if (resultCode == Activity.RESULT_OK) {
                //返回自定义栏目列表
                channelList = data?.getParcelableArrayListExtra("changedChannel")
                viewPage.adapter = NewsViewPagerAdapter(childFragmentManager, channelList!!)
            }
        }
    }

    fun scrollToFirstPosition() {
        val curFragment = viewPage.adapter.instantiateItem(viewPage, viewPage.currentItem) as NewsListFragment
        curFragment.scrollToFirstPosition()
    }
}
package com.cpd.yuqing.adapter

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.activity.VideoContentActivity
import com.cpd.yuqing.activity.VideoListActivity
import com.cpd.yuqing.databinding.*
import com.cpd.yuqing.db.vo.video.Channel
import com.cpd.yuqing.db.vo.video.News
import com.cpd.yuqing.retrofitInterface.IVideoChannelApi
import com.cpd.yuqing.retrofitInterface.IVideoNewsApi
import com.cpd.yuqing.util.RetrofitUtils
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import kotlin.properties.Delegates
import kotlinx.android.synthetic.main.video_channel_part_level1_5.view.*
import kotlinx.android.synthetic.main.video_part_header.*

/**
 * 视频栏目首页的RecyclerViewAdapter
 * Created by s21v on 2018/3/8.
 */
class VideoHomeRecyclerViewAdapter(val context: Context, channels: ArrayList<Channel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val videoNewsApi: IVideoNewsApi = RetrofitUtils.getInstance(context)!!.retrofitInstance
            .create(IVideoNewsApi::class.java)
    private var typeChannelMap: HashMap<Int, ArrayList<Channel>> by Delegates.notNull()

    init {
        // 按类型重整栏目
        typeChannelMap = hashMapOf()
        // 一级栏目
        val level1Channels = arrayListOf<Channel>()
        // 二级栏目
        val level2Channels = arrayListOf<Channel>()
        // 微视频
        val weiShiPinChannels = arrayListOf<Channel>()
        // 警务新闻
        val jingWuChannels = arrayListOf<Channel>()
        for (i in 0 until channels.size) {
            if (channels[i].id == 20) {
                jingWuChannels.add(channels[i])
                continue
            }
            if (channels[i].id == 28) {
                weiShiPinChannels.add(channels[i])
                continue
            }
            if (channels[i].parentChannelId != 0) {
                level2Channels.add(channels[i])
                continue
            } else {
                level1Channels.add(channels[i])
                continue
            }
        }
        typeChannelMap[LEVEL_1_5_TYPE] = jingWuChannels
        typeChannelMap[LEVEL_1_1_TYPE] = level1Channels
        typeChannelMap[LEVEL_2_TYPE] = level2Channels
        typeChannelMap[LEVEL_1_2_TYPE] = weiShiPinChannels
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val layoutInflater = LayoutInflater.from(context)

        when (viewType) {
            LEVEL_1_5_TYPE -> {
                val rootView = layoutInflater.inflate(R.layout.video_channel_part_level1_5, parent, false)
                return Level15ViewHolder(rootView)
            }
            LEVEL_1_3_TYPE -> {
                val dataBinding = DataBindingUtil.inflate<VideoChannelPartLevel13Binding>(layoutInflater,
                        R.layout.video_channel_part_level1_3, parent, false)
                return Level13ViewHolder(dataBinding, typeChannelMap[LEVEL_1_5_TYPE]!![0])
            }
            LEVEL_2_TYPE -> {
                val dataBinding = DataBindingUtil.inflate<VideoChannelPartLevel2Binding>(layoutInflater,
                        R.layout.video_channel_part_level2, parent, false)
                return Level2ViewHolder(dataBinding)
            }
            LEVEL_1_2_TYPE -> {
                val dataBinding = DataBindingUtil.inflate<VideoChannelPartLevel12Binding>(layoutInflater,
                        R.layout.video_channel_part_level1_2, parent, false)
                return Level12ViewHolder(dataBinding, typeChannelMap[LEVEL_1_2_TYPE]!![0])
            }
            LEVEL_1_1_TYPE -> {
                val dataBinding = DataBindingUtil.inflate<VideoChannelPartLevel11Binding>(layoutInflater,
                        R.layout.video_channel_part_level1_1, parent, false)
                return Level1ViewHolder(dataBinding)
            }
            LEVEL_1_4_TYPE -> {
                val dataBinding = DataBindingUtil.inflate<VideoChannelPartLevel14Binding>(layoutInflater,
                        R.layout.video_channel_part_level1_4, parent, false)
                return Level14ViewHolder(dataBinding)
            }
        }
        return null
    }

    // 1.将警务新闻拆分成两部分（播报、非播报）, 2.专题列表, 3. 公安微视频
    override fun getItemCount(): Int = typeChannelMap[LEVEL_1_1_TYPE]!!.size
            .plus(typeChannelMap[LEVEL_2_TYPE]!!.size) + 4

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is Level13ViewHolder -> //警务新闻
                videoNewsApi.getLastCpdNews(3)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Subscriber<ArrayList<News>>() {
                            lateinit var news: ArrayList<News>
                            override fun onNext(t: ArrayList<News>?) {
                                Log.i(TAG, "onNext()")
                                news = t!!
                            }

                            override fun onCompleted() {
                                Log.i(TAG, "onCompleted()")
                                val viewDataBinding = holder.viewDataBinding
                                viewDataBinding.setVariable(BR.channel, holder.channel)
                                viewDataBinding.setVariable(BR.news, news)
                                viewDataBinding.executePendingBindings()
                                Glide.with(context.applicationContext)
                                        .load(news[0].thumbIconUrl)
                                        .into(viewDataBinding.thumbIcon1)
                                Glide.with(context.applicationContext)
                                        .load(news[1].thumbIconUrl)
                                        .into(viewDataBinding.thumbIcon2)
                                Glide.with(context.applicationContext)
                                        .load(news[2].thumbIconUrl)
                                        .into(viewDataBinding.thumbIcon3)
                                val gridLayout = viewDataBinding.root.findViewById<GridLayout>(R.id.GridLayout)
                                for (i in 0 until gridLayout.childCount) {
                                    gridLayout.getChildAt(i).setOnClickListener {
                                        val intent = Intent(context, VideoContentActivity::class.java)
                                        intent.putExtra("news", news[i])
                                        context.startActivity(intent)
                                    }
                                }
                                // 更多按钮
                                val moreTv = viewDataBinding.root.findViewById<TextView>(R.id.more)
                                moreTv.setOnClickListener {
                                    val intent = Intent(context, VideoListActivity::class.java)
                                    intent.putExtra("channel", holder.channel)
                                    context.startActivity(intent)
                                }
                            }

                            override fun onError(e: Throwable?) {
                                Log.i(TAG, e?.message)
                            }
                        })
            is Level15ViewHolder -> //轮播
                videoNewsApi.getLastNotCpdNews(7)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Subscriber<ArrayList<News>>() {
                            lateinit var news: ArrayList<News>
                            val imageViews: ArrayList<ImageView> = arrayListOf()
                            override fun onNext(t: ArrayList<News>?) {
                                Log.i(TAG, "onNext()")
                                news = t!!
                                for (i in news) {
                                    val imageView = ImageView(context)
                                    imageView.scaleType = ImageView.ScaleType.FIT_XY
                                    Glide.with(context).load(i.thumbIconUrl).into(imageView)
                                    imageViews.add(imageView)
                                    imageView.setOnClickListener {
                                        val intent = Intent(context, VideoContentActivity::class.java)
                                        intent.putExtra("news", i)
                                        context.startActivity(intent)
                                    }
                                }
                                //设置viewpager首尾重复项,达到循环的效果
                                val firstImageView = ImageView(context)
                                firstImageView.scaleType = ImageView.ScaleType.FIT_XY
                                Glide.with(context).load(news.last().thumbIconUrl).into(firstImageView)
                                imageViews.add(0, firstImageView)
                                val endImageView = ImageView(context)
                                endImageView.scaleType = ImageView.ScaleType.FIT_XY
                                Glide.with(context).load(news[0].thumbIconUrl).into(endImageView)
                                imageViews.add(endImageView)
                            }

                            override fun onCompleted() {
                                val mViewPager = holder.view.id_viewpager
                                mViewPager.pageMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                        4f, context.resources.displayMetrics).toInt()
                                mViewPager.offscreenPageLimit = imageViews.size
                                mViewPager.adapter = object : PagerAdapter() {
                                    override fun instantiateItem(container: ViewGroup, position: Int): Any {
                                        container.addView(imageViews[position])
                                        return imageViews[position]
                                    }

                                    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any?) {
                                        container.removeView(imageViews[position])
                                    }

                                    override fun isViewFromObject(view: View, `object`: Any): Boolean {
                                        return view === `object`
                                    }

                                    override fun getCount(): Int {
                                        return imageViews.size
                                    }
                                }
                                mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                                    var currentPosition = mViewPager.currentItem
                                    override fun onPageScrollStateChanged(state: Int) {
                                        if (ViewPager.SCROLL_STATE_IDLE != state)
                                            return
                                        if (currentPosition == 0)
                                            mViewPager.setCurrentItem(imageViews.size - 2, false)
                                        else if (currentPosition == imageViews.size - 1)
                                            mViewPager.setCurrentItem(1, false)
                                    }

                                    /**
                                     * 在非第一页与最后一页时:
                                     * 滑动到下一页，position为当前页位置；滑动到上一页：position为当前页-1;
                                     * positionOffset 滑动到下一页，[0,1)区间上变化；滑动到上一页：(1,0]区间上变化;
                                     * positionOffsetPixels这个和positionOffset很像：滑动到下一页，[0,宽度)区间上变化；滑动到上一页：(宽度,0]区间上变化
                                     *
                                     * 第一页时：滑动到上一页position=0 ，其他基本为0
                                     *
                                     * 最后一页时：滑动到下一页position为当前页位置，其他两个参数为0
                                     */
                                    /**
                                     * 在非第一页与最后一页时:
                                     * 滑动到下一页，position为当前页位置；滑动到上一页：position为当前页-1;
                                     * positionOffset 滑动到下一页，[0,1)区间上变化；滑动到上一页：(1,0]区间上变化;
                                     * positionOffsetPixels这个和positionOffset很像：滑动到下一页，[0,宽度)区间上变化；滑动到上一页：(宽度,0]区间上变化
                                     *
                                     * 第一页时：滑动到上一页position=0 ，其他基本为0
                                     *
                                     * 最后一页时：滑动到下一页position为当前页位置，其他两个参数为0
                                     */
                                    /**
                                     * 在非第一页与最后一页时:
                                     * 滑动到下一页，position为当前页位置；滑动到上一页：position为当前页-1;
                                     * positionOffset 滑动到下一页，[0,1)区间上变化；滑动到上一页：(1,0]区间上变化;
                                     * positionOffsetPixels这个和positionOffset很像：滑动到下一页，[0,宽度)区间上变化；滑动到上一页：(宽度,0]区间上变化
                                     *
                                     * 第一页时：滑动到上一页position=0 ，其他基本为0
                                     *
                                     * 最后一页时：滑动到下一页position为当前页位置，其他两个参数为0
                                     */
                                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                                    override fun onPageSelected(position: Int) {
                                        currentPosition = position
                                        when (currentPosition) {
                                            0 -> holder.view.title_tv.text = news[news.size - 1].title
                                            imageViews.size - 1 -> holder.view.title_tv.text = news[0].title
                                            else -> holder.view.title_tv.text = news[position - 1].title
                                        }
                                    }
                                })
                                mViewPager.currentItem = imageViews.size / 2
                            }

                            override fun onError(e: Throwable?) {
                                Log.i(TAG, e?.message)
                            }
                        })
            // 一级栏目
            is Level1ViewHolder -> {
                val curChannel = typeChannelMap[LEVEL_1_1_TYPE]!![position - 2]
                videoNewsApi.getLastVideoNews(curChannel.id, 4)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Subscriber<ArrayList<News>>() {
                            lateinit var news: ArrayList<News>
                            override fun onNext(t: ArrayList<News>?) {
                                Log.i(TAG, "onNext()")
                                news = t!!
                            }

                            override fun onCompleted() {
                                Log.i(TAG, "onCompleted()")
                                val viewDataBinding = holder.viewDataBinding
                                viewDataBinding.setVariable(BR.channel, curChannel)
                                viewDataBinding.setVariable(BR.news, news)
                                viewDataBinding.executePendingBindings()
                                Glide.with(context.applicationContext)
                                        .load(news[0].thumbIconUrl)
                                        .into(viewDataBinding.newsImg1)
                                Glide.with(context.applicationContext)
                                        .load(news[1].thumbIconUrl)
                                        .into(viewDataBinding.newsImg2)
                                Glide.with(context.applicationContext)
                                        .load(news[2].thumbIconUrl)
                                        .into(viewDataBinding.newsImg3)
                                Glide.with(context.applicationContext)
                                        .load(news[3].thumbIconUrl)
                                        .into(viewDataBinding.newsImg4)
                                val gridLayout = viewDataBinding.root.findViewById<GridLayout>(R.id.GridLayout)
                                for (i in 0 until gridLayout.childCount) {
                                    gridLayout.getChildAt(i).setOnClickListener {
                                        val intent = Intent(context, VideoContentActivity::class.java)
                                        intent.putExtra("news", news[i])
                                        context.startActivity(intent)
                                    }
                                }
                            }

                            override fun onError(e: Throwable?) {
                                Log.i(TAG, e?.message)
                            }
                        })
            }
            // 最新专题
            is Level14ViewHolder -> {
                val videoChannelApi = RetrofitUtils.getInstance(context)!!.retrofitInstance
                        .create(IVideoChannelApi::class.java)
                videoChannelApi.lastSubject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Subscriber<Channel>() {
                            lateinit var result: Channel
                            override fun onNext(t: Channel?) {
                                result = t!!
                            }

                            override fun onCompleted() {
                                val viewDataBinding = holder.viewDataBinding
                                viewDataBinding.setVariable(BR.channel, result)
                                viewDataBinding.executePendingBindings()
                                Glide.with(context).load(result.subject_img_url)
                                        .into(viewDataBinding.subjectImg)
                            }

                            override fun onError(e: Throwable?) {
                                Log.i(TAG, e?.message)
                            }
                        })
            }
            // 二级栏目
            is Level2ViewHolder -> {
                val curChannel = typeChannelMap[LEVEL_2_TYPE]!![position - 3 - typeChannelMap[LEVEL_1_1_TYPE]!!.size]
                videoNewsApi.getLastVideoNews(curChannel.id, 5)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Subscriber<ArrayList<News>>() {
                            lateinit var news: ArrayList<News>
                            override fun onNext(t: ArrayList<News>?) {
                                Log.i(TAG, "onNext()")
                                news = t!!
                            }

                            override fun onCompleted() {
                                Log.i(TAG, "onCompleted()")
                                val viewDataBinding = holder.viewDataBinding
                                viewDataBinding.setVariable(BR.channel, curChannel)
                                viewDataBinding.setVariable(BR.news, news)
                                viewDataBinding.executePendingBindings()
                                Glide.with(context.applicationContext)
                                        .load(news[0].thumbIconUrl)
                                        .into(viewDataBinding.newsImg1)
                                Glide.with(context.applicationContext)
                                        .load(news[1].thumbIconUrl)
                                        .into(viewDataBinding.newsImg2)
                                Glide.with(context.applicationContext)
                                        .load(news[2].thumbIconUrl)
                                        .into(viewDataBinding.newsImg3)
                                Glide.with(context.applicationContext)
                                        .load(news[3].thumbIconUrl)
                                        .into(viewDataBinding.newsImg4)
                                Glide.with(context.applicationContext)
                                        .load(news[4].thumbIconUrl)
                                        .into(viewDataBinding.newsImg5)
                                val gridLayout = viewDataBinding.root.findViewById<GridLayout>(R.id.GridLayout)
                                for (i in 0 until gridLayout.childCount) {
                                    gridLayout.getChildAt(i).setOnClickListener {
                                        val intent = Intent(context, VideoContentActivity::class.java)
                                        intent.putExtra("news", news[i])
                                        context.startActivity(intent)
                                    }
                                }
                            }

                            override fun onError(e: Throwable?) {
                                Log.i(TAG, e?.message)
                            }
                        })
            }
            // 微视频
            is Level12ViewHolder -> {
                val curChannel = typeChannelMap[LEVEL_1_2_TYPE]!![0]
                videoNewsApi.getLastVideoNews(curChannel.id, 6)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Subscriber<ArrayList<News>>() {
                            lateinit var news: ArrayList<News>
                            override fun onNext(t: ArrayList<News>?) {
                                Log.i(TAG, "onNext()")
                                news = t!!
                            }

                            override fun onCompleted() {
                                Log.i(TAG, "onCompleted()")
                                val viewDataBinding = holder.viewDataBinding
                                viewDataBinding.setVariable(BR.channel, curChannel)
                                viewDataBinding.setVariable(BR.news, news)
                                viewDataBinding.executePendingBindings()
                                Glide.with(context.applicationContext)
                                        .load(news[0].thumbIconUrl)
                                        .into(viewDataBinding.newsImg1)
                                Glide.with(context.applicationContext)
                                        .load(news[1].thumbIconUrl)
                                        .into(viewDataBinding.newsImg2)
                                Glide.with(context.applicationContext)
                                        .load(news[2].thumbIconUrl)
                                        .into(viewDataBinding.newsImg3)
                                Glide.with(context.applicationContext)
                                        .load(news[3].thumbIconUrl)
                                        .into(viewDataBinding.newsImg4)
                                Glide.with(context.applicationContext)
                                        .load(news[4].thumbIconUrl)
                                        .into(viewDataBinding.newsImg5)
                                Glide.with(context.applicationContext)
                                        .load(news[5].thumbIconUrl)
                                        .into(viewDataBinding.newsImg6)
                                val gridLayout = viewDataBinding.root.findViewById<GridLayout>(R.id.GridLayout)
                                for (i in 0 until gridLayout.childCount) {
                                    gridLayout.getChildAt(i).setOnClickListener {
                                        val intent = Intent(context, VideoContentActivity::class.java)
                                        intent.putExtra("news", news[i])
                                        context.startActivity(intent)
                                    }
                                }
                            }

                            override fun onError(e: Throwable?) {
                                Log.i(TAG, e?.message)
                            }
                        })
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> LEVEL_1_5_TYPE   //非播报类警务新闻
            1 -> LEVEL_1_3_TYPE   //警务新闻播报类
            in 2 until 2 + typeChannelMap[LEVEL_1_1_TYPE]!!.size -> LEVEL_1_1_TYPE   //一级栏目
            2 + typeChannelMap[LEVEL_1_1_TYPE]!!.size -> LEVEL_1_4_TYPE    // 专题
            in 3 + typeChannelMap[LEVEL_1_1_TYPE]!!.size until
                    3 + typeChannelMap[LEVEL_1_1_TYPE]!!.size + typeChannelMap[LEVEL_2_TYPE]!!.size
            -> LEVEL_2_TYPE     //二级栏目
            3 + typeChannelMap[LEVEL_1_1_TYPE]!!.size + typeChannelMap[LEVEL_2_TYPE]!!.size
            -> LEVEL_1_2_TYPE   //微电影
            else -> -1
        }
    }

    class Level13ViewHolder(var viewDataBinding: VideoChannelPartLevel13Binding, var channel: Channel) :
            RecyclerView.ViewHolder(viewDataBinding.root)

    class Level1ViewHolder(var viewDataBinding: VideoChannelPartLevel11Binding) :
            RecyclerView.ViewHolder(viewDataBinding.root)

    class Level12ViewHolder(var viewDataBinding: VideoChannelPartLevel12Binding, var channel: Channel) :
            RecyclerView.ViewHolder(viewDataBinding.root)

    class Level2ViewHolder(var viewDataBinding: VideoChannelPartLevel2Binding) :
            RecyclerView.ViewHolder(viewDataBinding.root)

    class Level14ViewHolder(var viewDataBinding: VideoChannelPartLevel14Binding) :
            RecyclerView.ViewHolder(viewDataBinding.root)

    class Level15ViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val TAG = "VideoHomeRVAdapter"
        private const val COUNT_PER_PAGE = 20
        private const val LEVEL_1_5_TYPE = 1  //警务新闻中非播报的内容,轮播
        private const val LEVEL_1_3_TYPE = 2    //警务新闻播报
        private const val LEVEL_1_1_TYPE = 3    //一级栏目 如 推荐 资讯
        private const val LEVEL_1_2_TYPE = 4    //公安微电影
        private const val LEVEL_1_4_TYPE = 5    //专题
        private const val LEVEL_2_TYPE = 6  //二级栏目 如 原创 厅局长访谈 警徽荣耀
    }
}
package com.cpd.yuqing.activity

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import com.cpd.yuqing.R
import com.cpd.yuqing.db.dao.ChannelDao
import com.cpd.yuqing.db.vo.Channel
import kotlinx.android.synthetic.main.activity_customchannel.*

/**
 * Created by s21v on 2017/8/15.
 */
class CustomChannelActivity : AppCompatActivity() {
    private var selectedChannelListData: ArrayList<Channel>? = null
    var unselectedChannelListData: ArrayList<Channel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获得数据
        selectedChannelListData = if (savedInstanceState != null) {
            savedInstanceState.getParcelableArrayList("channelList")
        } else {
            intent.getParcelableArrayListExtra("channelList")
        }
        unselectedChannelListData = ChannelDao.getInstance(this).queryByIsShow(false)
        if (unselectedChannelListData == null) {
            unselectedChannelListData = arrayListOf()
        }
        //设置视图
        setContentView(R.layout.activity_customchannel)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //关联数据
        selectedChannelList.layoutManager = GridLayoutManager(this, 4)
        selectedChannelList.adapter = ChannelListAdapter(selectedChannelListData!!, true)
        unSelectedChannelList.layoutManager = GridLayoutManager(this, 4)
        unSelectedChannelList.adapter = ChannelListAdapter(unselectedChannelListData!!, false)
        submit.setOnClickListener {
            exit()
        }
    }

    private fun exit() {
        var size = selectedChannelListData!!.size
        selectedChannelListData!!.forEach { it.sortNum = size-- }
        val channelDao = ChannelDao.getInstance(this)
        channelDao.updateChannel(selectedChannelListData!!)
        selectedChannelListData = channelDao.queryByIsShow(true)
        val intent = Intent(this@CustomChannelActivity, MainActivity::class.java)
        intent.putExtra("changedChannel", selectedChannelListData)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                exit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        exit()
        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        ChannelDao.getInstance(this).closeDB()
    }

    inner class ChannelListAdapter(private val channelListDate: ArrayList<Channel>, private val isSelected: Boolean)
        : RecyclerView.Adapter<ChannelListAdapter.ChannelViewHolder>() {
        override fun getItemCount(): Int = channelListDate.size

        override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
            holder.view.text = channelListDate[position].name
            if (position == 0 && isSelected) {  //头条的位置不允许改变
                holder.view.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
                holder.view.setTextColor(resources.getColor(R.color.sign_in_button_click, null))
            } else {
                holder.view.setOnLongClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        holder.view.startDragAndDrop(ClipData.newPlainText("sourcePosition",
                                selectedChannelList.getChildAdapterPosition(holder.view).toString()),
                                View.DragShadowBuilder(holder.view), null, 0)
                    } else {
                        holder.view.startDrag(ClipData.newPlainText("sourcePosition",
                                selectedChannelList.getChildAdapterPosition(holder.view).toString()),
                                View.DragShadowBuilder(holder.view), null, 0)
                    }
                    true
                }

                holder.view.setOnDragListener { p0, p1 ->
                    when (p1?.action) {
                        DragEvent.ACTION_DROP -> {
                            val sourcePosition = Integer.valueOf(p1.clipData.getItemAt(0).text.toString())
                            val destPosition = selectedChannelList.getChildAdapterPosition(p0)
                            if (destPosition == 0)
                                return@setOnDragListener false
                            if (sourcePosition != destPosition) {
                                val sourceItem = channelListDate[sourcePosition]
                                val destItem = channelListDate[destPosition]
                                val subChannelList: ArrayList<Channel>
                                //调整显示顺序
                                sourceItem.sortNum = destItem.sortNum
                                if (sourcePosition > destPosition) {
                                    subChannelList = ArrayList(channelListDate.subList(destPosition, sourcePosition))
                                    subChannelList.forEach { it.sortNum -= 1 }
                                } else {
                                    subChannelList = ArrayList(channelListDate.subList(sourcePosition + 1, destPosition + 1))
                                    subChannelList.forEach { it.sortNum += 1 }
                                }
                                //在数据结构中对换两项
                                channelListDate.removeAt(sourcePosition)
                                channelListDate.add(destPosition, sourceItem)
                                selectedChannelList.adapter.notifyItemMoved(sourcePosition, destPosition)   //底层数据便不会改变，必须提前对底层数据进行更改
                                //在本地数据库中更新显示数据
                                subChannelList.add(sourceItem)
                                ChannelDao.getInstance(this@CustomChannelActivity).updateChannel(subChannelList)
                            }
                        }
                    }
                    true
                }

                holder.view.setOnClickListener {
                    val changeChannel: Channel
                    if (isSelected) {   //已选栏目的点击操作
                        val selectChannelPosition = selectedChannelList.getChildAdapterPosition(it)
                        changeChannel = selectedChannelListData!![selectChannelPosition]
                        changeChannel.sortNum = -1
                        unselectedChannelListData!!.add(changeChannel)
                        selectedChannelListData!!.removeAt(selectChannelPosition)
                        selectedChannelList!!.adapter.notifyItemRemoved(selectChannelPosition)
                        unSelectedChannelList!!.adapter.notifyItemInserted(unselectedChannelListData!!.size - 1)
                    } else {    //未选栏目的点击操作
                        val unSelectedChannelPosition = unSelectedChannelList.getChildAdapterPosition(it)
                        changeChannel = unselectedChannelListData!![unSelectedChannelPosition]
                        changeChannel.sortNum = 0
                        selectedChannelListData!!.add(changeChannel)
                        unselectedChannelListData!!.removeAt(unSelectedChannelPosition)
                        unSelectedChannelList!!.adapter.notifyItemRemoved(unSelectedChannelPosition)
                        selectedChannelList!!.adapter.notifyItemInserted(selectedChannelListData!!.size - 1)
                    }
                    //更新数据库
                    ChannelDao.getInstance(this@CustomChannelActivity).updateChannel(arrayListOf(changeChannel))
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelViewHolder {
            val view = TextView(this@CustomChannelActivity)
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)
            val marginLayoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            marginLayoutParams.setMargins(margin.toInt(), margin.toInt(), margin.toInt(), margin.toInt())
            view.layoutParams = marginLayoutParams
            view.setLines(1)
            view.gravity = Gravity.CENTER
            view.textSize = 16f
            view.setBackgroundColor(resources.getColor(R.color.sign_in_button_normal, null))
            view.setTextColor(Color.WHITE)
            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)
            view.setPadding(padding.toInt(), padding.toInt(), padding.toInt(), padding.toInt())
            return ChannelViewHolder(view)
        }

        inner class ChannelViewHolder(var view: TextView) : RecyclerView.ViewHolder(view)
    }
}




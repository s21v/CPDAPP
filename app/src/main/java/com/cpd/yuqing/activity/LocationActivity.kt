package com.cpd.yuqing.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_location.*
import java.io.InputStreamReader

/**
 * Created by s21v on 2017/8/3.
 */
class LocationActivity : AppCompatActivity() {
    var cities: List<City>? = null //城市数据
    var currentLevel = 1    //当前层级
    var stepCity1: City? = null //第一级
    var stepCity2: City? = null //第二级
    var stepCity3: City? = null //第三级
    var stepCity4: City? = null //第四级
    var cityListAdapter: CityListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //初始化数据
        val type = object : TypeToken<List<City>>(){}.type
        cities = Gson().fromJson<List<City>>(InputStreamReader(assets.open("address.json")), type)
        //初始化列表
        cityList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        cityListAdapter = CityListAdapter(cities!!)
        cityList.adapter = cityListAdapter
    }

    fun cursorMove(from:Int, to:Int) {
        val transitionAnimator = ObjectAnimator.ofFloat(cursor, "translationX", cursor.translationX, cursor.translationX+cursor.width*(to - from).toFloat())
        transitionAnimator.duration = 500
        transitionAnimator.start()
    }

    //处理tab点击事件
    fun onTabClick(view : View) {
        Log.i("LocationActivity", "onTabClick")
        when(view.id) {
            R.id.stepLevel1 -> {
                //更新数据
                stepCity1 = null
                stepCity2 = null
                stepCity3 = null
                stepCity4 = null
                cityListAdapter!!.data = cities!!
                cityListAdapter!!.notifyDataSetChanged()
                //更新界面
                stepLevel4.visibility = View.INVISIBLE
                stepLevel3.visibility = View.INVISIBLE
                stepLevel2.visibility = View.INVISIBLE
                stepLevel1.text = resources.getString(R.string.selectLocation)
                //动画
                cursorMove(currentLevel, 1)
                currentLevel = 1
            }
            R.id.stepLevel2 -> {
                //更新数据
                stepCity2 = null
                stepCity3 = null
                stepCity4 = null
                cityListAdapter!!.data = stepCity1!!.children
                cityListAdapter!!.notifyDataSetChanged()
                //更新界面
                stepLevel4.visibility = View.INVISIBLE
                stepLevel3.visibility = View.INVISIBLE
                stepLevel2.text = resources.getString(R.string.selectLocation)
                //动画
                cursorMove(currentLevel, 2)
                currentLevel = 2
            }
            R.id.stepLevel3 -> {
                //更新数据
                stepCity3 = null
                stepCity4 = null
                cityListAdapter!!.data = stepCity2!!.children
                cityListAdapter!!.notifyDataSetChanged()
                //更新界面
                stepLevel4.visibility = View.INVISIBLE
                stepLevel3.text = resources.getString(R.string.selectLocation)
                //动画
                cursorMove(currentLevel, 3)
                currentLevel = 3
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home) {
            returnCurrentLocationString("", RESULT_CODE_FAIL)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun returnCurrentLocationString(location: String, resultCode: Int) {
        if(RESULT_CODE_SUCCESS == resultCode) {
            val intent = Intent()
            intent.putExtra("location", location)
            setResult(resultCode, intent)
        } else
            setResult(resultCode)
        finish()
    }

    inner class CityListAdapter(var data: List<City>) : RecyclerView.Adapter<CityListAdapter.CityNameViewHolder>() {
        override fun onBindViewHolder(holder: CityNameViewHolder?, position: Int) {
            (holder?.itemView as TextView).text = data[position].name
            holder.itemView.setOnClickListener {
                when (currentLevel) {
                    1 -> {
                        //设置tab
                        stepLevel1.text = data[position].name
                        if (data[position].hasChildren()) {
                            //光标动画
                            cursorMove(currentLevel, currentLevel+1)
                            //设置下一个tab可见
                            stepLevel2.visibility = View.VISIBLE
                            //更新数据
                            stepCity1 = data[position]
                            currentLevel++
                            data = data[position].children
                            this.notifyDataSetChanged()
                        } else
                            returnCurrentLocationString(stepLevel1.text.toString(), RESULT_CODE_SUCCESS)
                    }
                    2 -> {
                        //设置tab
                        stepLevel2.text = data[position].name
                        if (data[position].hasChildren()) {
                            //光标动画
                            cursorMove(currentLevel, currentLevel+1)
                            //设置下一个tab可见
                            stepLevel3.visibility = View.VISIBLE
                            //更新数据
                            stepCity2 = data[position]
                            currentLevel++
                            data = data[position].children
                            this.notifyDataSetChanged()
                        } else
                            returnCurrentLocationString(stepLevel1.text.toString()+stepLevel2.text, RESULT_CODE_SUCCESS)
                    }
                    3 -> {
                        //设置tab
                        stepLevel3.text = data[position].name
                        if (data[position].hasChildren()) {
                            //光标动画
                            cursorMove(currentLevel, currentLevel+1)
                            //设置下一个tab可见
                            stepLevel4.visibility = View.VISIBLE
                            //更新数据
                            stepCity3 = data[position]
                            currentLevel++
                            data = data[position].children
                            this.notifyDataSetChanged()
                        } else
                            returnCurrentLocationString(stepLevel1.text.toString()+stepLevel2.text+stepLevel3.text, RESULT_CODE_SUCCESS)
                    }
                    4 -> {
                        //设置tab
                        stepLevel4.text = data[position].name
                        //更新数据
                        stepCity4 = data[position]
                        returnCurrentLocationString(stepLevel1.text.toString()+stepLevel2.text+stepLevel3.text+stepLevel4.text, RESULT_CODE_SUCCESS)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CityNameViewHolder {
            val textView = this@LocationActivity.layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
            val marginLayoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            marginLayoutParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    8f, this@LocationActivity.resources.displayMetrics).toInt()
            textView.layoutParams = marginLayoutParams
            return CityNameViewHolder(textView)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        inner class CityNameViewHolder(view: TextView) : RecyclerView.ViewHolder(view)
    }

    companion object {
        val RESULT_CODE_SUCCESS = 1
        val RESULT_CODE_FAIL = -1
    }
}



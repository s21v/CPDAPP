package com.cpd.yuqing.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.Toast
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.cpd.yuqing.R
import com.cpd.yuqing.activity.LocationActivity
import kotlinx.android.synthetic.main.fragment_home_location.*
import kotlinx.android.synthetic.main.fragment_header.*

/**
 * Created by s21v on 2017/8/2.
 */
class HomeLocationFragment : Fragment() {

    private var mLocationClient:LocationClient? = null
    init {
        mLocationClient = LocationClient(activity)
        mLocationClient!!.registerLocationListener(object : BDLocationListener{
            override fun onConnectHotSpotMessage(p0: String?, p1: Int) {
                Log.i(TAG, "onConnectHotSpotMessage() $p0 , $p1")
            }

            override fun onReceiveLocation(p0: BDLocation?) {
                val currentLocation = StringBuilder()
                currentLocation.append("纬度:").append(p0?.latitude).append("\n")
                currentLocation.append("经度:").append(p0?.longitude).append("\n")
                currentLocation.append("定位方式:")
                when (p0?.locType) {
                    BDLocation.TypeGpsLocation -> currentLocation.append("GPS\n")
                    BDLocation.TypeNetWorkLocation -> currentLocation.append("网络\n")
                }
                currentLocation.append("所在省市").append(p0?.province+" , "+p0?.city+"\n")
                position_text_view.text = currentLocation.toString()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        //运行时权限检查
        val requestPermissionList = arrayListOf<String>()
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
            requestPermissionList.add(Manifest.permission.READ_PHONE_STATE)
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            requestPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            requestPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(requestPermissionList.isNotEmpty())
            ActivityCompat.requestPermissions(activity, requestPermissionList.toTypedArray(), PERMISSIONS_REQUEST_CODE)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_location, container, false)
    }

    override fun onStart() {
        super.onStart()
        activity.appbarlayout.setExpanded(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.location_opt_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.location -> {
                //手工选择位置
                startActivityForResult(Intent(context, LocationActivity::class.java), LOCATION_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == LOCATION_REQUEST_CODE)
            if (resultCode == LocationActivity.RESULT_CODE_SUCCESS) {
                position_text_view.text = data?.getStringExtra("location")?:"不能定位当前位置请手动选择位置"
            }
    }

    //运行时权限检查的回调函数
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if(grantResults.isNotEmpty()) {
                    if(grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
                                Toast.makeText(activity, "必须同意所有权限才能运行定位功能", Toast.LENGTH_SHORT).show()
                                (activity as AppCompatActivity).supportFragmentManager.popBackStack()
                            }
                    else
                        mLocationClient!!.start()
                } else {
                    Toast.makeText(activity, "发生未知错误", Toast.LENGTH_SHORT).show()
                    (activity as AppCompatActivity).supportFragmentManager.popBackStack()
                }
            }
        }
    }

    companion object {
        val TAG = "NavLocationFragment"
        private const val PERMISSIONS_REQUEST_CODE = 1
        private const val LOCATION_REQUEST_CODE = 2
    }
}
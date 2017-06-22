package com.cpd.yuqing.data

/**
 * Created by s21v on 2017/6/20.
 */
class Setter {
    var i:Int = 0
    set(value) {
        System.out.print("in Setter:"+value)
        field = value
    }
}
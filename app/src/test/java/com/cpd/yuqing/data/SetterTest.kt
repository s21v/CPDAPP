package com.cpd.yuqing.data

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by s21v on 2017/6/20.
 */
class SetterTest {
    @Test
    fun setI() {
        var s = Setter()
        s.i = 10
        assertEquals(10, s.i)
    }

}
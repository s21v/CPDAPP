package com.cpd.yuqing.activity;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cpd.yuqing.util.SplitRect;

import java.util.ArrayList;
import java.util.LinkedList;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 井字型 图形
//        LinkedList<Point> inputPoints = new LinkedList<>();
//        inputPoints.add(new Point(100, 100));      //1
//        inputPoints.add(new Point(150, 100));    //2
//        inputPoints.add(new Point(150, 50));   //3
//        inputPoints.add(new Point(200, 50));     //4
//        inputPoints.add(new Point(200, 100));   //5
//        inputPoints.add(new Point(250, 100));   //6
//        inputPoints.add(new Point(250, 50));    //7
//        inputPoints.add(new Point(300, 50));    //8
//        inputPoints.add(new Point(300, 100));   //9
//        inputPoints.add(new Point(350, 100));   //10
//        inputPoints.add(new Point(350, 150));   //11
//        inputPoints.add(new Point(300, 150));   //12
//        inputPoints.add(new Point(300, 200));   //13
//        inputPoints.add(new Point(350, 200));   //14
//        inputPoints.add(new Point(350, 250));   //15
//        inputPoints.add(new Point(300, 250));   //16
//        inputPoints.add(new Point(300, 300));   //17
//        inputPoints.add(new Point(250, 300));   //18
//        inputPoints.add(new Point(250, 250));   //19
//        inputPoints.add(new Point(200, 250));   //20
//        inputPoints.add(new Point(200, 300));   //21
//        inputPoints.add(new Point(150, 300));   //22
//        inputPoints.add(new Point(150, 250));   //23
//        inputPoints.add(new Point(100, 250));   //24
//        inputPoints.add(new Point(100, 200));   //25
//        inputPoints.add(new Point(150, 200));   //26
//        inputPoints.add(new Point(150, 150));   //26
//        inputPoints.add(new Point(100, 150));   //26
//        ArrayList<Rect> rects = SplitRect.Split2Rect(inputPoints);
//        for (Rect r: rects)
//            Log.i("# 图形", "Rect:"+r);
//
//        // 普通 图形
//        LinkedList<Point> inputPoints1 = new LinkedList<>();
//        inputPoints1.add(new Point(100, 100));
//        inputPoints1.add(new Point(150, 100));
//        inputPoints1.add(new Point(150, 150));
//        inputPoints1.add(new Point(100, 150));
//        ArrayList<Rect> rects1 = SplitRect.Split2Rect(inputPoints1);
//        for (Rect r: rects1)
//            Log.i("普通 图形", "Rect:"+r);
//        // |一 图形
//        LinkedList<Point> inputPoints2 = new LinkedList<>();
//        inputPoints2.add(new Point(100, 100));
//        inputPoints2.add(new Point(200, 100));
//        inputPoints2.add(new Point(200, 150));
//        inputPoints2.add(new Point(150, 150));
//        inputPoints2.add(new Point(150, 200));
//        inputPoints2.add(new Point(100, 200));
//        ArrayList<Rect> rects2 = SplitRect.Split2Rect(inputPoints2);
//        for (Rect r: rects2)
//            Log.i("|一 图形", "Rect:"+r);
//        // _| 图形
//        LinkedList<Point> inputPoints3 = new LinkedList<>();
//        inputPoints3.add(new Point(100, 100));
//        inputPoints3.add(new Point(150, 100));
//        inputPoints3.add(new Point(150, 50));
//        inputPoints3.add(new Point(200, 50));
//        inputPoints3.add(new Point(200, 150));
//        inputPoints3.add(new Point(100, 150));
//        ArrayList<Rect> rects3 = SplitRect.Split2Rect(inputPoints3);
//        for (Rect r: rects3)
//            Log.i("_| 图形", "Rect:"+r);
//        // |_ 图形
//        LinkedList<Point> inputPoints4 = new LinkedList<>();
//        inputPoints4.add(new Point(100, 100));
//        inputPoints4.add(new Point(150, 100));
//        inputPoints4.add(new Point(150, 150));
//        inputPoints4.add(new Point(200, 150));
//        inputPoints4.add(new Point(200, 200));
//        inputPoints4.add(new Point(100, 200));
//        ArrayList<Rect> rects4 = SplitRect.Split2Rect(inputPoints4);
//        for (Rect r: rects4)
//            Log.i("|_ 图形", "Rect:"+r);
//        // 一|图形
//        LinkedList<Point> inputPoints5 = new LinkedList<>();
//        inputPoints5.add(new Point(100, 100));
//        inputPoints5.add(new Point(200, 100));
//        inputPoints5.add(new Point(200, 200));
//        inputPoints5.add(new Point(150, 200));
//        inputPoints5.add(new Point(150, 150));
//        inputPoints5.add(new Point(100, 150));
//        ArrayList<Rect> rects5 = SplitRect.Split2Rect(inputPoints5);
//        for (Rect r: rects5)
//            Log.i("一|图形", "Rect:"+r);
//        // T 图形
//        LinkedList<Point> inputPoints6 = new LinkedList<>();
//        inputPoints6.add(new Point(100, 100));
//        inputPoints6.add(new Point(250, 100));
//        inputPoints6.add(new Point(250, 150));
//        inputPoints6.add(new Point(200, 150));
//        inputPoints6.add(new Point(200, 200));
//        inputPoints6.add(new Point(150, 200));
//        inputPoints6.add(new Point(150, 150));
//        inputPoints6.add(new Point(100, 150));
//        ArrayList<Rect> rects6 = SplitRect.Split2Rect(inputPoints6);
//        for (Rect r: rects6)
//            Log.i("T 图形", "Rect:"+r);
//        //卜 字型
//        LinkedList<Point> inputPoints7 = new LinkedList<>();
//        inputPoints7.add(new Point(100, 100));
//        inputPoints7.add(new Point(150, 100));
//        inputPoints7.add(new Point(150, 150));
//        inputPoints7.add(new Point(200, 150));
//        inputPoints7.add(new Point(200, 200));
//        inputPoints7.add(new Point(150, 200));
//        inputPoints7.add(new Point(150, 250));
//        inputPoints7.add(new Point(100, 250));
//        ArrayList<Rect> rects7 = SplitRect.Split2Rect(inputPoints7);
//        for (Rect r: rects7)
//            Log.i("卜 字型", "Rect:"+r);
//        //E 型
//        LinkedList<Point> inputPoints8 = new LinkedList<>();
//        inputPoints8.add(new Point(100, 100));
//        inputPoints8.add(new Point(200, 100));
//        inputPoints8.add(new Point(200, 150));
//        inputPoints8.add(new Point(150, 150));
//        inputPoints8.add(new Point(150, 200));
//        inputPoints8.add(new Point(200, 200));
//        inputPoints8.add(new Point(200, 250));
//        inputPoints8.add(new Point(150, 250));
//        inputPoints8.add(new Point(150, 300));
//        inputPoints8.add(new Point(200, 300));
//        inputPoints8.add(new Point(200, 350));
//        inputPoints8.add(new Point(100, 350));
//        ArrayList<Rect> rects8 = SplitRect.Split2Rect(inputPoints8);
//        for (Rect r: rects8)
//            Log.i("E 型", "Rect:"+r);
//        // 十字型
//        LinkedList<Point> inputPoints9 = new LinkedList<>();
//        inputPoints9.add(new Point(100, 100));
//        inputPoints9.add(new Point(150, 100));
//        inputPoints9.add(new Point(150, 50));
//        inputPoints9.add(new Point(200, 50));
//        inputPoints9.add(new Point(200, 100));
//        inputPoints9.add(new Point(250, 100));
//        inputPoints9.add(new Point(250, 150));
//        inputPoints9.add(new Point(200, 150));
//        inputPoints9.add(new Point(200, 200));
//        inputPoints9.add(new Point(150, 200));
//        inputPoints9.add(new Point(150, 150));
//        inputPoints9.add(new Point(100, 150));
//        ArrayList<Rect> rects9 = SplitRect.Split2Rect(inputPoints9);
//        for (Rect r: rects9)
//            Log.i("十字型", "Rect:"+r);
//
////         test1字型
//        LinkedList<Point> inputPoints10 = new LinkedList<>();
//        inputPoints10.add(new Point(100, 100));      //1
//        inputPoints10.add(new Point(150, 100));    //2
//        inputPoints10.add(new Point(150, 50));   //3
//        inputPoints10.add(new Point(200, 50));     //4
//        inputPoints10.add(new Point(200, 100));   //5
//        inputPoints10.add(new Point(250, 100));   //6
//        inputPoints10.add(new Point(250, 50));    //7
//        inputPoints10.add(new Point(300, 50));    //8
//        inputPoints10.add(new Point(300, 100));   //9
//        inputPoints10.add(new Point(350, 100));   //10
//        inputPoints10.add(new Point(350, 150));   //11
//        inputPoints10.add(new Point(300, 150));   //12
//        inputPoints10.add(new Point(300, 200));   //13
//        inputPoints10.add(new Point(350, 200));   //14
//        inputPoints10.add(new Point(350, 250));   //15
//        inputPoints10.add(new Point(300, 250));   //16
//        inputPoints10.add(new Point(300, 300));   //17
//        inputPoints10.add(new Point(250, 300));   //18
//        inputPoints10.add(new Point(250, 250));   //19
//        inputPoints10.add(new Point(100, 250));   //20
//        ArrayList<Rect> rects10 = SplitRect.Split2Rect(inputPoints10);
//        for (Rect r: rects10)
//            Log.i("test1字型", "Rect:"+r);

//        // 凸字形
//        LinkedList<Point> inputPoints11 = new LinkedList<>();
//        inputPoints11.add(new Point(100, 100));
//        inputPoints11.add(new Point(150, 100));
//        inputPoints11.add(new Point(150, 50));
//        inputPoints11.add(new Point(200, 50));
//        inputPoints11.add(new Point(200, 100));
//        inputPoints11.add(new Point(250, 100));
//        inputPoints11.add(new Point(250, 150));
//        inputPoints11.add(new Point(100, 150));
//        ArrayList<Rect> rects11 = SplitRect.Split2Rect(inputPoints11);
//        for (Rect r: rects11)
//            Log.i("凸字形", "Rect:"+r);
//
//        // 倒凹字形
//        LinkedList<Point> inputPoints12 = new LinkedList<>();
//        inputPoints12.add(new Point(100, 100));
//        inputPoints12.add(new Point(250, 100));
//        inputPoints12.add(new Point(250, 200));
//        inputPoints12.add(new Point(200, 200));
//        inputPoints12.add(new Point(200, 150));
//        inputPoints12.add(new Point(150, 150));
//        inputPoints12.add(new Point(150, 200));
//        inputPoints12.add(new Point(100, 200));
//        ArrayList<Rect> rects12 = SplitRect.Split2Rect(inputPoints12);
//        for (Rect r: rects12)
//            Log.i("倒凹字形", "Rect:"+r);

        // 凹字形
        LinkedList<Point> inputPoints13 = new LinkedList<>();
        inputPoints13.add(new Point(100, 100));
        inputPoints13.add(new Point(150, 100));
        inputPoints13.add(new Point(150, 150));
        inputPoints13.add(new Point(200, 150));
        inputPoints13.add(new Point(200, 100));
        inputPoints13.add(new Point(250, 100));
        inputPoints13.add(new Point(250, 200));
        inputPoints13.add(new Point(100, 200));
        ArrayList<Rect> rects13 = SplitRect.Split2Rect(inputPoints13);
        for (Rect r: rects13)
            Log.i("凹字形", "Rect:"+r);
    }
}

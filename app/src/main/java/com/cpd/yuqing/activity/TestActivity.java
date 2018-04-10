package com.cpd.yuqing.activity;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cpd.yuqing.util.SplitRect;

import java.util.ArrayList;
import java.util.LinkedList;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinkedList<Point> inputPoints = new LinkedList<>();
        inputPoints.add(new Point(100, 100));      //1
        inputPoints.add(new Point(150, 100));    //2
        inputPoints.add(new Point(150, 50));   //3
        inputPoints.add(new Point(200, 50));     //4
        inputPoints.add(new Point(200, 100));   //5
        inputPoints.add(new Point(250, 100));   //6
        inputPoints.add(new Point(250, 50));    //7
        inputPoints.add(new Point(300, 50));    //8
        inputPoints.add(new Point(300, 100));   //9
        inputPoints.add(new Point(350, 100));   //10
        inputPoints.add(new Point(350, 150));   //11
        inputPoints.add(new Point(300, 150));   //12
        inputPoints.add(new Point(300, 200));   //13
        inputPoints.add(new Point(250, 200));   //14
        inputPoints.add(new Point(250, 150));   //15
        inputPoints.add(new Point(200, 150));   //16
        inputPoints.add(new Point(200, 200));   //17
        inputPoints.add(new Point(150, 200));   //18
        inputPoints.add(new Point(150, 150));   //19
        inputPoints.add(new Point(100, 150));   //20

        //  将第一个点加到队尾
        inputPoints.add(new Point(100, 100));      //1

        ArrayList<Rect> rects = SplitRect.Split2Rect(inputPoints);
        for (Rect r: rects)
            System.out.println(r);
    }
}

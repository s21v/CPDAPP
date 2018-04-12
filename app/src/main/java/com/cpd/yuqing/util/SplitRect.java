package com.cpd.yuqing.util;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 将不规则矩形拆分成规则矩形
 * Created by s21v on 2018/4/10.
 */
public class SplitRect {

    public static ArrayList<Rect> Split2Rect(LinkedList<Point> points) {
        ArrayList<Rect> rects = new ArrayList<>();
        ArrayList<Point> pointsInRect = new ArrayList<>();  // 存放常规矩形的潜在顶点（预备顶点）
        ArrayList<Edge> edgeInRect = new ArrayList<>();  // 存放常规矩形的边长
        while (!points.isEmpty()) {
            //  将点加入到临时列表中
            pointsInRect.add(points.poll());
            if (pointsInRect.size() > 1) {
                // 当预备顶点中的顶点大于两个点后，可以计算边长
                int lastPointIndexInRect = pointsInRect.size() - 1;
                // 合并相同方向上的点，并去掉多余的候选点
                Edge newEdge = getEdgeInfo(pointsInRect.get(lastPointIndexInRect - 1),
                        pointsInRect.get(lastPointIndexInRect));
                if (edgeInRect.size() > 1) {
                    Edge previousEdge = edgeInRect.get(edgeInRect.size() - 1);
                    if (newEdge.direction == previousEdge.direction) {
                        newEdge = new Edge(newEdge.type, newEdge.direction, newEdge.distance + previousEdge.distance);
                        edgeInRect.remove(edgeInRect.size() - 1);
                        pointsInRect.remove(lastPointIndexInRect - 1);
                    }
                }
                edgeInRect.add(newEdge);
                // 当边的数量大于等于3，就可以计算矩形的走势（内收型，外扩型）
                int lastEdgeIndexInRect = edgeInRect.size() - 1;
                if (edgeInRect.size() >= 3) {
                    // 获取要比较的两条平行的边
                    Edge edge1 = edgeInRect.get(lastEdgeIndexInRect - 2);    //  要比较的第一条边
                    Edge edge2 = edgeInRect.get(lastEdgeIndexInRect);        //  要比较的第二条边
                    if (isInward(edge1, edge2)) {     //  内收型，可以分割出常规矩形，或直接获得常规矩形
                        Point first = pointsInRect.get(lastEdgeIndexInRect - 2);    //  第一条边的起点
                        Point end = pointsInRect.get(lastEdgeIndexInRect + 1);      //  第二条边的终点
                        if (Math.abs(edge1.distance) == Math.abs(edge2.distance)) {     // 如果两条边的绝对值相等即认为找到一个常规的矩形
                            // 将end顶点放回队列的开头
                            points.add(0, end);
                            // 将first顶点放回到队列的尾部
                            points.add(first);
                            // 根据定点确定矩形
                            Rect rect = getRect(pointsInRect);
                            rects.add(rect);
                            // 清除 pointsInRect、 edgeInRect中的数据，以便继续使用
                            pointsInRect.clear();
                            edgeInRect.clear();
                        }
                        //  两条边的绝对值不想等,那么可以在较长的边截取一个新的点（点的平行向坐标值和较短的点的值一致），这个点和剩下的点可以组成一个常规的矩形
                        if (Math.abs(edge1.distance) < Math.abs(edge2.distance)) {    //  第一条边较短
                            Point newPoint;
                            //  按照数据点的出现规律：横、纵、横、纵..... ，所以所在位置为偶数的边为横向边（边的两个定点的y值相同，x不同）
                            if (edge1.type == Type.HORIZONTAL) {  // 横向边
                                newPoint = new Point(first.x, end.y);
                            } else {    // 纵向边
                                newPoint = new Point(end.x, first.y);
                            }
                            //  从 预备顶点 中删去无用的点
                            pointsInRect.remove(lastEdgeIndexInRect + 1);
                            //  添加新截取的点到 预备顶点
                            pointsInRect.add(newPoint);
                            //  原始点链表队列 依次添加first newPoint end  ， 因为新截取的点在较长的一边上所以
                            points.add(0, first);
                            points.add(1, newPoint);
                            //  并将删去的点添加到原始点链表队列中去(这里两个点的顺序不能乱，也要按顺时针方向添加)
                            points.add(2, end);
                            //  得到截取到的新矩形
                            Rect rect = getRect(pointsInRect);
                            rects.add(rect);
                            //  清除 pointsInRect、 edgeInRect中的数据，以便继续使用
                            pointsInRect.clear();
                            edgeInRect.clear();
                        } else if (Math.abs(edge1.distance) > Math.abs(edge2.distance)) { // 第二条边较短
                            Point newPoint;
                            if (edge1.type == Type.HORIZONTAL) {  // 横向边
                                newPoint = new Point(end.x, first.y);
                            } else {    // 纵向边
                                newPoint = new Point(first.x, end.y);
                            }
                            //  从 预备顶点 中删去无用的点
                            pointsInRect.remove(lastEdgeIndexInRect - 2);
                            //  添加新截取的点到 预备顶点
                            pointsInRect.add(newPoint);
                            //  比较end之后的点，以决定是否将end顶点添加回队列
                            Point next = points.peek();
                            //  将删去的点添加到原始点链表队列的队尾(这里两个点的顺序不能乱，也要按顺时针方向添加)      //todo 向原始点队列添加时应视end节点的下一个点所在位置决定是否添加、及添加的顺序
                            points.add(0, first);
                            //  添加新截取的点到原始点链表队列的队头
                            points.add(1, newPoint);
                            if (next != null)
                                if (newPoint.x == end.x) {
                                    if (newPoint.x != next.x)
                                        points.add(2, end);
                                } else if (newPoint.y == end.y) {
                                    if (newPoint.y != next.y)
                                        points.add(2, end);
                                }
                            //  得到截取到的新矩形
                            Rect rect = getRect(pointsInRect);
                            rects.add(rect);
                            //  清除 pointsInRect、 edgeInRect中的数据，以便继续使用
                            pointsInRect.clear();
                            edgeInRect.clear();
                        }
                    } else {    //外扩型图形
                        //  从 预备顶点 中删除无用的点，并将其放回到原始点链表队列中以备后面使用
                        Point backup = pointsInRect.remove(0);
                        points.add(backup);
                        // 删除此顶点相关的边长信息
                        edgeInRect.remove(0);
                    }
                }
            }
        }
        checkIntersectRect(rects);
        return rects;
    }

    //  检查并去掉重叠的矩形，返回结果应该是矩形之间互不重叠
    private static void checkIntersectRect(ArrayList<Rect> rects) {
        for (int i = 0; i < rects.size(); i++) {
            Rect x = rects.get(i);
            for (int j = i+1; j < rects.size(); j++) {

                    Rect y = rects.get(j);
                    if (y.contains(x)) {
                        Log.i("checkIntersectRect", y + " contains " + x);
                        Log.i("checkIntersectRect", "intersect: "+y.intersect(x));
                        Log.i("checkIntersectRect", "after intersect: "+y);
                    }

            }
        }
    }

    //  根据四个点确定矩形
    private static Rect getRect(ArrayList<Point> pointsInRect) throws IllegalArgumentException {
        if (pointsInRect.size() != 4)
            throw new IllegalArgumentException("坐标点数目异常，pointsInRectSize：" + pointsInRect.size());
        else {
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE,
                    minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            for (Point p : pointsInRect) {
                if (p.x > maxX)
                    maxX = p.x;
                else if (p.x < minX)
                    minX = p.x;
                if (p.y > maxY)
                    maxY = p.y;
                else if (p.y < minY)
                    minY = p.y;
            }
            return new Rect(minX, minY, maxX, maxY);
        }
    }

    //  获得相邻两个点的距离
    private static Edge getEdgeInfo(Point from, Point to) throws IllegalArgumentException {
        if (from.x == to.x) {
            if (from.y > to.y)
                return new Edge(Type.VERTICAL, Direction.toTop, to.y - from.y);
            else
                return new Edge(Type.VERTICAL, Direction.toBottom, to.y - from.y);
        } else if (from.y == to.y)
            if (from.x > to.x)
                return new Edge(Type.HORIZONTAL, Direction.toLeft, to.x - from.x);
            else
                return new Edge(Type.HORIZONTAL, Direction.toRight, to.x - from.x);
        else
            throw new IllegalArgumentException("无法测出距离，不合理的点：from:" + from + "， to:" + to);
    }

    //  通过两个整数的正负符号是否不同，来判断图形的走势。
    //  正负符号不同测证明是内收型图形，可以分割出一个规则矩形
    //  正负符号相同证明是外扩型图形，此时还不能够分割出一个规则矩形
    private static boolean isInward(Edge e1, Edge e2) {
        if (e1.direction == Direction.toRight)
            return e2.direction == Direction.toLeft;
        else if (e1.direction == Direction.toLeft)
            return e2.direction == Direction.toRight;
        else if (e1.direction == Direction.toBottom)
            return e2.direction == Direction.toTop;
        else
            return e2.direction == Direction.toBottom;
    }

    // 边的大体走向：横、竖
    enum Type {
        HORIZONTAL, VERTICAL,
    }

    // 边的详细走向：上下左右
    enum Direction {
        toTop, toLeft, toBottom, toRight
    }

    private static class Edge {
        Type type;
        Direction direction;
        int distance;

        Edge(Type type, Direction direction, int distance) {
            this.type = type;
            this.direction = direction;
            this.distance = distance;
        }
    }
}

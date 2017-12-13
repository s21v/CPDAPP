package com.cpd.yuqing.data;

import java.util.List;

/**
 * Created by s21v on 2017/8/3.
 */
public class City {

    /**
     * children : []
     * id : 2799
     * name : 三环以内
     */

    private String id;
    private String name;
    private List<City> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<City> getChildren() {
        return children;
    }

    public void setChildren(List<City> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "City{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", children=" + children +
                '}';
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }
}

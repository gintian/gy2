package com.hjsj.hrms.module.dashboard.portlets.test;

import com.hjsj.hrms.module.dashboard.portlets.buildin.ChartWidgetBase;

import java.util.ArrayList;
import java.util.List;

public class ChartTestProvider extends ChartWidgetBase {

    @Override
    public List<ItemModel> getChartData() {

        ArrayList list = new ArrayList();
        list.add(new ItemModel("博士",33));
        list.add(new ItemModel("硕士",50));
        list.add(new ItemModel("本科",22));
        list.add(new ItemModel("大专",71));
        list.add(new ItemModel("高中",90));
        list.add(new ItemModel("其他",15));
        return list;
    }
}

package com.hjsj.hrms.module.dashboard.portlets.test;

import com.hjsj.hrms.module.dashboard.portlets.buildin.TableWidgetBase;

import java.util.ArrayList;
import java.util.List;

public class TableTestProvider extends TableWidgetBase {

    @Override
    public TableModel getChartData() {

        TableModel tableModel = new TableModel();

        ColumnModel c1 = new ColumnModel("姓名","name");
        ColumnModel c2 = new ColumnModel("地址","address");
        ColumnModel c3 = new ColumnModel("日期","date");

        List columnList = new ArrayList();
        columnList.add(c1);
        columnList.add(c2);
        columnList.add(c3);
        tableModel.setColumns(columnList);

        List recordList = new ArrayList();

        RecordModel recordModel = new RecordModel();
        recordModel.put("name","王小虎");
        recordModel.put("address","上海市普陀区金沙江路 1518 弄");
        recordModel.put("date","2016-05-02");
        recordModel.setDetailUrl("http://www.baidu.com");
        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);

        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);
        recordList.add(recordModel);

        /**/

        tableModel.setTableDatas(recordList);

        return tableModel;
    }
}

package com.hjsj.hrms.module.dashboard.portlets.test;

import com.hjsj.hrms.module.dashboard.portlets.buildin.ListWidgetBase;

import java.util.ArrayList;
import java.util.List;

public class ListTestProvider extends ListWidgetBase {

    @Override
    public List<RecordData> getListData() {
        ArrayList list = new ArrayList();
        list.add(new RecordData("通知A","2018-06-12","10","http://www.baidu.com"));
        list.add(new RecordData("通知B","2018-06-12","10","http://www.baidu.com"));
        list.add(new RecordData("通知C","2018-06-12","10","http://www.baidu.com"));
        list.add(new RecordData("通知D","2018-06-12","10","http://www.baidu.com"));
        list.add(new RecordData("通知E","2018-06-12","10","http://www.baidu.com"));
        list.add(new RecordData("通知F","2018-06-12","10","http://www.baidu.com"));

        return list;
    }
}

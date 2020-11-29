package com.hjsj.hrms.module.dashboard.portlets.test;

import com.hjsj.hrms.module.dashboard.portlets.buildin.NumberWidgetBase;

import java.util.ArrayList;
import java.util.List;

public class NumberTestProvider extends NumberWidgetBase {

    @Override
    public List<NumberModel> getNumberData() {
        ArrayList list = new ArrayList();
        list.add(new NumberModel("博士",330,"头","color:red"));
        list.add(new NumberModel("硕士",500,"只","color:orange"));
        list.add(new NumberModel("本科",220,"匹","color:yellow"));
        list.add(new NumberModel("高中",900,"条","color:green"));
        list.add(new NumberModel("初中",120,"根","color:blue"));
        list.add(new NumberModel("其他",150,"尾","color:purple"));
        return list;
    }
}

package com.hjsj.hrms.module.dashboard.display.businessobject;

import com.hrms.struts.valueobject.UserView;

import java.sql.SQLException;
import java.util.ArrayList;

public interface PlanService {
    /**
     * 根据菜单id获取指定菜单门户方案列表
     *
     * @param userView
     * @return
     * @throws SQLException
     */
    public ArrayList getPlanListByMenu(UserView userView, String menuid) throws Exception;

    /**
     * 获取方案的组件集合
     * @param planid
     * @return
     * @throws Exception
     */
    public ArrayList getPlanWidgetList(String planid) throws Exception;
}

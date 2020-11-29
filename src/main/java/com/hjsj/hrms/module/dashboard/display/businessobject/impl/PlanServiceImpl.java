package com.hjsj.hrms.module.dashboard.display.businessobject.impl;

import com.google.gson.Gson;
import com.hjsj.hrms.module.dashboard.display.businessobject.PlanService;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

public class PlanServiceImpl implements PlanService {

    Connection conn;
    ContentDAO dao;

    public PlanServiceImpl(Connection conn) {
        this.conn = conn;
        this.dao = new ContentDAO(this.conn);
    }

    @Override
    public ArrayList getPlanListByMenu(UserView userView, String menuid) throws Exception {
        /*如果menuid为空，查全部*/
        menuid = menuid == null ? "%" : menuid;

        String b0110;
        //查找用户所属单位，如果没有则可能是业务用户，查找操作单位
        if (userView.isSuper_admin()) {
            b0110 = "%";
        } else {
            String orgid = userView.getUserOrgId();
            if (StringUtils.isBlank(orgid) && userView.getStatus() == 4) {
                orgid = userView.getManagePrivCodeValue();
            }
            if (StringUtils.isBlank(orgid)) {
                b0110 = orgid;
            } else {
                b0110 = orgid + "%";
            }
        }

        ArrayList planList = this.searchPlan(b0110,menuid);

        for(int i=planList.size()-1;i>-1;i--){
            HashMap plan = (HashMap)planList.get(i);
            String rolelist = (String)plan.remove("rolelist");
            if (rolelist.length() > 0 && !userView.isSuper_admin()) {
                //权限控制，先忽略
                StringTokenizer st = new StringTokenizer(rolelist, ",");
                boolean hasRole = false;
                while (st.hasMoreElements()) {
                    if (userView.haveRoleId(st.nextToken())) {
                        hasRole = true;
                        break;
                    }
                }

                if (!hasRole) {
                    planList.remove(i);
                    continue;
                }
            }

            /* 方案id加密处理一下*/
            plan.put("planid", PubFunc.encrypt((String)plan.get("planid")));
        }

        return planList;
    }

    @Override
    public ArrayList getPlanWidgetList(String planid) throws Exception {
        ArrayList widgets = new ArrayList();
        String detailSql = "select itemid,A.widgetid,w,h,x,y,params,name,B.vueWidget,B.extWidget from t_sys_portal_plandetail A left join t_sys_portal_widget B on A.widgetid=B.widgetid where A.planid=?";
        try(RowSet rowSet = this.dao.search(detailSql, Arrays.asList(planid))){
            while (rowSet.next()) {
                HashMap portlet = new HashMap(10);
                portlet.put("itemid", rowSet.getString("itemid"));
                portlet.put("widgetid", rowSet.getInt("widgetid"));
                portlet.put("name", rowSet.getString("name"));
                portlet.put("w", rowSet.getInt("w"));
                portlet.put("h", rowSet.getInt("h"));
                portlet.put("x", rowSet.getInt("x"));
                portlet.put("y", rowSet.getInt("y"));
                portlet.put("vueWidget", rowSet.getString("vueWidget"));
                portlet.put("extWidget", rowSet.getString("extWidget"));
                Object params = new Gson().fromJson(Sql_switcher.readMemo(rowSet, "params"), HashMap.class);
                portlet.put("params", params);

                widgets.add(portlet);
            }
        }
        return widgets;
    }

    private ArrayList searchPlan(String b0110,String menuid) throws Exception {
        ArrayList planList = new ArrayList();

        String sql = "select planid,planname,menuid,rolelist,thumbnail from t_sys_portal_plan where b0110 like ? and  menuid like ? order by Create_time ";
        ArrayList values = new ArrayList();
        values.add(b0110);
        values.add(menuid);

        try(RowSet rowSet = this.dao.search(sql,values)){
            while(rowSet.next()){
                HashMap planItem = new HashMap();
                planItem.put("planid", rowSet.getString("planid"));
                planItem.put("planname", rowSet.getString("planname"));
                planItem.put("menuid", rowSet.getString("menuid"));
                planItem.put("rolelist", Sql_switcher.readMemo(rowSet, "rolelist"));
                planItem.put("thumbnail", Sql_switcher.readMemo(rowSet, "thumbnail"));
                planList.add(planItem);
            }
        }
        return planList;
    }
}

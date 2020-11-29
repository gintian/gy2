package com.hjsj.hrms.redevelopment.extractexperter.transaction;

import com.hjsj.hrms.redevelopment.extractexperter.businessobject.ProjectBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * @Title:        SearchProjectInforTrans.java
 * @Description:  项目信息管理主界面输入框查询
 * @Company:      hjsj     
 * @Create time:  2015-11-26 下午01:51:18
 * @author        chenxg
 * @version       1.0
 */
public class SearchProjectInforTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            ArrayList<String> valuesList = new ArrayList<String>();
            String subModuleId = (String) this.getFormHM().get("subModuleId");
            if ("re_project_00001".equals(subModuleId)) {
                // 查询类型，1为输入查询，2为方案查询
                String type = (String) this.getFormHM().get("type");
                if ("1".equals(type)) {
                    // 输入的内容
                    valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
                }
            }

            // 拼接sql
            StringBuffer sql = new StringBuffer("select * from n03 where 1=1");
            if (valuesList.size() > 0) {
                String where = getWhereSql(valuesList);
                if (!StringUtils.isEmpty(where))
                    sql.append(" and (" + where + ")");
            }
            //重新加载查询sql
            TableDataConfigCache catche=(TableDataConfigCache)this.userView.getHm().get("re_project_00001");
            catche.setTableSql(sql.toString());
            this.getFormHM().put("messages", "");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 拼接 查询条件
     * @param valuelist 输入框中的输入的值
     * @return
     */
    private String getWhereSql(ArrayList<String> valuelist) {
        if (valuelist == null || valuelist.size() < 1)
            return "";

        StringBuffer where = new StringBuffer();
        ProjectBo bo = new ProjectBo(this.frameconn, this.userView);
        for (int i = 0; i < valuelist.size(); i++) {
            String sqlwhere = valuelist.get(i);
            if (StringUtils.isEmpty(sqlwhere))
                continue;
            
            sqlwhere = SafeCode.decode(sqlwhere);
            if (StringUtils.isEmpty(sqlwhere))
                continue;

            if (where != null && where.length() > 1)
                where.append(" or");

            where.append(" N0302 like '%" + sqlwhere + "%'");
            // 获取部门编号
            String orgids = bo.getOrgIds(sqlwhere);
            where.append(" or N0304 in ('abc'" + orgids + ")");
            where.append(" or N0305 in ('abc'" + orgids + ")");

            where.append(" or " + Sql_switcher.dateToChar("N0306", "yyyy-mm-yy") + "='" + sqlwhere + "'");
            where.append(" or N0307 like '%" + sqlwhere + "%'");
            where.append(" or N0319 like '%" + sqlwhere + "%'");
        }

        if (where == null || where.length() < 1)
            where.append("");

        return where.toString();

    }
}

package com.hjsj.hrms.module.projectmanage.project.transaction;

import com.hjsj.hrms.module.projectmanage.project.businessobject.ProjectManageBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * 项目管理查询
 * 
 * @Title: SearchProjectManageTrans.java
 * @Description:
 * @Company: hjsj
 * @Create time: 2016-1-5 下午04:38:13
 * @author chenxg
 * @version 1.0
 */
public class SearchProjectManageTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            String where = "";
            ArrayList<String> valuesList = new ArrayList<String>();
            String subModuleId = (String) this.getFormHM().get("subModuleId");
            ProjectManageBo bo = new ProjectManageBo(this.userView, this.frameconn);
            String p1119 = (String) this.getFormHM().get("p1119");
            if ("projectmanage_0001".equals(subModuleId)) {
                // 查询类型，1为输入查询，2为方案查询
                String type = (String) this.getFormHM().get("type");
                if ("1".equals(type)) {
                    // 输入的内容
                    valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
                    if (valuesList.size() > 0)
                        where = getWhereSql(valuesList);
                    
                } else if ("2".equals(type)) {
                    String exp = (String) this.getFormHM().get("exp");
                    String cond = (String) this.getFormHM().get("cond");
                    // 解析表达式并获得sql语句
                    FactorList parser = new FactorList(SafeCode.decode(exp), PubFunc
                            .keyWord_reback(SafeCode.decode(cond)), userView.getUserName());
                    String expWhr = parser.getSingleTableSqlExpression("P11");
                    if (!StringUtils.isEmpty(exp))
                        where = expWhr;
                    else 
                        where = "1=1";
                }
            }
            
            this.userView.getHm().put("projectWhere", where);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取查询条件
     * 
     * @param valuesList
     *            输入的内容
     * @return
     */
    private String getWhereSql(ArrayList<String> valuesList) {
        StringBuffer where = new StringBuffer();
        for (int i = 0; i < valuesList.size(); i++) {
            String sqlwhere = valuesList.get(i);
            if (StringUtils.isEmpty(sqlwhere))
                continue;
            
            sqlwhere = SafeCode.decode(sqlwhere);
            if (StringUtils.isEmpty(sqlwhere))
                continue;

            if (where != null && where.length() > 1)
                where.append(" OR");

            where.append(" P1103 like '%" + sqlwhere + "%'");
            where.append(" OR P1105 like '%" + sqlwhere + "%'");
        }

        if (where == null || where.length() < 1)
            where.append("");
        
        return where.toString();
    }

}

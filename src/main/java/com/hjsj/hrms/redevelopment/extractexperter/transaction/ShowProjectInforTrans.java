package com.hjsj.hrms.redevelopment.extractexperter.transaction;

import com.hjsj.hrms.redevelopment.extractexperter.businessobject.ProjectBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * @Title:        ShowProjectInforTrans.java
 * @Description:  项目信息管理主界面展现
 * @Company:      hjsj     
 * @Create time:  2015-11-26 下午01:52:06
 * @author        chenxg
 * @version       1.0
 */
public class ShowProjectInforTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            ProjectBo bo = new ProjectBo(this.frameconn, this.userView);
             // 获取表头
            ArrayList<ColumnsInfo> columnlist = bo.getColumnList();
            // 拼接sql
            String sql = getSql();
            // 生成表格
            TableConfigBuilder builder = new TableConfigBuilder("re_project_00001", columnlist, "re_project", this.userView, this.getFrameconn());
            builder.setDataSql(sql);
            builder.setOrderBy(" order by n0301 desc");
            builder.setAutoRender(true);
            builder.setTitle("纪委专家库抽选");
            builder.setSetScheme(true);
            builder.setScheme(true);
            builder.setSchemeItemKey("Y:n03");
            builder.setColumnFilter(true);
            builder.setSelectable(true);
            builder.setPageSize(20);
            builder.setLockable(true);
            builder.setConstantName("extractexperter/showProjectInforList");
            builder.setTableTools(bo.getButtonList());
            builder.setRowdbclick("");
            builder.setEditable(true);
            String config = builder.createExtTableConfig();
            this.getFormHM().put("tableConfig", config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 拼接查询用的sql
     * @return sql语句
     */
    private String getSql() {
    	StringBuffer sql = new StringBuffer("select ");
		ArrayList fieldList = DataDictionary.getFieldList("n03", Constant.USED_FIELD_SET);
		
		for(int i = 0; i < fieldList.size(); i++) {
			FieldItem fi = (FieldItem) fieldList.get(i);
			if(fi == null)
				continue;
			
			String itemId = fi.getItemid();
			String itemType = fi.getItemtype();
			if("N".equalsIgnoreCase(itemType))
				sql.append(Sql_switcher.isnull(itemId, "0"));
			else
				sql.append(Sql_switcher.isnull(itemId, "''"));
			
			sql.append(" AS " + itemId);
			sql.append(",");
		}
		
		if(sql.toString().endsWith(","))
			sql.setLength(sql.length() - 1);
		
		sql.append(" from n03");
		return sql.toString();
	}
}

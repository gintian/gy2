package com.hjsj.hrms.module.gz.salaryaccounting.changecompare.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.changecompare.businessobject.ChangeCompareBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * <p>Title:UpdateStateTrans.java</p>
 * <p>Description>:薪资发放_更新选中状态</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 18, 2016 3:29:52 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class UpdateStateTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		String dbname=(String)this.getFormHM().get("dbname"); // 人员库
		String a0100=(String)this.getFormHM().get("a0100"); // 员工号
		String tabletype=(String)this.getFormHM().get("tabletype"); // 表区分
		String state=(String)this.getFormHM().get("state"); // 更新值
		String isUpdateAll=(String)this.getFormHM().get("isUpdateAll");//是否需要把全部数据更新成state值
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			ChangeCompareBo changeCompareBo = new ChangeCompareBo(this.getFrameconn(), this.userView);// 工具类
			if("1".equals(isUpdateAll)){//把全部数据更新成0
				String tableName = changeCompareBo.getTableName(tabletype);
				String sql = "update " + tableName + " set STATE='"+state+"'";
				dao.update(sql.toString());
				return ;
			}
			
			String tableName = changeCompareBo.getTableName(tabletype);
			String sql = "update " + tableName + " set STATE=? where DBNAME=? and A0100=?";
			ArrayList<String> list = new ArrayList<String>();
			list.add(state);
			list.add(dbname);
			list.add(a0100);
			dao.update(sql.toString(), list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

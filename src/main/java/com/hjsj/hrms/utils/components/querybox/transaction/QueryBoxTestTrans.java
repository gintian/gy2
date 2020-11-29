package com.hjsj.hrms.utils.components.querybox.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * 方案查询测试
 * @author wangzj
 * 2015-06-30 10:08:00
 */
public class QueryBoxTestTrans extends IBusiness{
	
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		// 查询类型，1为输入查询，2为方案查询
		String type = (String) this.getFormHM().get("type");
		
		if("1".equals(type)) {
			// 输入的内容
			List values = (ArrayList) this.getFormHM().get("inputValues");
			this.getFormHM().put("status", "成功！！！");
		} else if ("2".equals(type)) {
			String exp = (String) this.getFormHM().get("exp");
			String cond = (String) this.getFormHM().get("cond");
			
			// 解析表达式并获得sql语句
			FactorList parser = new FactorList(exp ,cond, userView.getUserName());
			String condSql = parser.getSingleTableSqlExpression("data");
			
			
			this.getFormHM().put("status", "成功！！！");
		}
		
		
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new GeneralException(e.getMessage());
		}
	}
}

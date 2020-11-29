package com.hjsj.hrms.transaction.browse.history;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSetMetaData;
import java.util.HashMap;

/**
 * <p>Title:SearchQueryTrans.java</p>
 * <p>Description>:SearchQueryTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 25, 2010 5:29:50 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class SearchQueryTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		// 因子表达式
		String sexpr = SafeCode.decode((String) this.getFormHM().get("sexpr"));
		sexpr = sexpr != null && sexpr.trim().length() > 0 ? sexpr : "";
		sexpr = PubFunc.keyWord_reback(sexpr);
		// 条件
		String sfactor = (String) this.getFormHM().get("sfactor");
		sfactor = sfactor != null && sfactor.trim().length() > 0 ? sfactor : "";
		sfactor = SafeCode.decode(sfactor);
		sfactor = PubFunc.keyWord_reback(sfactor);
		
		String strQuery = "";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		//String setname=(String)this.getFormHM().get("tablename");
		String setname="hr_emp_hisdata";
		HashMap map =  new HashMap();
		String sql="select * from "+setname+ " where 1=2";
		try {
			this.frowset = dao.search(sql.toString());
			ResultSetMetaData rsmd = this.frowset.getMetaData();
			int size = rsmd.getColumnCount();
			for (int i = 1; i <= size; i++) {
				
				String field = rsmd.getColumnName(i);
				if("id".equalsIgnoreCase(field))
					continue;
				if("Nbase".equalsIgnoreCase(field))
					continue;
				if("A0000".equalsIgnoreCase(field))
					continue;
				if("A0100".equalsIgnoreCase(field))
					continue;
				if(field!=null&&field.length()>0){
					FieldItem fi=DataDictionary.getFieldItem(field);
					if(fi==null)
						continue;
					FieldItem fieldItemClone = (FieldItem)fi.clone();
					fieldItemClone.setFieldsetid(setname);
					fieldItemClone.setUseflag("1");
					map.put(field.toLowerCase(), fieldItemClone);
				}
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
		//zhangcq 2016-7-25 根据历史节点条件进行查询
		FactorList factorslist = new FactorList(sexpr, PubFunc.getStr(sfactor), userView.getUserId(),map);
		factorslist.setSuper_admin(userView.isSuper_admin());
		strQuery = factorslist.getSingleTableSqlExpression(setname);
		strQuery = " AND " + strQuery;
		strQuery =  strQuery.replaceAll("hr_emp_hisdata", "heh");
		this.getFormHM().put("check", "ok");
		this.userView.getHm().put("staff_sql", strQuery);
	}

}

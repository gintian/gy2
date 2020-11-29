package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchDblistTrans extends IBusiness{

	public void execute() throws GeneralException {
		ArrayList dblist = new ArrayList();
		try{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String str_db = (String)hm.get("dblist");
			hm.remove("str_db");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sb = new StringBuffer();
			sb.append("select Pre,dbname from DBName");
			this.frowset = dao.search(sb.toString());
			while(this.frowset.next()){
				LazyDynaBean dblistBean = new LazyDynaBean();
				String pre = this.frowset.getString("pre");
				String dbname = this.frowset.getString("dbname");
				String isCheck="0";//默认是没有选中
				if(str_db.indexOf(pre)!=-1)
					isCheck="1";
				dblistBean.set("pre", pre);
				dblistBean.set("dbname", dbname);
				dblistBean.set("isCheck", isCheck);
				dblist.add(dblistBean);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("selectDblist", dblist);
		
	}

}

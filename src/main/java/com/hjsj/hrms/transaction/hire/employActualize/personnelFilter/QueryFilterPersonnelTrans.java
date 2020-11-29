package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:QueryFilterPersonnelTrans.java</p>
 * <p>Description:根据条件查询候选人应聘职位对应表中的信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 1, 2006 2:43:33 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class QueryFilterPersonnelTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String b_query=(String)hm.get("b_query");
		//hm.remove("b_query");
		String codeid=(String)hm.get("code");
		String codesetid=(String)hm.get("codeset");	
		String operate=(String)hm.get("operate");
		String extendSql=(String)this.getFormHM().get("extendSql");
		String orderSql=(String)this.getFormHM().get("orderSql");
		
		if(operate!=null&& "init".equals(operate))
		{
			extendSql="";
			orderSql="";
		}
		
		if(extendSql!=null&&extendSql.length()>1)
		{
			extendSql=extendSql.replaceAll("codeitemdesc","org.codeitemdesc");
			extendSql=extendSql.replaceAll("departname","org2.codeitemdesc");
			
		}
		ArrayList tableHeadNameList=new ArrayList();				  //表头列名；
		ArrayList tableColumnsList=new ArrayList();			
		
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		ArrayList list=employActualize.getTableColumn_headNameList();
		tableHeadNameList=(ArrayList)list.get(0);
		tableColumnsList=(ArrayList)list.get(1);
		String columns=(String)list.get(2);
		
		String dbname=employActualize.getZP_DB_NAME();
		if("0".equals(b_query)||codeid==null)
		{
			if(!userView.isAdmin()){
				codeid=this.getUserView().getManagePrivCodeValue();
				b_query="link";
			}
			else			
				codeid="0";
		
		}
		String sql=employActualize.getQuerySQL(dbname,tableColumnsList,codeid,extendSql);
		this.getFormHM().put("hireStateStr",gethireStateStr());
		this.getFormHM().put("dbName",dbname);
		this.getFormHM().put("select_str",sql.substring(0,sql.indexOf("from")));
		this.getFormHM().put("from_str",sql.substring(sql.indexOf("from")));
		this.getFormHM().put("tableHeadNameList",tableHeadNameList);
		this.getFormHM().put("tableColumnsList",tableColumnsList);
		this.getFormHM().put("columns",columns);
		this.getFormHM().put("codeid",codeid);
		this.getFormHM().put("username",this.getUserView().getUserName());
		this.getFormHM().put("linkDesc",b_query);
		if(operate!=null&& "init".equals(operate))
		{
			this.getFormHM().put("extendSql"," ");
			this.getFormHM().put("orderSql"," ");
		}
		hm.remove("operate");
	}
	
	public String gethireStateStr()
	{
		StringBuffer str=new StringBuffer("");
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from codeitem where codesetid='36' and parentid='1' and codeitemid=childid and parentid<>childid order by codeitemid");
			while(this.frowset.next())
			{
				str.append("~"+this.frowset.getString("codeitemid")+"&"+this.frowset.getString("codeitemdesc"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str.toString();
	}

}

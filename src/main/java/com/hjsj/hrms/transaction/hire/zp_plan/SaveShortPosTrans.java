/*
 * Created on 2005-11-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveShortPosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ArrayList strE01A1=(ArrayList)this.getFormHM().get("selectedShortPosList");
		String setstr=(String)this.getFormHM().get("setstr");
		String firstfieldstr=(String)this.getFormHM().get("firstfieldstr");
		String lastfieldstr=(String)this.getFormHM().get("lastfieldstr");
		String plan_id=(String)this.getFormHM().get("plan_id");
		StringBuffer strsql=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    List list=new ArrayList();
	    float count = 0;
		try
		{
		    if(strE01A1!=null && strE01A1.size()>0)
			{
		    	String sqlwhere="(";
		    	for(int i=0;i<strE01A1.size();i++)
		    	{
		    		LazyDynaBean bean=(LazyDynaBean)strE01A1.get(i);
		    		if(i!=strE01A1.size()-1)
		    		   sqlwhere+="'" + bean.get("e01a1") + "',";
		    		else
		    		   sqlwhere+="'" + bean.get("e01a1") + "')";		    			
		    	}
			 	String sql = "select " + setstr + ".E01A1," +setstr +  "." + firstfieldstr+","+ setstr +  "." +lastfieldstr+"," + setstr +  "." +firstfieldstr + "-" + PubFunc.getISNULL(setstr +  "." +lastfieldstr) +  " as shortcount,organization.parentid from "+setstr+",organization where  organization.codeitemid=" + setstr + ".E01A1 and " + setstr + ".E01A1 in " + sqlwhere ;
			 	this.frowset = dao.search(sql);
			 	while(this.frowset.next())
			 	{
			 		String sqlsql = "select pos_id,amount from zp_plan_details where pos_id = '"+this.frowset.getString("e01a1")+"' and plan_id='" + plan_id + "'";
			 		ResultSet rst = dao.search(sqlsql,list);
			 		if(rst.next())
			 		{
			 			String str="update zp_plan_details set amount=amount + " + this.frowset.getFloat("shortcount") + " where pos_id = '"+this.frowset.getString("e01a1")+ "' and plan_id='" + plan_id + "'";
			 			dao.update(str);
			 		}else
			 		{
			 			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
			            String details_id = idg.getId("zp_plan_details.details_id");
					    strsql.delete(0,strsql.length());
					    strsql.append("insert into zp_plan_details (details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status) values('"+details_id+"','"+this.frowset.getString("parentid")+"','"+this.frowset.getString("e01a1")+"',"+this.frowset.getFloat("shortcount")+",'','"+plan_id+"','',0,'0','0')");
					    dao.insert(strsql.toString(),list); //添加到招聘计划明细表中
			 		}			 	
			 	}			 		
		   }		    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

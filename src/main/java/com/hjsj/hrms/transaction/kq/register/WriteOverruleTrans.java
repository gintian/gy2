package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class WriteOverruleTrans extends IBusiness {
	public void execute() throws GeneralException
	{
		RecordVo one_vo=(RecordVo)this.getFormHM().get("one_vo");
		ContentDAO dao = new ContentDAO(this.getFrameconn());	
	    String userbase=one_vo.getString("nbase");
	    String work_date=one_vo.getString("q03z0");
	    String a0100=one_vo.getString("a0100");
	    String overrule=one_vo.getString("overrule");
	    RegisterInitInfoData registerInitInfoData=new RegisterInitInfoData();
        overrule=registerInitInfoData.getOverruleFormat(overrule,"02",this.userView.getUserFullName());
	    StringBuffer sql=new StringBuffer();
    	sql.append("select overrule,a0100 from q05 where ");    
    	sql.append(" nbase='"+userbase+"' ");
    	sql.append(" and a0100 = '"+a0100+"'");
    	sql.append(" and Q03Z0 ='"+work_date+"'"); 
    	sql.append(" and Q03Z5 in ('02','08','03','07')");
    	ArrayList list= new ArrayList();
	     try
	     {
	    	 this.frowset=dao.search(sql.toString());
	 		 if(this.frowset.next())
	 		 {
	 			String oldover=Sql_switcher.readMemo(this.frowset, "overrule");
	 			ArrayList u_list=new ArrayList();
	 			u_list.add(overrule + oldover);
	 	  	    u_list.add(userbase);
	 	  	    u_list.add(work_date);	    
	 	  	    u_list.add(this.frowset.getString("a0100"));
	 	  	    StringBuffer updateSql=new StringBuffer();	    
	 		    updateSql.append("update Q05 set ");
	 		    updateSql.append("overrule=?  where ");
	 		    updateSql.append("nbase=? and q03z0=? and a0100=?");	
	 		    list.add(u_list);
	 		    dao.batchUpdate(updateSql.toString(), list);
	 	  }
	    	 
	     }catch(Exception e)
	     {
	    	e.printStackTrace();
	     }
		this.getFormHM().put("one_vo",one_vo);
	}

}

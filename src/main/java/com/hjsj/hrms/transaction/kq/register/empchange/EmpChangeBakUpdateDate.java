package com.hjsj.hrms.transaction.kq.register.empchange;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 批量修改人员比对时间
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 25, 2007:10:17:13 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class EmpChangeBakUpdateDate extends IBusiness{
	public void execute()throws GeneralException
	{
		ArrayList emplist = (ArrayList)this.getFormHM().get("selectedlist");	   
	    String change_date=(String)this.getFormHM().get("curdate");	  
	    if(emplist==null||emplist.size()<=0)
	    	return ;
	    	
	    StringBuffer sql=new StringBuffer();
    	String date=Sql_switcher.dateValue(change_date);  
    	sql.append("update kq_employ_change set change_date="+date+"");
    	sql.append("where nbase=? and a0100=?");
    	ArrayList list=new ArrayList();
    	for(int i=0;i<emplist.size();i++)
    	{
    		RecordVo vo_change=(RecordVo)emplist.get(i);
//    		LazyDynaBean vo_change=(LazyDynaBean)emplist.get(i);
		    String userbase=(String)vo_change.getString("nbase");
		    String a0100=(String)vo_change.getString("a0100");
		    ArrayList one_list=new ArrayList();
		    one_list.add(userbase);
		    one_list.add(a0100);
		    list.add(one_list);
    	}
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		dao.batchUpdate(sql.toString(),list);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}

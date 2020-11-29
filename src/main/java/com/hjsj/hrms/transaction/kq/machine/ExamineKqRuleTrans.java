package com.hjsj.hrms.transaction.kq.machine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 检测考勤文件规则名称是否存在
 * <p>Title:ExamineKqRuleTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 12, 2007 4:10:37 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class ExamineKqRuleTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String name=(String)this.getFormHM().get("name");	    
		if(name==null||name.length()<=0)
		{
			this.getFormHM().put("flag","null");
			return;
		}
	    String sql="select rule_name from kq_data_rule where rule_name='"+name.trim()+"'";
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	    	this.frowset=dao.search(sql);
	    	if(this.frowset.next())
	    	{
	    		this.getFormHM().put("flag","exist");
	    		this.getFormHM().put("name",name);
				return;
	    	}else
	    	{
	    		this.getFormHM().put("flag","ok");
	    		this.getFormHM().put("name",name);
	    		return;
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(e);
	    }
	}	

}

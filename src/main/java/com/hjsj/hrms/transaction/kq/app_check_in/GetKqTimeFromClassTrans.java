package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;

public class GetKqTimeFromClassTrans extends IBusiness {
	
	
	
	public void execute() throws GeneralException {
		String class_id=(String)this.getFormHM().get("class_id");
		if(class_id==null||class_id.length()<=0|| "#".equals(class_id))
		{
			this.getFormHM().put("start_h","00");
			this.getFormHM().put("start_m","00");
			this.getFormHM().put("end_h","00");
			this.getFormHM().put("end_m","00");
			this.getFormHM().put("isspan", "false");
		}else
		{
			StringBuffer sql=new StringBuffer();
			sql.append("select * from kq_class where class_id='"+class_id+"'");
			String onduty="";
            String offduty="";
            try
            {
            	ContentDAO dao=new ContentDAO(this.getFrameconn());
            	this.frowset=dao.search(sql.toString());
            	if(this.frowset.next())
            	{
            		onduty=this.frowset.getString("onduty_1");
            		for(int i=3;i>0;i--)
            		{
            			offduty=this.frowset.getString("offduty_"+i);
            			if(offduty!=null&&offduty.length()==5)
            				break;
            		}
            	}
            	if(onduty==null||onduty.length()!=5||offduty==null||offduty.length()!=5)
            	{
            		this.getFormHM().put("start_h","");
        			this.getFormHM().put("start_m","");
        			this.getFormHM().put("end_h","");
        			this.getFormHM().put("end_m","");
        			this.getFormHM().put("isspan", "false");
            	}else
            	{
            		this.getFormHM().put("start_h",onduty.substring(0,2));
        			this.getFormHM().put("start_m",onduty.substring(3));
        			this.getFormHM().put("end_h",offduty.substring(0,2));
        			this.getFormHM().put("end_m",offduty.substring(3));
        			if(onduty.compareTo(offduty)>0)
        			{
        				this.getFormHM().put("isspan", "true");
        				String z3=(String)this.getFormHM().get("z3");
        				if(z3!=null&&z3.length()>=10)
        				{
        					z3=z3.substring(0,10);
        					z3=z3.replace(".", "-");
        					Date z3D=DateUtils.getDate(z3,"yyyy-MM-dd");
        					z3D=DateUtils.addDays(z3D, 1);
        					this.getFormHM().put("z3",DateUtils.format(z3D, "yyyy-MM-dd"));
        				}
        			}
        			else
        			  this.getFormHM().put("isspan", "false");
            	}
            }catch(Exception e)
            {
            	e.printStackTrace();
            }
			
		}
		this.getFormHM().put("class_id",class_id);

	}

}

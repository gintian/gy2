package com.hjsj.hrms.transaction.kq.register.report;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteKqReportTypeTran extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		 
		 ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedlist");	
		 if(selectedinfolist==null||selectedinfolist.size()==0)
			{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.report.noselect.manager"),"",""));	
			}
		 ArrayList list=new ArrayList();
		 for(int i=0;i<selectedinfolist.size();i++)
         {
			ArrayList one_value= new ArrayList();
			RecordVo vo=(RecordVo)selectedinfolist.get(i);
			String report_id=vo.getString("report_id");
			one_value.add(report_id);
			list.add(one_value);
         }
		 try
		 {
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
			 String del_str="delete from kq_report where report_id=?";
			 dao.batchUpdate(del_str,list);
		 }catch(Exception e)
		{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		}	
	}
}

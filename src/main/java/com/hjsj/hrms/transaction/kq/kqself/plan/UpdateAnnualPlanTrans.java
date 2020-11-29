package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * <p>Title:</p>
 * <p>Description:修改部门制定计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-22:11:55:06</p>
 * @author kf-1
 * @version 1.0
 *
 */
public class UpdateAnnualPlanTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		ArrayList onelist=(ArrayList)this.getFormHM().get("onelist");		
		RecordVo vo=new RecordVo("q29");
		for(int i=0;i<onelist.size();i++)
   	    {
   	       FieldItem field=(FieldItem)onelist.get(i);   	       
           if("N".equals(field.getItemtype()))
              vo.setDouble(field.getItemid().toLowerCase(),Double.parseDouble(field.getValue()));
	   		   else if("D".equals(field.getItemtype()))
              vo.setDate(field.getItemid().toLowerCase(),field.getValue());
	   	       else	
	   		      vo.setString(field.getItemid().toLowerCase(),field.getValue());
   	    }
		ContentDAO dao=new ContentDAO(this.getFrameconn());		
		try
		{
			dao.updateValueObject(vo);
		}catch(Exception e)
		{
		  e.printStackTrace();
		}		 
	}
	
}

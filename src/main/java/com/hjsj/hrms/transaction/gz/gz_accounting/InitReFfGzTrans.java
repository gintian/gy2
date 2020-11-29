package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *  
 * <p>Title:InitReFfGzTrans.java</p>
 * <p>Description:展现重发工资设置界面</p> 
 * <p>Company:hjsj</p> 
 * create time at:2013-10-8 上午09:42:00 
 * @author dengcan
 * @version 6.x
 */
public class InitReFfGzTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{ 
			String count="";
			String salaryid=(String)this.getFormHM().get("salaryid");
			String bosdate=(String)this.getFormHM().get("bosdate");
			this.getFormHM().remove("bosdate");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			ArrayList datelist=gzbo.getSubDateList();
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String url_desc="";
			if(hm!=null&&hm.get("b_queryRf")!=null)
			{
				url_desc=(String)hm.get("b_queryRf");
				hm.remove("b_queryRf");
			}
			ArrayList countlist=new ArrayList();
			if(((url_desc!=null&& "link".equalsIgnoreCase(url_desc))|| (bosdate==null|| "".equalsIgnoreCase(bosdate)))&&datelist.size()>0)
			{
				bosdate=((CommonData)datelist.get(0)).getDataValue();  
			}
//			else
//				throw new Exception("无数据支持重发操作！");
			if(bosdate!=null&&bosdate.trim().length()>0)
			{
				countlist=gzbo.getRfCountList(bosdate);
				if(countlist.size()>0)
					count=((CommonData)countlist.get(countlist.size()-1)).getDataValue();	
			}
			
	//		else
	//			throw new Exception("无数据支持重发操作！");
			this.getFormHM().put("bosdate", bosdate);
			this.getFormHM().put("count", count);
			this.getFormHM().put("datelist",datelist);
			this.getFormHM().put("countlist", countlist);
		}
		catch(Exception e)
		{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		}
	}

}

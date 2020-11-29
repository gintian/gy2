package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:报表汇总校验</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 10, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ReportCollectValidateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid");
			
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			
			int    rows=Integer.parseInt((String)this.getFormHM().get("rows"));
			int    cols=Integer.parseInt((String)this.getFormHM().get("cols"));
			ArrayList results=(ArrayList)this.getFormHM().get("results");
			int    operateObject=Integer.parseInt((String)this.getFormHM().get("operateObject"));
			String unitcode=(String)this.getFormHM().get("unitcode");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			double[][] value=new double[rows][cols];
			for(int i=0;i<rows;i++)
			{
				String temp=(String)results.get(i);
				String[] temp_arr=temp.split("/");
				for(int j=0;j<cols;j++)
				{
					value[i][j]=Double.parseDouble(temp_arr[j]);
				}
			}
			if(operateObject==1)
			{
				this.frowset=dao.search("select unitcode from operuser where username='"+userView.getUserName()+"'");
				if(this.frowset.next())
					unitcode=this.frowset.getString("unitcode");
			}
			ReportCollectBo bo=new ReportCollectBo(this.frameconn);
			String info=bo.compareChildData3(unitcode,tabid,value);
			this.getFormHM().put("info",SafeCode.encode(info));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}

package com.hjsj.hrms.transaction.gz.gz_analyse.historydata;

import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:SetSalaryIdsTrans.java</p>
 * <p>Description>:SetSalaryIdsTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jun 18, 2009 2:12:40 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: fanzhiguo
 */
public class SetSalaryIdsTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
    	try
		{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String salaryids = (String)hm.get("salaryids");
			String[] salaryids2 = salaryids.split("@");
			//如果用户没有当前薪资类别的资源权限   20141008  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			for (int i = 0; i < salaryids2.length; i++)
			{
			    String salaryid = salaryids2[i];
			    if(salaryid!=null&&salaryid.trim().length()>0)
			    { 
					safeBo.isSalarySetResource(salaryid,null);
			    }
			}
			
			this.getFormHM().put("salaryids", salaryids2);
			HistoryDataBo bo = new HistoryDataBo(this.getFrameconn(),"");
			bo.syncSalaryarchiveStrut();//浏览的时候同步归档表 zhaoxg add 2013-12-7
			//bo.syncSalaryTaxArchiveStrut();
			
			/**=0按管理范围控制=1按操作单位控制=3按模块操作单位控制*/
			String viewUnit="0";
			if(this.getUserView().getUnit_id()!=null&&this.getUserView().getUnit_id().trim().length()>0&&!"UN".equalsIgnoreCase(this.getUserView().getUnit_id()))
			{
				viewUnit="1";
			}
			
			String clientName = SystemConfig.getPropertyValue("clientName");
			if(clientName==null||!"weichai".equalsIgnoreCase(clientName))
			{
				if(this.userView.getUnitIdByBusi("1")!=null&&this.userView.getUnitIdByBusi("1").length()>0&&!"UN".equalsIgnoreCase(this.userView.getUnitIdByBusi("1")))
				{
					viewUnit="3";
				}
			}
			this.getFormHM().put("viewUnit",viewUnit);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }

}

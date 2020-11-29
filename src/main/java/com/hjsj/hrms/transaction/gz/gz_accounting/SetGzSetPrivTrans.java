package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 17, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SetGzSetPrivTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)this.getFormHM().get("salaryid");
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
			String priv=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String gz_module=(String)this.getFormHM().get("gz_module");
		 
			 
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,gz_module);
			
			if(hm.get("ff_bosdate")==null||((String)hm.get("ff_bosdate")).trim().length()==0)
			{
				this.getFormHM().put("ff_bosdate", "");
				this.getFormHM().put("ff_count", "");
			}
			else
			{
				this.getFormHM().put("ff_bosdate", "业务日期:"+(String)hm.get("ff_bosdate"));
				this.getFormHM().put("ff_count",(String)hm.get("ff_count")+"次");
				hm.remove("ff_bosdate");
				hm.remove("ff_count");
			}
			
			
			if(priv.length()==0)
				priv="0";
		//	if(manager!=null&&manager.length()>0&&manager.equalsIgnoreCase(this.userView.getUserName()))  //影响树按权限展示  bug:0020234
		//		priv="0";
			
			if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName()))
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select * from gz_extend_log where salaryid="+salaryid+"  and username='"+manager+"'");
				if(this.frowset.next())
				{
					
				}
				else
				{
					
					if("1".equals(gz_module))
						throw GeneralExceptionHandler.Handle(new Exception("该保险类别的管理员还没有建立保险表!"));
					else
						throw GeneralExceptionHandler.Handle(new Exception("该薪资类别的管理员还没有建立薪资表!"));
				}
			}
			
			this.getFormHM().put("priv",priv);
			this.getFormHM().put("itemid", "all");
			
//			returnFlag 0：返回薪资发放的类别界面 1：返回部门月奖金界面
			HashMap requestPamaHM = (HashMap)this.getFormHM().get("requestPamaHM");
			String returnFlag=(String)requestPamaHM.get("returnFlag");
			requestPamaHM.remove("returnFlag");
			returnFlag=returnFlag==null?"0":returnFlag;
				
			String year="0000";
			String month ="00";
			String operOrg ="00";
			if("1".equals(returnFlag))
			{
			    year  = (String)requestPamaHM.get("theyear");
			    month  = (String)requestPamaHM.get("themonth");
			    operOrg = (String)requestPamaHM.get("operOrg");
			    requestPamaHM.remove("theyear");
			    requestPamaHM.remove("themonth");
			    requestPamaHM.remove("operOrg");			   	
			}			
			
			
			{
				String flag="0";
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
				orgid = orgid != null ? orgid : "";
				String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
				deptid = deptid != null ? deptid : "";
				if(manager!=null&&manager.trim().length()>0)
				{
					if(!this.userView.getUserName().equalsIgnoreCase(manager))
					{
						if(orgid.length()>0||deptid.length()>0)
							flag="1";
					}
				}
				this.getFormHM().put("showUnitCodeTree",flag); 
			}
			
			
			
			
			this.getFormHM().put("returnFlag",returnFlag);
			this.getFormHM().put("theyear",year);	
			this.getFormHM().put("themonth",month);
			this.getFormHM().put("operOrg",operOrg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}

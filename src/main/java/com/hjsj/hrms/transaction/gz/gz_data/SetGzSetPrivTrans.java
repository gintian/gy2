package com.hjsj.hrms.transaction.gz.gz_data;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2009-9-10:下午02:43:36
 * </p>
 * 
 * @author fanzhiguo
 * @version 4.0
 */
public class SetGzSetPrivTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

	try
	{
	    HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	    String salaryid = (String) this.getFormHM().get("salaryid");
	    SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.getFrameconn(), Integer.parseInt(salaryid));
	    String priv = ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE, "flag"); // 人员范围权限过滤标志
                                                                                        // 1：有
	    String manager = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");

	    if (priv.length() == 0)
		priv = "0";
	    if (manager != null && manager.length() > 0 && manager.equalsIgnoreCase(this.userView.getUserName()))
		priv = "0";

	    if (manager != null && manager.length() > 0 && !manager.equalsIgnoreCase(this.userView.getUserName()))
	    {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		this.frowset = dao.search("select * from gz_extend_log where salaryid=" + salaryid + "  and username='" + manager + "'");
		if (this.frowset.next())
		{

		} else
		    throw GeneralExceptionHandler.Handle(new Exception("该薪资类别的管理员还没有建立薪资表!"));

	    }

	    this.getFormHM().put("priv", priv);
	    this.getFormHM().put("itemid", "all");
	    
	    
	    
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
	    
	    
	    
	    
	    

	    // returnFlag 0：返回薪资发放的类别界面 1：返回部门月奖金界面
	    HashMap requestPamaHM = (HashMap) this.getFormHM().get("requestPamaHM");
	    String returnFlag = (String) requestPamaHM.get("returnFlag");
	    requestPamaHM.remove("returnFlag");
	    returnFlag = returnFlag == null ? "0" : returnFlag;	 

	    String year = "0000";
	    String month = "00";
	    String operOrg = "00";
	    String isleafOrg = "0";
	    String isAllDistri = "0";
	    String isOnlyLeafOrgs = "0";
	    String isOrgCheckNo = "0";
	    if ("1".equals(returnFlag))
	    {
		year = (String) requestPamaHM.get("theyear");
		month = (String) requestPamaHM.get("themonth");
		operOrg = (String) requestPamaHM.get("orgcode");
		isleafOrg = (String) requestPamaHM.get("isleafOrg");
		isAllDistri = (String) requestPamaHM.get("isAllDistri");
		isOnlyLeafOrgs = (String) requestPamaHM.get("isOnlyLeafOrgs");
		isOrgCheckNo = (String)requestPamaHM.get("isOrgCheckNo");
		
		requestPamaHM.remove("theyear");
		requestPamaHM.remove("themonth");
		requestPamaHM.remove("orgcode");
		requestPamaHM.remove("isleafOrg");
		requestPamaHM.remove("isAllDistri");
		requestPamaHM.remove("isOnlyLeafOrgs");
	    }
	    this.getFormHM().put("theyear", year);
	    this.getFormHM().put("themonth", month);
	    this.getFormHM().put("operOrg", operOrg);
	    this.getFormHM().put("isLeafOrg", isleafOrg);
	    this.getFormHM().put("isAllDistri", isAllDistri);
	    this.getFormHM().put("returnFlag", returnFlag);
	    this.getFormHM().put("isOnlyLeafOrgs", isOnlyLeafOrgs);
	    this.getFormHM().put("isOrgCheckNo", isOrgCheckNo);

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
    }

}

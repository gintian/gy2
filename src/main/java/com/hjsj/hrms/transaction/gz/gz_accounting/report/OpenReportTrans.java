package com.hjsj.hrms.transaction.gz.gz_accounting.report;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class OpenReportTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String privCode = this.userView.getManagePrivCode();
			String privCodeValue =this.userView.getManagePrivCodeValue();
			String gzmodel = (String) this.userView.getHm().get("gzmodel");//薪资发放 0；历史数据分析 3
			if("3".equals(gzmodel)){
				privCode = "";
				privCodeValue = "-1";
			}
			/**如果存在操作单位，则安操作单位走 （首开提 2011-04-11）*/
			if(this.userView.getUnit_id()!=null&&this.userView.getUnit_id().trim().length()>=3)
			{
				privCode = "CZDW";
				privCodeValue=this.userView.getUnit_id();
			}
			String userName=this.userView.getUserName();
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)this.getFormHM().get("salaryid");
			if(salaryid==null||salaryid.length()==0){
				salaryid = (String) map.get("salaryid");
			}
			String role = "0";
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
	        String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
	        /**不是共享类别，或者当前用户是管理员*/
			if((manager==null||manager.length()==0||userName.equals(manager))) 
			{	
				priv_mode="0";
			}
			else
			{
				userName=manager;
				priv_mode="1";
			}
			if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			{
				role = "1";
			}
			ArrayList privList = this.userView.getPrivDbList();
			StringBuffer privdb = new StringBuffer("");
			if(privList != null&&privList.size()>0)
			{
				for(int i =0;i<privList.size();i++)
				{
					privdb.append(privList.get(i).toString().toUpperCase());
					privdb.append("#");
				}
				privdb.setLength(privdb.length()-1);
			}
			
			String address=(String)map.get("pt");
			address = PubFunc.hireKeyWord_filter_reback(address);
			this.getFormHM().put("priv_mode",priv_mode);
			this.getFormHM().put("manageUserName", userName);
			this.getFormHM().put("privDb",privdb.toString());
			this.getFormHM().put("role", role);
			this.getFormHM().put("privCode",privCode==null?"":privCode);
			this.getFormHM().put("privCodeValue",privCodeValue==null?"":privCodeValue);
			this.getFormHM().put("address",address);
			
			this.getFormHM().put("salaryid", salaryid);
			String model=(String)this.getFormHM().get("model");
			if(model==null||model.length()==0){
				model = (String) map.get("model");
			}
			this.getFormHM().put("model", model);
			String bosdate=(String)this.getFormHM().get("bosdate");
			if(bosdate==null||bosdate.length()==0){
				bosdate = (String) map.get("bosdate");
			}
			this.getFormHM().put("bosdate", bosdate);
			String boscount=(String)this.getFormHM().get("boscount");
			if(boscount==null||boscount.length()==0){
				boscount = (String) map.get("boscount");
			}
			this.getFormHM().put("boscount", boscount);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}

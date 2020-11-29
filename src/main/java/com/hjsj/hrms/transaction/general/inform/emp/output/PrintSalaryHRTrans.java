package com.hjsj.hrms.transaction.general.inform.emp.output;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class PrintSalaryHRTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String gz_module=(String)hm.get("gz_module");
		String privSet = "";
		if(!this.userView.isSuper_admin())
		{
			if("1".equals(gz_module))
				privSet = ","+this.userView.getResourceString(IResourceConstant.INS_SET)+",";
			else
				privSet = ","+this.userView.getResourceString(IResourceConstant.GZ_SET)+",";
		}
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		hm.remove("salaryid");
		
		String tabid = (String)hm.get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		hm.remove("tabid");
		
		String a_code = (String)hm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		
		String condid = (String)hm.get("condid");
		condid=condid!=null&&condid.trim().length()>0?condid:"";
		condid= "new".equalsIgnoreCase(condid)?"":condid;
		
		String model=(String)hm.get("model");
		model=model!=null&&model.trim().length()>0?model:"1";
		
		String bosdate=(String)hm.get("bosdate");
		bosdate=bosdate!=null&&bosdate.trim().length()>0?bosdate:"2008.10.10";
		
		String boscount=(String)hm.get("boscount");
		boscount=boscount!=null&&boscount.trim().length()>0?boscount:"1";
		
		String filterWhl = (String) this.userView.getHm().get("gz_filterWhl");
		filterWhl=filterWhl!=null&&filterWhl.trim().length()>0?filterWhl:"";
		filterWhl = SafeCode.decode(filterWhl);
		hm.remove("filterWhl");

		SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.getFrameconn(), Integer.parseInt(salaryid));
		String priv = ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag"); // 人员范围权限过滤标志 1：有
		String manager = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET,"user");
		if(priv.length()==0)
			priv="0";
		if(manager!=null&&manager.length()>0&&manager.equalsIgnoreCase(this.userView.getUserName()))
			priv="0";

		if("0".equals(priv)){
			a_code="UN";
		}
		String showUnitCodeTree="0";  //是否按操作单位来显示树
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		showUnitCodeTree=gzbo.getControlByUnitcode();
		int ver=this.userView.getVersion(); //锁版本校验 
		if(ver>=70){//新版薪资进入报表 不传condid 改为subModuleId
			this.getFormHM().put("subModuleId",(String)hm.get("subModuleId") );
			hm.remove("subModuleId");
		}
		this.getFormHM().put("showUnitCodeTree", showUnitCodeTree);
		this.getFormHM().put("salaryid",salaryid);
		this.getFormHM().put("tabid",tabid);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("condid",condid);
		this.getFormHM().put("priv_mode",priv);
		this.getFormHM().put("filterWhl",filterWhl);
		this.getFormHM().put("model",model);
		this.getFormHM().put("bosdate",bosdate);
		this.getFormHM().put("boscount",boscount);
		this.getFormHM().put("privSet", privSet);
	}
	

}

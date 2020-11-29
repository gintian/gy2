package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.KqEmpMonthDataBo;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class AuditCollectDataTrans extends IBusiness {


	public void execute() throws GeneralException {
		String kind = (String) this.getFormHM().get("kind");
		String code = (String) this.getFormHM().get("code");
		if (kind == null || kind.length() <= 0) 
		{
			String a_code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
			if(a_code==null||a_code.length()<=0)
			{
				String privcode=RegisterInitInfoData.getKqPrivCode(userView);
				if("UN".equalsIgnoreCase(privcode))
					kind="2";
				else if("UM".equalsIgnoreCase(privcode))
					kind="1";
				else if("@K".equalsIgnoreCase(privcode))
					kind="0";
				code=RegisterInitInfoData.getKqPrivCodeValue(userView);
			}else
			{
				if(a_code.indexOf("UN")!=-1)
				{
					kind="2";
				}else if(a_code.indexOf("UM")!=-1)
				{
					kind="1";
				}else if(a_code.indexOf("@K")!=-1)
				{
					kind="0";
				}
				code=a_code.substring(2);
			}
		}
		if(code == null)
			code = "";
        KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		ArrayList dblist=kqUtilsClass.setKqPerList(code,kind);	        
        KqEmpMonthDataBo bo = new KqEmpMonthDataBo(this.getFrameconn(),this.userView);
        String ids=bo.getFormula();
        String msg="";//0：没有指定审核公式	no：审核通过	  否则 审核不通过 导出审核报告
        if(ids==null|| "".equals(ids))
        {
        	msg="0";
        	this.getFormHM().put("msg",msg);
        	return;
        }
        
        HashMap  hm = bo.auditEmpMonthData(ids,code,kind,dblist);
        String fileName = (String)hm.get("fileName");
        this.getFormHM().put("fileName", fileName);
        msg = (String)hm.get("msg");
        this.getFormHM().put("msg",msg);
	}
	

	
}

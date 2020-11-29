package com.hjsj.hrms.transaction.standarduty;

import com.hjsj.hrms.businessobject.standarduty.DutyXmlBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

public class SetItemOption extends IBusiness{

	public void execute() throws GeneralException {
		try{
			  HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			  
			  String flag=hm.get("b_search").toString();
			  String submit=hm.get("submit").toString();
				  this.getFormHM().put("submitflag", submit);
			  
			  if(!"reload".equalsIgnoreCase(flag)){
				 RecordVo option_vo=ConstantParamter.getConstantVo("POS_STANDARD");
				 
				 DutyXmlBo dxb=new DutyXmlBo(this.frameconn);
				 String xml="";
					 if(option_vo!=null)
						  xml=option_vo.getString("str_value");
				    Map relevantset=dxb.getRelevantsetMap(xml);//取得基准岗位指标集对应岗位指标集的信息
				 
				    this.getFormHM().put("sduty", dxb.getSduty());//sduty 代表基准岗位
				    this.getFormHM().put("duty", dxb.getDuty());  // duty 代表实际岗位
				    Map sdu=dxb.getSdutyitem();
				    Map du=dxb.getDutyitem();
				    this.getFormHM().put("sdutyitem", sdu);
				    this.getFormHM().put("dutyitem",du);
				    this.getFormHM().put("relevantset", relevantset);
				    this.getFormHM().put("targetids", dxb.getSduty().clone());
			  }
				    
				       
		}catch (Exception ee)
	       {
	         ee.printStackTrace();
	         GeneralExceptionHandler.Handle(ee);
	       }
	}

}
